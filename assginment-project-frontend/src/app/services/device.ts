// src/app/services/device.service.ts

import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Device, GlobalDeviceSummary,  } from '../models/device-summary.model';

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
}
