import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  isDarkMode = signal(true);
  toggleTheme(){
    if (this.isDarkMode()) {
      document.body.classList.add('light-theme');
    } else {
      document.body.classList.remove('light-theme');
    }
    this.isDarkMode.set(!this.isDarkMode());
  }

  openAddDevice(){
    console.log('Add Device button clicked - Modal will open here.');
  }

}
