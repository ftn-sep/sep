import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardPaymentPageComponent } from './card-payment-page.component';

describe('CardPaymentPageComponent', () => {
  let component: CardPaymentPageComponent;
  let fixture: ComponentFixture<CardPaymentPageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CardPaymentPageComponent]
    });
    fixture = TestBed.createComponent(CardPaymentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
