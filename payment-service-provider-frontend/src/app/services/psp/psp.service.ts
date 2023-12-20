import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PspService {

  apiHost: string = '/api';
  headers: HttpHeaders = new HttpHeaders({
    Accept: 'application/json',
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': this.apiHost,
  });

  constructor(private http: HttpClient) { }

  generateUrl(paymentRequest: any) : Observable<any> {
    return this.http.post(this.apiHost + '/psp/payment/card', paymentRequest);
  }

  bitcoinPayment(paymentRequest: any) {
    return this.http.get(this.apiHost + '/psp/ping-crypto', {
        headers: new HttpHeaders({
          'Accept': 'text/html',
        }),
        responseType: 'text'
      });
  }

  qrPayment(paymentRequest: any) {
    // return this.http.get(this.apiHost + 'api/psp/ping-qr');
  }

  paypalPayment(paymentRequest: any) {
    return this.http.post(this.apiHost + '/psp/payment/paypal', paymentRequest);
  }

  sendNewPaymentMethods(selectedMethods: any) {
    return this.http.post(this.apiHost + '/psp/payment-methods', selectedMethods);
  }

  getSubscribedPaymentMethodsByMerchantOrderId(merchantOrderId: string) {
    return this.http.get(this.apiHost + '/psp/subscribed-methods?merchantOrderId=' + merchantOrderId);
  }

  getSubscribedPaymentMethodsByUsername(sellerUsername: any) {
    return this.http.get(this.apiHost + '/psp/subscribed-methods?username=' + sellerUsername);
  }
}
