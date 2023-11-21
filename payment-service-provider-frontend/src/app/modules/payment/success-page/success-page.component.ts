import { Component } from '@angular/core';

@Component({
  selector: 'app-success-page',
  templateUrl: './success-page.component.html',
  styleUrls: ['./success-page.component.css']
})
export class SuccessPageComponent {

  backToStore() {
    window.location.href = 'http://localhost:4201/'
  }
}
