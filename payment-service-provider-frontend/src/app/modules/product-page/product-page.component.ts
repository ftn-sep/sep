import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { PspService } from 'src/app/services/psp/psp.service';


@Component({
  selector: 'app-product-page',
  templateUrl: './product-page.component.html',
  styleUrls: ['./product-page.component.css']
})
export class ProductPageComponent {

  constructor(private pspService: PspService,
              private datePipe: DatePipe) { }

    buyItem() {
    const dataToSend = {
      amount: 100,
      merchantOrderId: 1000123,
      merchantTimeStamp: this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss')
    };

    this.pspService.generateUrl(dataToSend)
      .subscribe(
        response => {
          console.log('Uspešno poslato na backend!', response);
          if (response && response.paymentUrl) {
            window.location.href = response.paymentUrl;
          }
        },
        error => {
          console.error('Greška prilikom slanja na backend:', error);
          // Dodajte logiku za obradu greške ako je potrebno
        }
      );
  }

}
