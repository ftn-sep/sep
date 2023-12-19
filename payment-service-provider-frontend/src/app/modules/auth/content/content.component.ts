import { Component } from '@angular/core';
import { FormGroup, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { KeycloakService } from 'keycloak-angular';
import { PspService } from 'src/app/services/psp/psp.service';

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  standalone: true,
  imports: [ReactiveFormsModule],
  styleUrl: './content.component.css'
})
export class ContentComponent {
  paymentForm: FormGroup;
  dataFromMerchant: any = {};
  selectedMethods: string[] = [];
  alreadyHasSellerId: boolean = false;

  constructor(
    private keycloakService: KeycloakService,
    private snackBar: MatSnackBar,
    private pspService: PspService,
    private formbuilder: FormBuilder,
  ) {
    this.paymentForm = this.formbuilder.group({
      paymentMethod: [''],
    });
  }

  ngOnInit() {
    this.selectedMethods = this.mapRolesToMethods();
    this.alreadyHasSellerId = this.selectedMethods.includes('card');
  }

  mapRolesToMethods() : string[] {
    let methods = ['card','paypal','crypto','qr'];
    let currentMethods : string[] = [];
    methods.forEach((method) => {
      if (this.keycloakService.isUserInRole(method.toUpperCase() + '_PAYMENT')) 
        currentMethods.push(method);
    })
    return currentMethods;
  }

  changeMethods(method: string) {
    if (this.selectedMethods.includes(method)) {
      this.selectedMethods = this.selectedMethods.filter((m) => m != method);
    }
    else {
      this.selectedMethods.push(method);
    }
  }

  submit() {
    console.log(this.selectedMethods);

    let accountNumber = null;
    if (!this.alreadyHasSellerId && this.selectedMethods.includes('card')) {
      console.log('modal');
      // todo: open modal for bank account number
      accountNumber = '1000987654321';
    }
    const obj = {
      selectedMethods: this.selectedMethods,
      sellerUsername: this.keycloakService.getKeycloakInstance().idTokenParsed!['email'],
      accountNumber: accountNumber
    };

    this.pspService.sendNewPaymentMethods(obj).subscribe({
      next: (res) => {
        console.log(res);
      },
      error: (err) => {
        console.log(err);
      }
    });
  }

  logout() {
    this.keycloakService.logout();
  }

  private displayError(message: string) {
    this.snackBar.open(message, 'Close', { duration: 5000})
  }
}
