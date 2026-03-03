import { Injectable, signal } from '@angular/core';

export type ModalType =
  | 'none'
  | 'add-device'
  | 'update-device'
  | 'delete-device'
  | 'add-shelf'
  | 'delete-shelf'
  | 'add-shelf-position'
  | 'delete-shelf-position';

@Injectable({
  providedIn: 'root', // This makes the service available everywhere in your app
})
export class ModalService {
  // The central state that controls what modal is open
  activeModal = signal<ModalType>('none');

  // NEW: The "bucket" that holds the ID of the specific item being edited or deleted
  selectedItemId = signal<string | null>(null);

  // UPDATED: Now accepts an optional second argument for the ID
  openModal(type: ModalType, id: string | null = null) {
    this.selectedItemId.set(id); // Drop the ID into the bucket
    this.activeModal.set(type);  // Turn on the modal
  }

  closeModal() {
    this.activeModal.set('none');
  }
}
