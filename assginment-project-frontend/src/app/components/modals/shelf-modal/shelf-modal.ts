import { Component, EventEmitter, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { DeviceService } from '../../../services/device';
import { ModalService } from '../../../services/modal';
import { Observable, of, timer } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-shelf-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './shelf-modal.html',
  styleUrl: './shelf-modal.css', 
})
export class ShelfModal implements OnInit {
  @Output() close = new EventEmitter<void>();

  shelfForm: FormGroup;
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);
  
  // NEW: Store the original name so we don't flag it as duplicate when updating
  originalShelfName = '';

  constructor(
    private fb: FormBuilder,
    private deviceService: DeviceService,
    public modalService: ModalService 
  ) {
    this.shelfForm = this.fb.group({
      // Notice the 3rd argument array is for Async Validators!
      shelfName: ['', [Validators.required], [this.shelfNameValidator()]],
      partNumber: ['', Validators.required]
    });
  }

  ngOnInit() {
    if (this.modalService.activeModal() === 'update-shelf') {
      const shelfId = this.modalService.selectedItemId();
      const currentSummary = this.deviceService.selectedDeviceSummary();
      
      if (currentSummary && shelfId) {
        const targetPair = currentSummary.shelfPairs.find(pair => pair.shelf?.id === shelfId);
        if (targetPair && targetPair.shelf) {
          
          this.originalShelfName = targetPair.shelf.shelfName; // Store the original name
          
          this.shelfForm.patchValue({
            shelfName: targetPair.shelf.shelfName,
            partNumber: targetPair.shelf.partNumber
          });
        }
      }
    }
  }

  // =========================================
  // THE ASYNC VALIDATOR
  // =========================================
  shelfNameValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      // 1. If it's empty, don't bother checking
      if (!control.value) return of(null);

      // 2. If we are updating and the name hasn't changed, it's valid!
      if (this.modalService.activeModal() === 'update-shelf' && control.value === this.originalShelfName) {
        return of(null);
      }

      // 3. Wait 500ms after the user stops typing (Debounce)
      return timer(500).pipe(
        switchMap(() => this.deviceService.checkShelfNameValidity(control.value)),
        // If the backend returns 200 OK, the name is valid (return null)
        map(() => null),
        // If the backend throws an error (e.g. 400 or 409), the name is taken
        catchError(() => of({ nameTaken: true }))
      );
    };
  }

  closeModal() {
    this.shelfForm.reset();
    this.isSubmitting.set(false);
    this.errorMessage.set(null);
    this.close.emit();
  }

  onSubmit() {
    if (this.shelfForm.invalid || this.shelfForm.pending) {
      this.shelfForm.markAllAsTouched();
      return;
    }

    const targetId = this.modalService.selectedItemId();
    if (!targetId) {
      this.errorMessage.set('Error: Target ID not found.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    if (this.modalService.activeModal() === 'add-shelf') {
      const newShelfData = {
        shelfName: this.shelfForm.value.shelfName,
        partNumber: this.shelfForm.value.partNumber,
        shelfPositionId: targetId
      };

      this.deviceService.addShelf(newShelfData).subscribe({
        next: () => this.closeModal(),
        error: (err) => this.handleError(err, 'Failed to add shelf.')
      });
    } 
    else if (this.modalService.activeModal() === 'update-shelf') {
      this.deviceService.updateShelf(targetId, this.shelfForm.value).subscribe({
        next: () => this.closeModal(),
        error: (err) => this.handleError(err, 'Failed to update shelf.')
      });
    }
  }

  private handleError(err: any, defaultMessage: string) {
    this.isSubmitting.set(false);
    let backendError = defaultMessage;
    if (err.error) {
      if (typeof err.error === 'string') backendError = err.error;
      else if (err.error.message) backendError = err.error.message;
    }
    this.errorMessage.set(backendError);
  }
}