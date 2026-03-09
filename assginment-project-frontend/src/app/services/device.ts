import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { Device, GlobalDeviceSummary, ShelfPosition } from '../models/device-summary.model';

@Injectable({
  providedIn: 'root', // This is what makes it "global"
})

export class DeviceService {
  // Contains all the devices
  devices = signal<Device[]>([]);

  // Contains the device summary of selected device
  selectedDeviceSummary = signal<GlobalDeviceSummary | null>(null);

  // Contains device name of selected device
  selectedDeviceName = signal<string | null>(null);

  // Contains device name of loading device
  loadingDeviceName = signal<string | null>(null);

  private apiUrl: string = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  loadInitialDevices() {
    this.http.get<Device[]>(`${this.apiUrl}/api/devices/allDevices`).subscribe({
      next: (fetchedDevices) => {
        this.devices.set(fetchedDevices);
      },
      error: (error) => {
        console.error('Failed to fetch devices:', error);
      },
    });
  }

  fetchDeviceSummary(deviceName: string) {
    this.loadingDeviceName.set(deviceName);

    this.http
      .get<GlobalDeviceSummary>(`${this.apiUrl}/api/devices/deviceDetails/${deviceName}`)
      .subscribe({
        next: (summaryData) => {
          this.selectedDeviceSummary.set(summaryData);
          this.selectedDeviceName.set(deviceName);
          this.loadingDeviceName.set(null);
        },
        error: (err) => {
          console.error('Failed to fetch device summary:', err);
          this.loadingDeviceName.set(null);
        },
      });
  }

  addDevice(newDeviceData: any) {
    return this.http.post<Device>(`${this.apiUrl}/api/devices/`, newDeviceData).pipe(
      tap((savedDevice) => {
        this.devices.update((currentDevices) => [...currentDevices, savedDevice]);
      }),
    );
  }

  addShelf(newShelfData: any) {
    return this.http.post<any>(`${this.apiUrl}/api/shelves/createAndAttach`, newShelfData).pipe(
      tap((savedShelf) => {
        const currentSummary = this.selectedDeviceSummary();

        if (currentSummary && currentSummary.shelfPairs) {
          // Map through the existing pairs
          const updatedPairs = currentSummary.shelfPairs.map((pair) => {
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
            shelfPairs: updatedPairs,
          });
        }
      }),
    );
  }

  addShelfPosition(newPositionData: any) {
    return this.http
      .post<ShelfPosition>(`${this.apiUrl}/api/shelfPositions/`, newPositionData)
      .pipe(
        tap((savedPosition: ShelfPosition) => {
          const currentSummary = this.selectedDeviceSummary();

          if (currentSummary) {
            const newShelfPair = {
              shelfPosition: savedPosition,
              shelf: null,
            };

            this.selectedDeviceSummary.set({
              ...currentSummary,
              device: {
                ...currentSummary.device,
                numberOfShelfPositions: currentSummary.device.numberOfShelfPositions + 1,
              },
              shelfPairs: [...currentSummary.shelfPairs, newShelfPair],
            });
          }
        }),
      );
  }

  updateDevice(deviceId: string, updatedData: any) {
    // FIX 1: Removed /${deviceId} from the URL to perfectly match your Spring Boot @PutMapping("/update")
    const endpointUrl = `${this.apiUrl}/api/devices/update`;

    return this.http.put<any>(endpointUrl, updatedData).pipe(
      tap((updatedDevice) => {
        // Update Sidebar list
        this.devices.update((currentDevices) =>
          currentDevices.map((d) => (d.id === deviceId ? { ...d, ...updatedDevice } : d)),
        );

        // Update Device Summary screen
        const currentSummary = this.selectedDeviceSummary();

        // FIX 2: Added a quick safety check to ensure we only update the summary if the IDs actually match
        if (currentSummary && currentSummary.device.id === deviceId) {
          this.selectedDeviceSummary.set({
            ...currentSummary,
            device: { ...currentSummary.device, ...updatedDevice },
          });
        }
      }),
    );
  }

  checkDeviceNameValidity(deviceName: string) {
    // Modify URL to match the exact controller endpoint you wrote
    return this.http.get(`${this.apiUrl}/api/devices/check/${deviceName}`, {
      responseType: 'text',
    });
  }

  checkShelfNameValidity(shelfName: string) {
    return this.http.get(`${this.apiUrl}/api/shelves/check/${shelfName}`, { responseType: 'text' });
  }

  updateShelf(shelfId: string, formValues: any) {
    const currentSummary = this.selectedDeviceSummary();
    let oldShelfName = '';

    // 1. Dig into our local state to find the original shelf name before it was changed
    if (currentSummary && currentSummary.shelfPairs) {
      const targetPair = currentSummary.shelfPairs.find((pair) => pair.shelf?.id === shelfId);
      if (targetPair && targetPair.shelf) {
        oldShelfName = targetPair.shelf.shelfName;
      }
    }

    // 2. Build the payload to match your UpdateShelf DTO exactly!
    const payload = {
      id: shelfId,
      previousShelfName: oldShelfName,
      newShelfName: formValues.shelfName,
      newPartNumber: formValues.partNumber,
    };

    // 3. Send the PUT request (Adjust the base URL if your controller has a class-level mapping)
    return this.http.put<any>(`${this.apiUrl}/api/shelves/update`, payload).pipe(
      tap((updatedShelfResponse) => {
        // 4. Instantly update the UI with the response from the backend
        if (currentSummary && currentSummary.shelfPairs) {
          const updatedPairs = currentSummary.shelfPairs.map((pair) => {
            if (pair.shelf && pair.shelf.id === shelfId) {
              // Swap out the old shelf object for the fresh one returned by Spring Boot
              return { ...pair, shelf: updatedShelfResponse };
            }
            return pair;
          });

          this.selectedDeviceSummary.set({
            ...currentSummary,
            shelfPairs: updatedPairs,
          });
        }
      }),
    );
  }

  deleteDevice(deviceName: string) {
    return this.http
      .delete(`${this.apiUrl}/api/devices/${deviceName}`, { responseType: 'text' })
      .pipe(
        tap(() => {
          this.devices.update((currentDevices) =>
            currentDevices.filter((d) => d.deviceName !== deviceName),
          );
          if (this.selectedDeviceSummary()?.device?.deviceName === deviceName) {
            this.selectedDeviceSummary.set(null);
            this.selectedDeviceName.set(null);
          }
        }),
      );
  }

  deleteShelfPosition(positionId: string) {
    const currentSummary = this.selectedDeviceSummary();
    const deviceName = currentSummary?.device?.deviceName;

    return this.http
      .delete(`${this.apiUrl}/api/shelfPositions/${deviceName}/${positionId}`, {
        responseType: 'text',
      })
      .pipe(
        tap(() => {
          this.selectedDeviceSummary.update((currentState) => {
            if (!currentState || !currentState.shelfPairs) return currentState;

            const updatedPairs = currentState.shelfPairs.filter(
              (pair) => pair.shelfPosition?.id !== positionId,
            );

            return {
              ...currentState,
              device: {
                ...currentState.device,
                numberOfShelfPositions: Math.max(0, currentState.device.numberOfShelfPositions - 1),
              },
              shelfPairs: updatedPairs,
            };
          });
        }),
      );
  }

  deleteShelf(shelfName: string) {
    return this.http.delete(`${this.apiUrl}/api/shelves/${shelfName}`, { responseType: 'text' }).pipe(
      tap(() => {
        const currentSummary = this.selectedDeviceSummary();

        if (currentSummary && currentSummary.shelfPairs) {
          const updatedPairs = currentSummary.shelfPairs.map((pair) => {
            if (pair.shelf && pair.shelf.shelfName === shelfName) {
              return { ...pair, shelf: null };
            }
            return pair;
          });
          this.selectedDeviceSummary.set({
            ...currentSummary,
            shelfPairs: updatedPairs,
          });
        }
      }),
    );
  }
}
