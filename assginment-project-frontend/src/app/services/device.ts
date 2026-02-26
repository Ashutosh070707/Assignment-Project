// src/app/services/device.service.ts

import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
        console.log('Successfully fetched all devices:', fetchedDevices);
      },
      error: (error) => {
        console.error('Failed to fetch devices:', error);
      }
    });
  }
}