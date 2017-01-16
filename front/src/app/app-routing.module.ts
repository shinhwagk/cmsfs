import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ConfigComponent } from './config/config.component';
import { MachineComponent } from './config/machine/machine.component';

const routes: Routes = [
	{ path: '', redirectTo: '/config', pathMatch: 'full' },
	{
		path: 'config', component: ConfigComponent,
		children: [
			{ path: 'machines', component: MachineComponent }
		]
	},

];

@NgModule({
	imports: [RouterModule.forRoot(routes, { useHash: true })],
	exports: [RouterModule],
	providers: []
})
export class AppRoutingModule { }