import { Component } from '@angular/core';
import { FormGroup, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { KeycloakService } from 'keycloak-angular';
import { ToastrService } from 'ngx-toastr';
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
  selectedMethods: string[] = [];
  accountNumberNotNeeded: boolean = false;

  constructor(
    private keycloakService: KeycloakService,
    private snackBar: MatSnackBar,
    private pspService: PspService,
    private formbuilder: FormBuilder,
    private toastrService: ToastrService,
  ) {
    this.paymentForm = this.formbuilder.group({
      paymentMethod: [''],
    });
  }

  ngOnInit() {
    const sellerUsername = this.keycloakService.getKeycloakInstance().idTokenParsed!['email'];
    this.pspService.getSubscribedPaymentMethodsByUsername(sellerUsername).subscribe({
      next: (res: any) => {
        this.selectedMethods = res.paymentMethods.map((m: string) => m.toLowerCase());
        this.accountNumberNotNeeded = res.hasMerchantIdAndPassword;
      },
      error: (err) => {
        this.displayError(err);
      }
    });
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
    let accountNumber = null;
    if (!this.accountNumberNotNeeded && this.selectedMethods.some(m => m === 'card' || m === 'qr')) {
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
        this.toastrService.success('Successfully changed subscriptions!');
      },
      error: (err) => {
        this.toastrService.error(err.error);
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
