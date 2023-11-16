import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-card-payment-page',
  templateUrl: './card-payment-page.component.html',
  styleUrls: ['./card-payment-page.component.css']
})

export class CardPaymentPageComponent implements OnInit {
  uuid: string | null = null;
  orderId: string | null = null;

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.uuid = params['uuid'];
      this.orderId = params['orderId'];

      // Možete koristiti ove vrednosti za dinamičko kreiranje linkova ili druge operacije
    });
  }
}
