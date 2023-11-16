import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductPageComponent } from './modules/product-page/product-page.component';
import { CardPaymentPageComponent } from './modules/payment/card-payment-page/card-payment-page.component';

const routes: Routes = [
  { path: 'product-page', component: ProductPageComponent },
  { path: 'acquirer-bank/card-details/:uuid/:orderId', component: CardPaymentPageComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
