import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductPageComponent } from './modules/product-page/product-page.component';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule, DatePipe } from '@angular/common';
import { PaymentMethodPageComponent } from './modules/payment/payment-method-page/payment-method-page.component';
import { CardPaymentPageComponent } from './modules/payment/card-payment-page/card-payment-page.component';

@NgModule({
  declarations: [
    AppComponent,
    ProductPageComponent,
    PaymentMethodPageComponent,
    CardPaymentPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CommonModule 
  ],
  providers: [
    DatePipe 
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
