import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PspService {


  apiHost: string = 'http://localhost:8080/';
  headers: HttpHeaders = new HttpHeaders({
    Accept: 'application/json',
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': this.apiHost,
  });

  constructor(private http: HttpClient) { }

  generateUrl(paymentRequest: any) : Observable<any> {
    return this.http.post(this.apiHost + 'api/psp/card-payment', paymentRequest);
  }

  bitcoinPayment(paymentRequest: any) {
    return this.http.post(this.apiHost + 'api/psp/crypto-payment', paymentRequest);
  }

  qrPayment(paymentRequest: any) {
    // return this.http.get(this.apiHost + 'api/psp/ping-qr');
  }

  paypalPayment(paymentRequest: any) {
    return this.http.post(this.apiHost + 'api/psp/paypal-payment', paymentRequest);
  }
}
