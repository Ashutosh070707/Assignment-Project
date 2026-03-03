import { Component, EventEmitter, Output, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { DeviceService } from '../../../services/device';
import { ModalService } from '../../../services/modal'; // Need this to get the ID!

@Component({
  selector: 'app-shelf-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './shelf-modal.html',
  styleUrl: './shelf-modal.css', // Assuming spinner and error-banner CSS are here
})
export class ShelfModal {
  @Output() close = new EventEmitter<void>();

  shelfForm: FormGroup;
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private deviceService: DeviceService,
    public modalService: ModalService 
  ) {
    this.shelfForm = this.fb.group({
      shelfName: ['', Validators.required],
      partNumber: ['', Validators.required]
    });
  }

  closeModal() {
    this.shelfForm.reset();
    this.isSubmitting.set(false);
    this.errorMessage.set(null);
    this.close.emit();
  }

  onSubmit() {
    if (this.shelfForm.invalid) {
      this.shelfForm.markAllAsTouched();
      return;
    }

    // 1. Get the target Position ID from our ModalService bucket
    const targetPositionId = this.modalService.selectedItemId();
    
    if (!targetPositionId) {
      this.errorMessage.set('Error: Cannot find the Shelf Position to attach this to.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    // 2. Build the payload. Adjust this based on what your Spring Boot backend expects!
    const newShelfData = {
      shelfName: this.shelfForm.value.shelfName,
      partNumber: this.shelfForm.value.partNumber,
      shelfPositionId: targetPositionId // Pass the position ID so backend knows where to link it
    };

    // 3. Send to service
    this.deviceService.addShelf(newShelfData).subscribe({
      next: () => {
        this.closeModal(); // Success!
      },
      error: (err) => {
        this.isSubmitting.set(false);
        let backendError = 'Failed to add shelf.';
        if (err.error) {
          if (typeof err.error === 'string') backendError = err.error;
          else if (err.error.message) backendError = err.error.message;
        }
        this.errorMessage.set(backendError);
      }
    });
  }
}