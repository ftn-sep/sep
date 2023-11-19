import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AcquirerBankService } from 'src/app/services/acquirer-bank/acquirer-bank.service';


@Component({
  selector: 'app-card-payment-page',
  templateUrl: './card-payment-page.component.html',
  styleUrls: ['./card-payment-page.component.css']
})

export class CardPaymentPageComponent implements OnInit {
  paymentForm!: FormGroup;
  amount!: number;
  uuid: string | null = null;
  paymentId: string | null = null;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private acquirerBankService: AcquirerBankService,
              private formBuilder: FormBuilder,
              private toastr: ToastrService) {

              this.amount = this.router.getCurrentNavigation()?.extras.state!['amount'];
              }

  ngOnInit() {
    this.paymentForm = this.formBuilder.group({
      cardHolderName: ['', Validators.required],
      pan: ['', [Validators.required, Validators.pattern('^\\d{4}(?:\\s*\\d{4}){3}$')]],
      cardExpiresIn: ['', [Validators.required, Validators.pattern('^(0[1-9]|1[0-2])/(2[3-9]|[3-9][0-9])$')]],
      securityCode: ['', [Validators.required, Validators.pattern('[0-9]{3}'), Validators.minLength(3) , Validators.maxLength(3)]]
    });
  }


  pay(){
    this.route.params.subscribe(params => {
      this.uuid = params['uuid'];
      this.paymentId = params['paymentId'];
    });
    
    const paymentDetails = this.paymentForm.value;
    paymentDetails.amount = this.amount;
    paymentDetails.uuid = this.uuid;
    paymentDetails.paymentId = this.paymentId;


    this.acquirerBankService.generateUrl(paymentDetails)
    .subscribe(
      response => {
        if (response) {
          const parsedUrl = new URL(response);
          const path = parsedUrl.pathname;

          this.router.navigate([path]);
        }
      },
      error => {
        if (error && error.error) {
          //this.toastr.error('Hello world!', 'Toastr fun!');
          const parsedUrl = new URL(error.error);
          const path = parsedUrl.pathname;

          this.router.navigate([path])
        }
      }
    );
  }
}
