import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { PspService } from 'src/app/services/psp/psp.service';

@Component({
  selector: 'app-payment-method-page',
  templateUrl: './payment-method-page.component.html',
  styleUrls: ['./payment-method-page.component.css'],
})
export class PaymentMethodPageComponent implements OnInit {
  paymentForm: FormGroup;
  dataFromMerchant: any = {};
  subscribedPaymentMethods: string[] = [];

  constructor(
    private pspService: PspService,
    private datePipe: DatePipe,
    private router: Router,
    private route: ActivatedRoute,
    private formbuilder: FormBuilder,
    private toastr: ToastrService,
    private snackBar: MatSnackBar,
  ) {
    this.paymentForm = this.formbuilder.group({
      paymentMethod: [''],
    });
  }

  ngOnInit() {
    this.dataFromMerchant.amount =
      this.route.snapshot.queryParamMap.get('amount');
    this.dataFromMerchant.merchantOrderId =
      this.route.snapshot.queryParamMap.get('merchantOrderId');
    this.dataFromMerchant.merchantTimeStamp =
      this.route.snapshot.queryParamMap.get('merchantTimestamp');

    this.pspService.getSubscribedPaymentMethodsByMerchantOrderId(this.dataFromMerchant.merchantOrderId).subscribe({
      next: (res: any) => {
        this.subscribedPaymentMethods = res.paymentMethods.map((m: string) => m.toLowerCase());
      },
      error: (err) => {
        console.log(err);
      }
    }); 
  }

  buyItem() {
    const selectedPaymentMethod = this.paymentForm.value.paymentMethod;

    if (selectedPaymentMethod === 'cardpayment') {
      this.cardPayment();
    } else if (selectedPaymentMethod === 'qr') {
      this.qrPayment();
    } else if (selectedPaymentMethod === 'paypal') {
      this.paypalPayment();
    } else if (selectedPaymentMethod === 'bitcoin') {
      this.bitcoinPayment();
    } else {
      console.log('Molimo odaberite način plaćanja.');
    }
  }

  cardPayment() {
    this.pspService.generateUrl(this.dataFromMerchant).subscribe({
      next: (response: any) => {
        console.log(response);

        if (response && response.paymentUrl) {
          const parsedUrl = new URL(response.paymentUrl);
          const path = parsedUrl.pathname;

          this.router.navigate([path], { state: { amount: response.amount } });
        }
      },
      error: (error: any) => {
        this.toastr.error(error.error);
      },
    });
  }

  private displayError(message: string) {
    this.snackBar.open(message, 'Close', { duration: 5000})
  }

  bitcoinPayment() {
    this.pspService.bitcoinPayment(this.dataFromMerchant).subscribe({
      next: (res: any) => {
        this.toastr.success(res);
      },
      error: (err: any) => {
        this.toastr.error(err.error);
      },
    });
  }

  paypalPayment() {
    this.pspService.paypalPayment(this.dataFromMerchant).subscribe({
      next: (res: any) => {
        window.location.replace(res.paymentUrl);
      },
      error: (err: any) => {
        this.toastr.error(err.error);
      },
    });
  }

  qrPayment() {
    // this.pspService.qrPayment(this.dataFromMerchant).subscribe({
    //   next: (res: any) => {
    //     console.log(res);
    //   },
    //   error: (err: any) => {
    //     console.log(err);
    //   }
    // })
  }
}
