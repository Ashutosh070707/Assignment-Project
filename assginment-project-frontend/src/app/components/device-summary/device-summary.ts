import { Component, signal } from '@angular/core';
import { ModalService } from '../../services/modal';
import { GlobalDeviceSummary } from '../../models/device-summary.model';
import { DeviceService } from '../../services/device';

@Component({
  selector: 'app-device-summary',
  imports: [],
  templateUrl: './device-summary.html',
  styleUrl: './device-summary.css',
})
export class DeviceSummary {
  constructor(public modalService: ModalService, public deviceService: DeviceService) {}

  isDeviceInfoOpen = signal(true);
  isShelfPositionsOpen = signal(false);
  isShelvesOpen = signal(false);
  isSettingOpen = signal(false); 

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
