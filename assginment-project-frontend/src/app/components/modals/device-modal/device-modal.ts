import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-device-modal',
  imports: [],
  templateUrl: './device-modal.html',
  styleUrl: './device-modal.css',
})
export class DeviceModal {
  // This catches the [mode]="..." from your HTML
  @Input() mode: 'add' | 'update' = 'add'; 

  // This sets up the (close)="..." event
  @Output() close = new EventEmitter<void>();

  closeModal() {
    this.close.emit();
  }
}
