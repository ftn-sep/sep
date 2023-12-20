import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AcquirerBankService {
  apiHost: string = '/api';
  headers: HttpHeaders = new HttpHeaders({
    Accept: 'application/json',
    'Content-Type': 'application/json',
  });

  constructor(private http: HttpClient) { }

  generateUrl(paymentDetails: any) : Observable<any> {
    return this.http.post(this.apiHost + '/acquirer/payment-card-details', paymentDetails);
  }
}

