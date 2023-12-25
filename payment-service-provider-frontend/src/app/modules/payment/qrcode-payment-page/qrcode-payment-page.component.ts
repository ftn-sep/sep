import { Component, OnInit, Input } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AcquirerBankService } from 'src/app/services/acquirer-bank/acquirer-bank.service';
import jsQR from 'jsqr';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-qrcode-payment-page',
  templateUrl: './qrcode-payment-page.component.html',
  styleUrl: './qrcode-payment-page.component.css',
})
export class QrcodePaymentPageComponent implements OnInit {
  amount!: number;
  description!: string;
  receiver!: string;
  receiverAccount!: string;
  qrCodeLoaded = false;
  qrCode: string = '';
  qrResultString: string = '';
  paymentId: string | null = null;
  uuid: string | null = null;
  amountrsd: number = 0;
  payer: string = 'Pera Peric';
  payerAccount: string = '100000098765432167';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private acquirerBankService: AcquirerBankService,
    private toastr: ToastrService
  ) {
    this.qrCode =
      'data:image/jpeg;base64,' +
      this.router.getCurrentNavigation()?.extras.state!['qrCode'];
  }

  ngOnInit() {}

  pay() {
    this.route.params.subscribe((params) => {
      this.uuid = params['uuid'];
      this.paymentId = params['paymentId'];
    });

    const paymentDetails = {
      cardHolderName: 'Pera Peric',
      pan: '5209302889842835',
      cardExpiresIn: '05/28',
      securityCode: '123',
      amount: this.amount,
      uuid: this.uuid,
      paymentId: this.paymentId,
    };

    this.acquirerBankService.generateUrl(paymentDetails).subscribe({
      next: (res: any) => {
        const parsedUrl = new URL(res);
        const path = parsedUrl.pathname;

        this.router.navigate([path]);
      },
      error: (err: any) => {
        this.toastr.error('Something went wrong, try again!', 'Error');
      },
    });
  }

  async scanQRCode() {
    const imageData = await this.base64ToImageData(this.qrCode);

    if (imageData) {
      const code = jsQR(imageData.data, imageData.width, imageData.height);

      if (code) {
        console.log('QR Code data:', code.data);
        // Do something with the QR code data
        this.qrResultString = code.data;
        let elements = this.qrResultString.split('|');
        this.receiver = elements[4].split(':')[1];
        this.receiverAccount = elements[3].split(':')[1];
        this.amountrsd = Number(elements[5].split(':RSD')[1].split(',')[0]);
        this.amount = this.amountrsd / 60;
        this.description = elements[7].split(':')[1];
        this.qrCodeLoaded = true;
      } else {
        console.log('No QR Code found');
      }
    }
  }

  base64ToImageData(base64: string): Promise<ImageData> {
    return new Promise((resolve, reject) => {
      const img = new Image();
      img.src = base64;

      img.onload = () => {
        const canvas = document.createElement('canvas');
        const context = canvas.getContext('2d');
        canvas.width = img.width;
        canvas.height = img.height;
        context!.drawImage(img, 0, 0, canvas.width, canvas.height);

        const imageData = context!.getImageData(
          0,
          0,
          canvas.width,
          canvas.height
        );
        resolve(imageData);
      };

      img.onerror = (error) => reject(error);
    });
  }
}
