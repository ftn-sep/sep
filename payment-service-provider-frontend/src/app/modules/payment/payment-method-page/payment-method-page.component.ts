import { state } from '@angular/animations';
import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { PspService } from 'src/app/services/psp/psp.service';

@Component({
  selector: 'app-payment-method-page',
  templateUrl: './payment-method-page.component.html',
  styleUrls: ['./payment-method-page.component.css']
})
export class PaymentMethodPageComponent {

  paymentForm: FormGroup;

  constructor(private pspService: PspService,
    private datePipe: DatePipe,
    private router: Router,
    private formbuilder: FormBuilder) {
      this.paymentForm = this.formbuilder.group({
        paymentMethod: ['']
      })
     }


    buyItem() {
      const dataToSend = {
        amount: 100,
        merchantOrderId: 1000701293,
        merchantTimeStamp: this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss')
      };

      //const selectedPaymentMethod = this.paymentForm.get('payment-method')?.value;
      const selectedPaymentMethod = this.paymentForm.value.paymentMethod;

      if (selectedPaymentMethod === 'cardpayment') {
        this.pspService.generateUrl(dataToSend)
        .subscribe(
          response => {
            if (response && response.paymentUrl) {
            const parsedUrl = new URL(response.paymentUrl);
            const path = parsedUrl.pathname;
  
            this.router.navigate([path], {state: {amount: response.amount}})
            }
          },
          error => {
          }
        ); 
      } else if (selectedPaymentMethod === 'qr') {
        // todo
      } else if (selectedPaymentMethod === 'paypal') {
        // todo
      } else if (selectedPaymentMethod === 'bitcoin') {
        // todo
      } else {
        console.log('Molimo odaberite način plaćanja.');
      }
    }

}
