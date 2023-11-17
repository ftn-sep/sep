import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductPageComponent } from './modules/product-page/product-page.component';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule, DatePipe } from '@angular/common';
import { PaymentMethodPageComponent } from './modules/payment/payment-method-page/payment-method-page.component';
import { CardPaymentPageComponent } from './modules/payment/card-payment-page/card-payment-page.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SuccessPageComponent } from './modules/payment/success-page/success-page.component';
import { ErrorPageComponent } from './modules/payment/error-page/error-page.component';
import { FailedPageComponent } from './modules/payment/failed-page/failed-page.component';

@NgModule({
  declarations: [
    AppComponent,
    ProductPageComponent,
    PaymentMethodPageComponent,
    CardPaymentPageComponent,
    SuccessPageComponent,
    ErrorPageComponent,
    FailedPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    DatePipe 
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
