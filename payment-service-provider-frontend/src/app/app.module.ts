import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule, DatePipe } from '@angular/common';
import { PaymentMethodPageComponent } from './modules/payment/payment-method-page/payment-method-page.component';
import { CardPaymentPageComponent } from './modules/payment/card-payment-page/card-payment-page.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SuccessPageComponent } from './modules/payment/success-page/success-page.component';
import { ErrorPageComponent } from './modules/payment/error-page/error-page.component';
import { FailedPageComponent } from './modules/payment/failed-page/failed-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { initializeKeycloak } from './init/keycloak-init.factory';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { QrcodePaymentPageComponent } from './modules/payment/qrcode-payment-page/qrcode-payment-page.component';

@NgModule({
  declarations: [
    AppComponent,
    PaymentMethodPageComponent,
    CardPaymentPageComponent,
    SuccessPageComponent,
    ErrorPageComponent,
    FailedPageComponent,
    QrcodePaymentPageComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      timeOut: 10000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    }),
    KeycloakAngularModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatTableModule,
    MatSnackBarModule,
    NgbModule,
  ],
  providers: [
    DatePipe,
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
