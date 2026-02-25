import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from "./components/header/header";
import { Sidebar } from "./components/sidebar/sidebar";
import { DeviceSummary } from './components/device-summary/device-summary';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Sidebar, DeviceSummary],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
}
