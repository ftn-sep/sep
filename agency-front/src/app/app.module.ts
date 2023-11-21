import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductPageComponent } from './components/product-page/product-page.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HeaderComponent } from './components/header/header.component';
import { ProductItemsComponent } from './components/product-items/product-items.component';
import { DatePipe } from '@angular/common';

@NgModule({
  declarations: [
    AppComponent,
    ProductPageComponent,
    NavbarComponent,
    HeaderComponent,
    ProductItemsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [
    DatePipe,

  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
