import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-shelf-position-modal',
  imports: [],
  templateUrl: './shelf-position-modal.html',
  styleUrl: './shelf-position-modal.css',
})
export class ShelfPositionModal {
@Output() close = new EventEmitter<void>();

  closeModal() {
    this.close.emit();
  }
}
