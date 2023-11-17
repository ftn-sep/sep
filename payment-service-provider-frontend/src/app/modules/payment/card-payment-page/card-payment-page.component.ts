import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AcquirerBankService } from 'src/app/services/acquirer-bank/acquirer-bank.service';

@Component({
  selector: 'app-card-payment-page',
  templateUrl: './card-payment-page.component.html',
  styleUrls: ['./card-payment-page.component.css']
})

export class CardPaymentPageComponent implements OnInit {

  cardHolderName!: string;
  pan!: string;
  cardExpiresIn!: string;
  securityCode!: string;
  amount!: number;

  uuid: string | null = null;
  paymentId: string | null = null;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private acquirerBankService: AcquirerBankService) {

              this.amount = this.router.getCurrentNavigation()?.extras.state!['amount'];
              }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.uuid = params['uuid'];
      this.paymentId = params['paymentId'];
    });
  }


  pay(){
    const paymentDetails = {
      cardHolderName: this.cardHolderName,
      pan: this.pan,
      cardExpiresIn: this.cardExpiresIn,
      securityCode: this.securityCode,
      amount: this.amount,
      uuid: this.uuid,
      paymentId: this.paymentId
    }

    this.acquirerBankService.generateUrl(paymentDetails)
    .subscribe(
      response => {
        if (response && response.paymentUrl) {
          window.location.href = response.paymentUrl;
        }
      },
      error => {
      }
    );


  }
}
