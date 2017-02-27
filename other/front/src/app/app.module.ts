import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { NavigationComponent } from './navigation.component';
import { ConfigComponent } from './config/config.component';
import { MachineComponent } from './config/machine/machine.component';
import { ConnecterJdbcComponent } from './config/machine/connecter/connecter-jdbc/connecter-jdbc.component';
import { MachineAddComponent } from './config/machine/machine-add.component';
import { MachineDeleteComponent } from './config/machine/machine-delete.component';
import { ConnecterComponent } from './config/machine/connecter/connecter.component';
import { ConnecterAddComponent } from './config/machine/connecter/connecter-add.component';
import { MonitorComponent } from './config/monitor/monitor.component';
import { MonitorAddComponent } from './config/monitor/monitor-add/monitor-add.component';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    NavigationComponent,
    ConfigComponent,
    MachineComponent,
    ConnecterJdbcComponent,
    MachineAddComponent,
    MachineDeleteComponent,
    ConnecterComponent,
    ConnecterAddComponent,
    MonitorComponent,
    MonitorAddComponent
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
