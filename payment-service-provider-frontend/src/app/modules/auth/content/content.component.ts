import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormGroup, FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { KeycloakService } from 'keycloak-angular';
import { ToastrService } from 'ngx-toastr';
import { PspService } from 'src/app/services/psp/psp.service';

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule,
    CommonModule
  ],
  styleUrl: './content.component.css'
})
export class ContentComponent {
  paymentForm: FormGroup;
  selectedMethods: string[] = [];
  accountNumberNotNeeded: boolean = false;
  showInputForAccNumber: boolean = false;

  constructor(
    private keycloakService: KeycloakService,
    private snackBar: MatSnackBar,
    private pspService: PspService,
    private formbuilder: FormBuilder,
    private toastrService: ToastrService,
  ) {
    this.paymentForm = this.formbuilder.group({
      paymentMethod: [''],
      accountNumber: ['']
    });
  }

  ngOnInit() {
    this.showInputForAccNumber = false;

    const sellerUsername = this.keycloakService.getKeycloakInstance().idTokenParsed!['email'];
    this.pspService.getSubscribedPaymentMethodsByUsername(sellerUsername).subscribe({
      next: (res: any) => {
        this.selectedMethods = res.paymentMethods.map((m: string) => m.toLowerCase());
        this.accountNumberNotNeeded = res.hasMerchantIdAndPassword;
      },
      error: (err) => {
        this.toastrService.error(err.error);
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

    if (this.isNeededAccNumber()) {
      this.showInputForAccNumber = true;
    }
    else this.showInputForAccNumber = false;

  }

  isNeededAccNumber() {
    return !this.accountNumberNotNeeded && this.selectedMethods.some(m => m === 'card' || m === 'qr');
  }
  submit() {
    const obj = {
      selectedMethods: this.selectedMethods,
      sellerUsername: this.keycloakService.getKeycloakInstance().idTokenParsed!['email'],
      accountNumber: this.paymentForm.value.accountNumber
    };

    if (!obj.sellerUsername || (!obj.accountNumber && this.isNeededAccNumber())) {
      this.toastrService.error('Fill Inputs');
      return;
    }

    this.pspService.sendNewPaymentMethods(obj).subscribe({
      next: (res) => {
        this.toastrService.success('Successfully changed subscriptions!');
        // send res.sellerId to merchant
      },
      error: (err) => {
        this.toastrService.error("Something went wrong");
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
