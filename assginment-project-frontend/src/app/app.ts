import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from "./components/header/header";
import { Sidebar } from "./components/sidebar/sidebar";
import { DeviceSummary } from './components/device-summary/device-summary';
import { ModalService } from './services/modal';
import { DeleteModal } from './components/modals/delete-modal/delete-modal';
import { DeviceModal } from './components/modals/device-modal/device-modal';
import { ShelfModal } from './components/modals/shelf-modal/shelf-modal';
import { ShelfPositionModal } from './components/modals/shelf-position-modal/shelf-position-modal';
import { DeviceService } from './services/device';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Sidebar, DeviceSummary, DeleteModal, DeviceModal, ShelfModal, ShelfPositionModal],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  constructor(public modalService: ModalService, private deviceService: DeviceService) {}
  // 5. This function runs automatically when the app loads
  ngOnInit() {
    this.deviceService.loadInitialDevices();
  }
}
