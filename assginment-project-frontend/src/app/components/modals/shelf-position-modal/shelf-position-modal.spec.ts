import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShelfPositionModal } from './shelf-position-modal';

describe('ShelfPositionModal', () => {
  let component: ShelfPositionModal;
  let fixture: ComponentFixture<ShelfPositionModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShelfPositionModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShelfPositionModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
