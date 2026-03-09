import { Component, signal } from '@angular/core';
import { DeviceService } from '../../services/device';

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
    // Prevent clicking if this device is already selected
    if (this.deviceService.selectedDeviceName() === deviceName) {
      return; 
    }

    // Prevent clicking any device if a fetch is currently in progress
    if (this.deviceService.loadingDeviceName() !== null) {
      return; 
    }

    // Fetching device summary of the selected device
    this.deviceService.fetchDeviceSummary(deviceName);
  }
}
