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
  selectedDeviceId = signal<string | null>(null);

  constructor(public deviceService: DeviceService) {}

  // // Mock data matching your wireframe
  // devices = signal<Device[]>([
  //   { id: '1', name: 'Device-1' },
  //   { id: '2', name: 'Device-2' },
  //   { id: '3', name: 'Device-3' },
  //   { id: '4', name: 'Device-4' },
  //   { id: '5', name: 'Device-5' },
  //   { id: '6', name: 'Device-6' },
  //   { id: '7', name: 'Device-7' },
  // ]);

  // devices = signal<Device[]>([]);

  toggleDropdown() {
    this.isDropdownOpen.update((isOpen) => !isOpen);
    if (!this.isDropdownOpen()) {
      this.selectedDeviceId.set(null);
    }
  }

  selectDevice(id: string) {
    this.selectedDeviceId.set(id);
    // Later, we will use a Service here to tell the Main Content which device to load!
  }
}
