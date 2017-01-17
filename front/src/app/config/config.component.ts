import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';


@Component({
	selector: 'app-config',
	templateUrl: './config.component.html',
	styleUrls: ['./config.component.css']
})
export class ConfigComponent implements OnInit {

	constructor(private router: Router) { }

	ngOnInit() {
	}

	gotoMachines() {
		this.router.navigate(['/config/machines'])
	}

	gotoMachineAdd() {
		this.router.navigate(['/config/machine/add'])
	}

	gotoMachineConnecterAdd() {
		this.router.navigate(['/config/machine/connecter/add'])
	}

	gotoMonitorAdd() {
		this.router.navigate(['/config/monitor/add'])
	}
}
