import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { DeviceService } from '../../../services/device';
import { ModalService } from '../../../services/modal';

@Component({
  selector: 'app-delete-modal',
  imports: [],
  templateUrl: './delete-modal.html',
  styleUrl: './delete-modal.css', // Assuming you have the .btn-spinner and .error-banner CSS here
})
export class DeleteModal {
  @Input() itemType: 'Device' | 'Shelf' | 'Shelf Position' = 'Device';
  @Output() close = new EventEmitter<void>();

  // Manage UI states locally in the modal
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private deviceService: DeviceService,
    public modalService: ModalService,
  ) {}

  closeModal() {
    this.errorMessage.set(null);
    this.isSubmitting.set(false);
    this.close.emit();
  }

  onConfirmDelete() {
    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    if (this.itemType === 'Device') {
      const currentDeviceName = this.deviceService.selectedDeviceSummary()?.device?.deviceName;

      if (!currentDeviceName) {
        this.errorMessage.set('Error: Cannot find the name of the device to delete.');
        this.isSubmitting.set(false);
        return;
      }

      this.deviceService.deleteDevice(currentDeviceName).subscribe({
        next: () => this.closeModal(),
        error: (err) => this.handleError(err, 'Failed to delete Device.'),
      });
    } else if (this.itemType === 'Shelf') {
      const shelfName = this.modalService.selectedItemId();
      if (!shelfName) return this.handleError({ error: 'Cannot find Shelf with this shelfName.' }, '');

      this.deviceService.deleteShelf(shelfName).subscribe({
        next: () => this.closeModal(),
        error: (err) => this.handleError(err, 'Failed to delete Shelf.'),
      });
    } // 3. Handling SHELF POSITION Deletion
    else if (this.itemType === 'Shelf Position') {
      
      // CHANGE 1: Grab the real ID from the ModalService instead of the placeholder string
      const positionId = this.modalService.selectedItemId(); 
      
      if (!positionId) {
        this.errorMessage.set('Error: Cannot find the ID of the shelf position to delete.');
        this.isSubmitting.set(false);
        return;
      }
      
      this.deviceService.deleteShelfPosition(positionId).subscribe({
        next: () => this.closeModal(),
        error: (err) => this.handleError(err, 'Failed to delete Shelf Position.')
      });
    }
  }

  // Reusable error parser
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
