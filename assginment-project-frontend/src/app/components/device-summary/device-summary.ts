import { Component, signal } from '@angular/core';
import { ModalService } from '../../services/modal';
import { GlobalDeviceSummary } from '../../models/device-summary.model';

@Component({
  selector: 'app-device-summary',
  imports: [],
  templateUrl: './device-summary.html',
  styleUrl: './device-summary.css',
})
export class DeviceSummary {
  constructor(public modalService: ModalService) {}

  isDeviceInfoOpen = signal(true);
  isShelfPositionsOpen = signal(false);
  isShelvesOpen = signal(false);
  isSettingOpen = signal(false);

  deviceSummary = signal<GlobalDeviceSummary>({
    device: {
      id: 'd-998877',
      deviceName: 'Core-Aggregator-Noida',
      partNumber: 'NTK-503-AB',
      deviceType: 'Optical Transport Node',
      buildingName: 'Noida-DC-01',
      noOfShelfPositions: 4,
    },
    shelfPairs: [
      {
        shelfPosition: {
          id: 'sp-01',
          deviceId: 'OPT-NODE-A',
        },
        shelf: {
          id: 'sh-01',
          shelfName: 'Main-Processing-Shelf',
          partNumber: 'NTK-100-BB',
        },
      },
      {
        shelfPosition: {
          id: 'sp-02',
          deviceId: 'OPT-NODE-A',
        },
        shelf: null,
      },
      {
        shelfPosition: {
          id: 'sp-03',
          deviceId: 'OPT-NODE-A',
        },
        shelf: {
          id: 'sh-03',
          shelfName: 'Power-Distribution-Shelf',
          partNumber: 'NTK-PWR-AC',
        },
      },
      {
        shelfPosition: {
          id: 'sp-04',
          deviceId: 'OPT-NODE-A',
        },
        shelf: null,
      },
    ],
  });

  toggleDeviceInfo() {
    this.isDeviceInfoOpen.update((v) => !v);
    if (this.isDeviceInfoOpen()) {
      this.isShelfPositionsOpen.set(false);
      this.isShelvesOpen.set(false);
      this.isSettingOpen.set(false);
    }
  }
  toggleShelfPositions() {
    this.isShelfPositionsOpen.update((v) => !v);
    if (this.isShelfPositionsOpen()) {
      this.isDeviceInfoOpen.set(false);
      this.isShelvesOpen.set(false);
      this.isSettingOpen.set(false);
    }
  }
  toggleShelves() {
    this.isShelvesOpen.update((v) => !v);
    if (this.isShelvesOpen()) {
      this.isDeviceInfoOpen.set(false);
      this.isShelfPositionsOpen.set(false);
      this.isSettingOpen.set(false);
    }
  }
  toggleSetting() {
    this.isSettingOpen.update((v) => !v);
    if (this.isSettingOpen()) {
      this.isDeviceInfoOpen.set(false);
      this.isShelfPositionsOpen.set(false);
      this.isShelvesOpen.set(false);
    }
  }
}
