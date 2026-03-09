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
  isDeviceInfoOpen = signal(true);
  isShelfPositionsOpen = signal(false);
  isShelvesOpen = signal(false);
  isSettingOpen = signal(false);

  isAddingPosition = signal(false);

  constructor(
    public modalService: ModalService,
    public deviceService: DeviceService,
  ) {}

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

  onAddShelfPosition() {
    const currentDevice = this.deviceService.selectedDeviceSummary()?.device;

    if (!currentDevice) {
      alert('Error: No active device selected.');
      return;
    }

    this.isAddingPosition.set(true);

    // Building the payload to send to the backend
    const newPositionData = {
      deviceId: currentDevice.id
    };

    this.deviceService.addShelfPosition(newPositionData).subscribe({
      next: () => {
        this.isAddingPosition.set(false);
      },
      error: (err) => {
        this.isAddingPosition.set(false);
        const backendError = typeof err.error === 'string' ? err.error : 'Failed to add position.';
        console.log(backendError, err)
      },
    });
  }
}
