import { Component, signal } from '@angular/core';
import { Header } from "./components/header/header";
import { Sidebar } from "./components/sidebar/sidebar";
import { DeviceSummary } from './components/device-summary/device-summary';
import { ModalService } from './services/modal';
import { DeleteModal } from './components/modals/delete-modal/delete-modal';
import { DeviceModal } from './components/modals/device-modal/device-modal';
import { ShelfModal } from './components/modals/shelf-modal/shelf-modal';
import { DeviceService } from './services/device';

@Component({
  selector: 'app-root',
  imports: [Header, Sidebar, DeviceSummary, DeleteModal, DeviceModal, ShelfModal],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  constructor(public modalService: ModalService, private deviceService: DeviceService) {}

  // To load all the devices from backend when application starts
  ngOnInit() {
    this.deviceService.loadInitialDevices();
  }
}
