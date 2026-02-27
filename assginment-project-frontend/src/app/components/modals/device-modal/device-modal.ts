import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

import { DeviceService } from '../../../services/device'; 
import { ModalService } from '../../../services/modal';

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

  constructor(
    public modalService: ModalService,
    private deviceService: DeviceService,
    private fb: FormBuilder
  ) {
    this.deviceForm = this.fb.group({
      deviceName: ['', Validators.required],
      deviceType: ['', Validators.required],
      partNumber: ['', Validators.required],
      buildingName: ['', Validators.required],
      numberOfShelfPositions: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit() {
    // FIX 1: Changed modalState() to activeModal()
    if (this.modalService.activeModal() === 'update-device') {
      const currentDevice = this.deviceService.selectedDeviceSummary()?.device;
      
      if (currentDevice) {
        this.deviceForm.patchValue({
          deviceName: currentDevice.deviceName,
          deviceType: currentDevice.deviceType,
          partNumber: currentDevice.partNumber,
          buildingName: currentDevice.buildingName,
          numberOfShelfPositions: currentDevice.numberOfShelfPositions
        });
      }
    }
  }

  onOverlayClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (target.classList.contains('modal-overlay')) {
      this.closeModal();
    }
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

    if (this.deviceForm.invalid) {
      this.deviceForm.markAllAsTouched(); 
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    // FIX 2: Changed modalState() to activeModal()
    if (this.modalService.activeModal() === 'add-device') {
      this.deviceService.addDevice(this.deviceForm.value).subscribe({
        next: (response) => {
          
          // FIX 3: Cast response to 'any' to stop the 'never' type error
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
      
    // FIX 4: Changed modalState() to activeModal()
    } else if (this.modalService.activeModal() === 'update-device') {
      const currentDeviceId = this.deviceService.selectedDeviceSummary()?.device.id;
      if (currentDeviceId) {
        this.deviceService.updateDevice(currentDeviceId, this.deviceForm.value).subscribe({
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