import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-delete-modal',
  imports: [],
  templateUrl: './delete-modal.html',
  styleUrl: './delete-modal.css',
})
export class DeleteModal {
// This catches the [itemType]="..." from your HTML
  @Input() itemType: 'Device' | 'Shelf' | 'Shelf Position' = 'Device';

  // This sets up the (close)="..." event
  @Output() close = new EventEmitter<void>();

  closeModal() {
    this.close.emit();
  }
}
