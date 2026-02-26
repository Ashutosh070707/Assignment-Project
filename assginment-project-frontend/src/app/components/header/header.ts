import { Component, signal } from '@angular/core';
import { ModalService } from '../../services/modal';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  constructor(public modalService: ModalService) {}

  isDarkMode = signal(true);
  toggleTheme(){
    if (this.isDarkMode()) {
      document.body.classList.add('light-theme');
    } else {
      document.body.classList.remove('light-theme');
    }
    this.isDarkMode.set(!this.isDarkMode());
  }
}
