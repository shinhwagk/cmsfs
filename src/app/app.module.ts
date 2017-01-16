import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { NavigationComponent } from './navigation.component';
import { ConfigComponent } from './config/config.component';
import { JdbcComponent } from './config/jdbc/jdbc.component';
import { MachineComponent } from './config/machine/machine.component';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    NavigationComponent,
    ConfigComponent,
    JdbcComponent,
    MachineComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
