import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ConfigComponent } from './config/config.component';
import { MachineComponent } from './config/machine/machine.component';
import { MachineAddComponent } from './config/machine/machine-add.component';
import { ConnecterComponent } from './config/machine/connecter/connecter.component';
import { ConnecterAddComponent } from './config/machine/connecter/connecter-add.component';
import { MonitorAddComponent } from './config/monitor/monitor-add/monitor-add.component';

const routes: Routes = [
	{ path: '', redirectTo: '/config', pathMatch: 'full' },
	{
		path: 'config', component: ConfigComponent,
		children: [
			{ path: 'machines', component: MachineComponent },
			{ path: 'machine/add', component: MachineAddComponent },
			{ path: 'machine/delete', component: MachineAddComponent },
			{ path: 'machine/connecters', component: ConnecterComponent },
			{ path: 'machine/connecter/add', component: ConnecterAddComponent },
			{ path: 'monitor/add', component: MonitorAddComponent }
		]
	},

];

@NgModule({
	imports: [RouterModule.forRoot(routes, { useHash: true })],
	exports: [RouterModule],
	providers: []
})
export class AppRoutingModule { }