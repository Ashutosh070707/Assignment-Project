import { Component, signal } from '@angular/core';

export interface Device {
  id: string;
  deviceId: string;
  deviceName: string;
  partNumber: string;
  deviceType: string;
  buildingName: string;
  noOfShelfPositions: number;
}

export interface ShelfPosition {
  id: string;
  deviceId: string;
}

export interface Shelf {
  id: string;
  shelfName: string;
  partNumber: string;
}

export interface deviceSummary {
  device: Device;
  shelfPositions: ShelfPosition[];
  shelves: Shelf[];
}

@Component({
  selector: 'app-device-summary',
  imports: [],
  templateUrl: './device-summary.html',
  styleUrl: './device-summary.css',
})
export class DeviceSummary {
  isDeviceInfoOpen = signal(true);
  isShelfPositionsOpen = signal(false); // Let's default to true right now so you can see it!
  isShelvesOpen = signal(false);
  isSettingOpen = signal(false);

  deviceSummary = signal({
    device: {
      id: 'd-8f7b2c99',
      deviceId: 'OPT-SW-001',
      deviceName: 'Optical-Aggregator-East',
      partNumber: 'CN-6500-CHAS',
      deviceType: 'Optical Switch',
      buildingName: 'Main-DC-Gurugram',
      noOfShelfPositions: 3,
    },

    shelfPositions: [
      {
        id: 'sp-01',
        deviceId: 'OPT-SW-001',
      },
      {
        id: 'sp-02',
        deviceId: 'OPT-SW-001',
      },
      {
        id: 'sp-03',
        deviceId: 'OPT-SW-001',
      },
    ],

    shelves: [
      {
        id: 'sh-01',
        shelfName: 'Control-Processor-Shelf',
        partNumber: 'SH-CP-100',
      },
      {
        id: 'sh-02',
        shelfName: 'Line-Card-Shelf-A',
        partNumber: 'SH-LC-200',
      },
      {
        id: 'sh-03',
        shelfName: 'Power-Distribution-Shelf',
        partNumber: 'SH-PWR-50',
      },
    ],
  });

  toggleDeviceInfo() {
    this.isDeviceInfoOpen.set(!this.isDeviceInfoOpen());
  }
  toggleShelfPositions() {
    this.isShelfPositionsOpen.set(!this.isShelfPositionsOpen());
  }
  toggleShelves() {
    this.isShelvesOpen.set(!this.isShelvesOpen());
  }
  toggleSettings() {
    this.isSettingOpen.set(!this.isSettingOpen);
  }
}
