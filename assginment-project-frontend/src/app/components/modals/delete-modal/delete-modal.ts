import { Component, computed, EventEmitter, Input, Output, signal } from '@angular/core';
import { DeviceService } from '../../../services/device';
import { ModalService } from '../../../services/modal';

@Component({
  selector: 'app-delete-modal',
  imports: [],
  templateUrl: './delete-modal.html',
  styleUrl: './delete-modal.css'
})

export class DeleteModal {
  itemType = computed(() => {
    const active = this.modalService.activeModal();
    if (active === 'delete-device') return 'Device';
    if (active === 'delete-shelf') return 'Shelf';
    if (active === 'delete-shelf-position') return 'Shelf Position';
    return 'Item'; // Fallback
  });
 
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private deviceService: DeviceService,
    public modalService: ModalService,
  ) {}

  closeModal() {
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
    this.modalService.closeModal();
  }

  onConfirmDelete() {
    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const type = this.itemType();

    if (type === 'Device') {
      const currentDeviceName = this.deviceService.selectedDeviceSummary()?.device?.deviceName;

      if (!currentDeviceName) {
        this.errorMessage.set('Cannot find the name of the device to delete.');
        this.isSubmitting.set(false);
        return;
      }

      this.deviceService.deleteDevice(currentDeviceName).subscribe({
        next: () => this.closeModal(),
        error: (err) => this.handleError(err, 'Failed to delete Device.'),
      });
    } else if (type === 'Shelf') {
      const shelfName = this.modalService.selectedItemId();
      if (!shelfName) return this.handleError({ error: 'Cannot find Shelf with this shelfName.' }, '');

      this.deviceService.deleteShelf(shelfName).subscribe({
        next: () => this.closeModal(),
        error: (err) => this.handleError(err, 'Failed to delete Shelf.'),
      });
    } else if (type === 'Shelf Position') {
      const positionId = this.modalService.selectedItemId(); 
      
      if (!positionId) {
        this.errorMessage.set('Cannot find the ID of the shelf position to delete.');
        this.isSubmitting.set(false);
        return;
      }
      
      this.deviceService.deleteShelfPosition(positionId).subscribe({
        next: () => this.closeModal(),
        error: (err) => this.handleError(err, 'Failed to delete Shelf Position.')
      });
    }
  }

  private handleError(err: any, defaultMessage: string) {
    this.isSubmitting.set(false);
    let backendError = defaultMessage;

    if (err.error) {
      if (typeof err.error === 'string') {
        backendError = err.error;
      } else if (err.error.message) {
        backendError = err.error.message;
      } else if (err.error.error) {
        backendError = err.error.error;
      }
    }

    this.errorMessage.set(backendError);
  }
}
