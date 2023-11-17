import { DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { PspService } from 'src/app/services/psp/psp.service';


@Component({
  selector: 'app-product-page',
  templateUrl: './product-page.component.html',
  styleUrls: ['./product-page.component.css']
})
export class ProductPageComponent {

  constructor(private pspService: PspService,
              private datePipe: DatePipe,
              private router: Router) { }

  buyItem() {
    this.router.navigate(['/payment-method-page'])
  }

}
