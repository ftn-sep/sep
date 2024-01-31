import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-product-items',
  templateUrl: './product-items.component.html',
  styleUrls: ['./product-items.component.scss'],
})
export class ProductItemsComponent {
  items: any[] = [];

  constructor(private datePipe: DatePipe) {}
  ngOnInit(): void {
    this.items = this.fillItems();
  }

  buy(item: any) {
      const merchOrderId = '9918' + this.getRandomSixDigits();
      const apiKey = 'c6cc0d0d8eb94653b96c6731a';
      const merchTimestamp = this.datePipe.transform(new Date(), 'yyyy-MM-dd HH:mm:ss')
      window.location.href = `http://localhost:4200/payment-method-page?merchantOrderId=${merchOrderId}&merchantTimestamp=${merchTimestamp}&amount=${item.price}&apiKey=${apiKey}`
  }

  fillItems(): any {
    return [
      {
        title: 'Kodifikacija',
        price: 12,
        img: 'https://thumbs.dreamstime.com/b/copyright-law-document-judge-gavel-concept-legal-education-128693946.jpg',
      },
      {
        title: 'Izdavanje zakona',
        price: 5,
        img: 'https://londonmedarb.com/wp-content/uploads/2015/08/Legal-Paperwork-003.jpg',
      },
    ];
  }
  getRandomSixDigits() {
    return Math.floor(100000 + Math.random() * 899999);
  }
}
