import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviceSummary } from './device-summary';

describe('DeviceSummary', () => {
  let component: DeviceSummary;
  let fixture: ComponentFixture<DeviceSummary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeviceSummary]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeviceSummary);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
