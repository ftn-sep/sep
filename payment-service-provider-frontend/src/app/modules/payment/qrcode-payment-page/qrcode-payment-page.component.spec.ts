import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QrcodePaymentPageComponent } from './qrcode-payment-page.component';

describe('QrcodePaymentPageComponent', () => {
  let component: QrcodePaymentPageComponent;
  let fixture: ComponentFixture<QrcodePaymentPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QrcodePaymentPageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(QrcodePaymentPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
