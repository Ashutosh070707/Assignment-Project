import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-shelf-modal',
  imports: [],
  templateUrl: './shelf-modal.html',
  styleUrl: './shelf-modal.css',
})
export class ShelfModal {
@Output() close = new EventEmitter<void>();

  closeModal() {
    this.close.emit();
  }
}
