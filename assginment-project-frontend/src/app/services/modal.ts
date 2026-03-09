import { Injectable, signal } from '@angular/core';

export type ModalType =
  | 'none'
  | 'add-device'
  | 'update-device'
  | 'delete-device'
  | 'add-shelf'
  | 'update-shelf'
  | 'delete-shelf'
  | 'add-shelf-position'
  | 'delete-shelf-position';

@Injectable({
  providedIn: 'root', // This makes the service available everywhere in your app
})
export class ModalService {
  activeModal = signal<ModalType>('none');
  selectedItemId = signal<string | null>(null);

  openModal(type: ModalType, id: string | null = null) {
    this.selectedItemId.set(id);
    this.activeModal.set(type); 
  }

  closeModal() {
    this.activeModal.set('none');
    this.selectedItemId.set(null);
  }
}
