import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CardPaymentPageComponent } from './modules/payment/card-payment-page/card-payment-page.component';
import { PaymentMethodPageComponent } from './modules/payment/payment-method-page/payment-method-page.component';
import { SuccessPageComponent } from './modules/payment/success-page/success-page.component';
import { ErrorPageComponent } from './modules/payment/error-page/error-page.component';
import { FailedPageComponent } from './modules/payment/failed-page/failed-page.component';

const routes: Routes = [
  { path: '', redirectTo: '/payment-method-page', pathMatch: 'full' },
  { path: 'payment-method-page', component: PaymentMethodPageComponent},
  { path: 'acquirer-bank/card-details/:uuid/:paymentId', component: CardPaymentPageComponent },
  { path: 'success-payment', component: SuccessPageComponent},
  { path: 'error-payment', component: ErrorPageComponent},
  { path: 'failed-payment', component: FailedPageComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
