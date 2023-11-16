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
  });

  constructor(private http: HttpClient) { }

  generateUrl(paymentRequest: any) : Observable<any> {
    return this.http.post(this.apiHost + 'api/psp/card-payment', paymentRequest);
  }
}
