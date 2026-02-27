import { Component, signal } from '@angular/core';
import { DeviceService } from '../../services/device';

// We define the shape of our device data
export interface Device {
  id: string;
  name: string;
}

@Component({
  selector: 'app-sidebar',
  imports: [],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  isDropdownOpen = signal(false);

  constructor(public deviceService: DeviceService) {}

  toggleDropdown() {
    this.isDropdownOpen.update((isOpen) => !isOpen);
    if (!this.isDropdownOpen()) {
      // Clear the selection globally if the dropdown is closed
      this.deviceService.selectedDeviceName.set(null);
      this.deviceService.selectedDeviceSummary.set(null);
    }
  }

  selectDevice(deviceName: string) {
    // GUARD 1: Prevent clicking if this device is already selected
    if (this.deviceService.selectedDeviceName() === deviceName) {
      return; 
    }

    // GUARD 2: Prevent clicking ANY device if a fetch is currently in progress
    if (this.deviceService.loadingDeviceName() !== null) {
      return; 
    }

    // Tell the service to fetch the data!
    this.deviceService.fetchDeviceSummary(deviceName);
  }
}
