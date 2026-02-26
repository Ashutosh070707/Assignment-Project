import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShelfModal } from './shelf-modal';

describe('ShelfModal', () => {
  let component: ShelfModal;
  let fixture: ComponentFixture<ShelfModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShelfModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ShelfModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
