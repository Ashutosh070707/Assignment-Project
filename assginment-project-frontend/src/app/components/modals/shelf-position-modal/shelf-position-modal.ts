import { Component, EventEmitter, Output, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { DeviceService } from '../../../services/device'; // Adjust path if needed

@Component({
  selector: 'app-shelf-position-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './shelf-position-modal.html',
  styleUrl: './shelf-position-modal.css', // Assuming you have the spinner and error-banner CSS here
})
export class ShelfPositionModal {
  @Output() close = new EventEmitter<void>();

  positionForm: FormGroup;
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private deviceService: DeviceService
  ) {
    // Initialize the form
    this.positionForm = this.fb.group({
      id: ['', Validators.required],
      assignedShelf: [''] // Optional, default to empty string
    });
  }

  closeModal() {
    this.positionForm.reset();
    this.isSubmitting.set(false);
    this.errorMessage.set(null);
    this.close.emit();
  }

  onSubmit() {
    if (this.positionForm.invalid) {
      this.positionForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    // 1. Grab the current device from the global signal
    const currentDevice = this.deviceService.selectedDeviceSummary()?.device;

    if (!currentDevice) {
      this.handleError({ error: 'No active device selected.' }, 'Failed to add position.');
      return;
    }

    // 2. Build the newPositionData object! 
    // This is where newPositionData comes from. We map the form values and the device details to match your backend model.
    const newPositionData = {
      id: this.positionForm.value.id,
      deviceId: currentDevice.id, // Your backend Neo4j query explicitly asks for $deviceId
      deviceName: currentDevice.deviceName // Adding this just in case your controller expects it
    };

    // 3. Send it to the service
    this.deviceService.addShelfPosition(newPositionData).subscribe({
      next: () => {
        // Success! The tap() operator in the service updates the UI, so we just close the modal.
        this.closeModal();
      },
      error: (err) => {
        this.handleError(err, 'Failed to add shelf position. It may already exist.');
      }
    });
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
    } else if (err.message) {
      backendError = err.message;
    }
    
    this.errorMessage.set(backendError);
  }
}