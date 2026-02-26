import { Injectable, signal } from '@angular/core';

export type ModalType = 'none' | 'add-device' | 'update-device' | 'delete-device' | 'add-shelf' | 'delete-shelf' | 'add-shelf-position' | 'delete-shelf-position';

@Injectable({
  providedIn: 'root' // This makes the service available everywhere in your app
})
export class ModalService {
  // The central state that controls what modal is open
  activeModal = signal<ModalType>('none');

  openModal(type: ModalType) {
    this.activeModal.set(type);
  }

  closeModal() {
    this.activeModal.set('none');
  }
}