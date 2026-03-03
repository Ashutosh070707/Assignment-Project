// src/app/services/device.service.ts

import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Device, GlobalDeviceSummary, ShelfPosition,  } from '../models/device-summary.model';

@Injectable({
  providedIn: 'root' // This is what makes it "global"
})

export class DeviceService {
  
  // ==========================================
  // GLOBAL STATE (Signals)
  // ==========================================
  
  // 1. Global Array: Holds the lightweight list of all devices
  devices = signal<Device[]>([]);
  // 2. Global Object: Holds the detailed summary of the ONE clicked device
  selectedDeviceSummary = signal<GlobalDeviceSummary | null>(null);

  // NEW: Track selection and loading states globally
  selectedDeviceName = signal<string | null>(null);
  loadingDeviceName = signal<string | null>(null);

  // ==========================================

  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  /**
   * Fetches the initial list of all devices from the Spring Boot backend.
   * Call this exactly once when the application starts.
   */
  loadInitialDevices() {
    this.http.get<Device[]>(`${this.apiUrl}/api/devices/allDevices`).subscribe({
      next: (fetchedDevices) => {
        // Update the global array signal with the data from the database
        this.devices.set(fetchedDevices);
        // console.log('Successfully fetched all devices:', fetchedDevices);
      },
      error: (error) => {
        console.error('Failed to fetch devices:', error);
      }
    });
  }


  // NEW: Fetch summary for a specific device
  fetchDeviceSummary(deviceName: string) {
    // 1. Start the loader for this specific device
    this.loadingDeviceName.set(deviceName);

    // 2. Make the API call (Replace '/deviceSummary/' with your actual backend endpoint)
    this.http.get<GlobalDeviceSummary>(`${this.apiUrl}/api/devices/deviceDetails/${deviceName}`).subscribe({
      next: (summaryData) => {

        // console.log(summaryData);

        // 3. When data arrives, populate the summary object
        this.selectedDeviceSummary.set(summaryData);
        
        // 4. Set it as the officially selected device (turns it active in the UI)
        this.selectedDeviceName.set(deviceName);
        
        // 5. Turn off the loader
        this.loadingDeviceName.set(null);
      },
      error: (err) => {
        console.error('Failed to fetch device summary:', err);
        // Turn off the loader even if it fails, so it doesn't spin forever
        this.loadingDeviceName.set(null);
      }
    });
  }


  addDevice(newDeviceData: any) {
    return this.http.post<Device>(`${this.apiUrl}/api/devices/`, newDeviceData).pipe(
      tap((savedDevice) => {
        // 'tap' lets the service secretly update the global array when the request succeeds
        this.devices.update(currentDevices => [...currentDevices, savedDevice]);
      })
    );
  }

  // ==========================================
  // ADD SHELF
  // ==========================================

  addShelf(newShelfData: any) {
    // Replace this URL with your actual Spring Boot endpoint for adding a shelf
    return this.http.post<any>(`${this.apiUrl}/api/shelves/createAndAttach`, newShelfData).pipe(
      tap((savedShelf) => {
        
        const currentSummary = this.selectedDeviceSummary();
        
        if (currentSummary && currentSummary.shelfPairs) {
          
          // Map through the existing pairs
          const updatedPairs = currentSummary.shelfPairs.map(pair => {
            // Find the specific empty slot we just added a shelf to
            if (pair.shelfPosition && pair.shelfPosition.id === newShelfData.shelfPositionId) {
              // Populate the 'shelf' side of this pair with the new data
              return { ...pair, shelf: savedShelf };
            }
            return pair; // Leave all other slots alone
          });

          // Instantly update the UI
          this.selectedDeviceSummary.set({
            ...currentSummary,
            shelfPairs: updatedPairs
          });
        }
      })
    );
  }

  // ==========================================
  // ADD SHELF POSITION
  // ==========================================

  addShelfPosition(newPositionData: any) {
    // Replace the URL with your actual Spring Boot controller endpoint for creating a shelf position
    return this.http.post<ShelfPosition>(`${this.apiUrl}/api/shelfPositions/`, newPositionData).pipe(
      tap((savedPosition: ShelfPosition) => {
        
        // 1. Get the current state of the UI
        const currentSummary = this.selectedDeviceSummary();

        if (currentSummary) {
          
          // 2. Construct the new pair. 
          // Since it's a brand new position, there is no physical shelf in it yet.
          const newShelfPair = {
            shelfPosition: savedPosition,
            shelf: null 
          };

          // 3. Update the global signal immutably
          this.selectedDeviceSummary.set({
            ...currentSummary,
            
            // Optionally update the device's total count so the UI stays perfectly in sync
            device: {
              ...currentSummary.device,
              numberOfShelfPositions: currentSummary.device.numberOfShelfPositions + 1
            },
            
            // Append the new pair to the end of the existing array
            shelfPairs: [...currentSummary.shelfPairs, newShelfPair]
          });
        }
      })
    );
  }

  updateDevice(deviceId: string, updatedData: any) {
    return this.http.put<Device>(`${this.apiUrl}/api/devices/update/${deviceId}`, updatedData).pipe(
      tap((updatedDevice) => {
        // Update Sidebar list
        this.devices.update(currentDevices => 
          currentDevices.map(d => d.id === deviceId ? { ...d, ...updatedDevice } : d)
        );
        // Update Device Summary screen
        const currentSummary = this.selectedDeviceSummary();
        if (currentSummary) {
          this.selectedDeviceSummary.set({
            ...currentSummary,
            device: { ...currentSummary.device, ...updatedDevice }
          });
        }
      })
    );
  }

// ==========================================
  // DELETE METHODS
  // ==========================================

  deleteDevice(deviceName: string) {
    // 1. Add { responseType: 'text' } so Angular accepts the plain string
    return this.http.delete(`${this.apiUrl}/api/devices/${deviceName}`, { responseType: 'text' }).pipe(
      tap(() => {
        // 2. Remove the deleted device from the global array
        this.devices.update(currentDevices => currentDevices.filter(d => d.deviceName !== deviceName));
        
        // 3. Compare deviceName to deviceName (not device.id)
        if (this.selectedDeviceSummary()?.device?.deviceName === deviceName) {
          this.selectedDeviceSummary.set(null);
          this.selectedDeviceName.set(null);
        }
      })
    );
  }

  // ==========================================
  // DELETE SHELF
  // ==========================================

  deleteShelf(shelfName: string) {
    // Note: Adjust this URL to exactly match your Spring Boot @DeleteMapping for shelves
    const endpointUrl = `${this.apiUrl}/api/shelves/${shelfName}`;

    return this.http.delete(endpointUrl, { responseType: 'text' }).pipe(
      tap(() => {
        const currentSummary = this.selectedDeviceSummary();
        
        if (currentSummary && currentSummary.shelfPairs) {
          
          // Map over the pairs and "empty" the slot that had this shelf
          const updatedPairs = currentSummary.shelfPairs.map(pair => {
            if (pair.shelf && pair.shelf.shelfName === shelfName) {
              // Keep the shelfPosition exactly as it is, but set shelf to null
              return { ...pair, shelf: null };
            }
            return pair; // Leave all other pairs alone
          });

          // Instantly update the UI
          this.selectedDeviceSummary.set({
            ...currentSummary,
            shelfPairs: updatedPairs
          });
        }
      })
    );
  }

  // ==========================================
  // DELETE SHELF POSITION
  // ==========================================

  deleteShelfPosition(positionId: string) {
    // 1. Grab the current summary to extract the deviceName
    const currentSummary = this.selectedDeviceSummary();
    const deviceName = currentSummary?.device?.deviceName;

    // Assuming your controller base URL maps to /api/shelf-positions. Adjust if needed!
    const endpointUrl = `${this.apiUrl}/api/shelfPositions/${deviceName}/${positionId}`;

    // 2. Add { responseType: 'text' } so Spring Boot's plain string response doesn't crash Angular
    return this.http.delete(endpointUrl, { responseType: 'text' }).pipe(
      tap(() => {
        
        if (currentSummary && currentSummary.shelfPairs) {
          // 3. Filter out the deleted pair
          const updatedPairs = currentSummary.shelfPairs.filter(pair => 
            !(pair.shelfPosition && pair.shelfPosition.id === positionId)
          );

          // 4. Update the UI instantly
          this.selectedDeviceSummary.set({
            ...currentSummary,
            device: {
              ...currentSummary.device,
              // Keep the count accurate!
              numberOfShelfPositions: Math.max(0, currentSummary.device.numberOfShelfPositions - 1)
            },
            shelfPairs: updatedPairs
          });
        }
      })
    );
  }
}
