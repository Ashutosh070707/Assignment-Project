import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { DeviceService } from '../../../services/device'; 
import { ModalService } from '../../../services/modal';
import { Observable, of, timer } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-device-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './device-modal.html',
  styleUrl: './device-modal.css',
})
export class DeviceModal implements OnInit {
  deviceForm: FormGroup;
  isSubmitting = signal(false);
  errorMessage = signal<string | null>(null);

  // Store original name so it doesn't trigger "name taken" against itself
  originalDeviceName = '';

  constructor(
    public modalService: ModalService,
    private deviceService: DeviceService,
    private fb: FormBuilder
  ) {
    this.deviceForm = this.fb.group({
      // Added async validator to deviceName
      deviceName: ['', [Validators.required], [this.deviceNameValidator()]],
      deviceType: ['', Validators.required],
      partNumber: ['', Validators.required],
      buildingName: ['', Validators.required],
      numberOfShelfPositions: [0, [Validators.required, Validators.min(1), Validators.max(14)]]
    });
  }

  ngOnInit() {
    if (this.modalService.activeModal() === 'update-device') {
      const currentDevice = this.deviceService.selectedDeviceSummary()?.device;
      
      if (currentDevice) {
        this.originalDeviceName = currentDevice.deviceName; // Save for async validation

        this.deviceForm.patchValue({
          deviceName: currentDevice.deviceName,
          deviceType: currentDevice.deviceType,
          partNumber: currentDevice.partNumber,
          buildingName: currentDevice.buildingName,
          numberOfShelfPositions: currentDevice.numberOfShelfPositions
        });

        // CRITICAL: Remove validation from the hidden field so the form can actually be valid!
        const shelfControl = this.deviceForm.get('numberOfShelfPositions');
        if (shelfControl) {
          shelfControl.clearValidators();
          shelfControl.updateValueAndValidity();
        }
      }
    }
  }

  // =========================================
  // THE ASYNC VALIDATOR
  // =========================================
  deviceNameValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) return of(null);

      // If updating and name hasn't changed, skip validation
      if (this.modalService.activeModal() === 'update-device' && control.value === this.originalDeviceName) {
        return of(null);
      }

      // Ping backend after 500ms debounce
      return timer(500).pipe(
        // Assuming your deviceService has a checkDeviceNameValidity method!
        switchMap(() => this.deviceService.checkDeviceNameValidity(control.value)),
        map(() => null),
        catchError(() => of({ nameTaken: true }))
      );
    };
  }

  closeModal() {
    this.modalService.closeModal();
    this.deviceForm.reset(); 
    this.isSubmitting.set(false); 
    this.errorMessage.set(null);  
  }

  onSubmit(event?: Event) {
    if (event) {
      event.preventDefault(); 
    }

    // Notice the added check for pending state
    if (this.deviceForm.invalid || this.deviceForm.pending) {
      this.deviceForm.markAllAsTouched(); 
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    if (this.modalService.activeModal() === 'add-device') {
      this.deviceService.addDevice(this.deviceForm.value).subscribe({
        next: (response) => {
          const res = response as any;
          if (res && typeof res === 'string' && res.toLowerCase().includes('error')) {
            this.isSubmitting.set(false);
            this.errorMessage.set(res);
            return;
          }
          this.closeModal(); 
        },
        error: (err) => {
          this.isSubmitting.set(false); 
          const backendError = typeof err.error === 'string' ? err.error : 'Failed to add device. It may already exist.';
          this.errorMessage.set(backendError); 
        }
      });
      
    } else if (this.modalService.activeModal() === 'update-device') {
      const currentDeviceId = this.deviceService.selectedDeviceSummary()?.device.id;
      
      if (currentDeviceId) {
        // Build the DTO payload matching your backend exactly
        const payload = {
          id: currentDeviceId,
          oldDeviceName: this.originalDeviceName,
          newDeviceName: this.deviceForm.value.deviceName,
          newPartNumber: this.deviceForm.value.partNumber,
          newBuildingName: this.deviceForm.value.buildingName,
          newDeviceType: this.deviceForm.value.deviceType
        };

        this.deviceService.updateDevice(currentDeviceId, payload).subscribe({
          next: () => {
            this.closeModal(); 
          },
          error: (err) => {
            this.isSubmitting.set(false);
            const backendError = typeof err.error === 'string' ? err.error : 'Failed to update device.';
            this.errorMessage.set(backendError);
          }
        });
      }
    }
  }
}