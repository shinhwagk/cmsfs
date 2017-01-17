import { Component, OnInit } from '@angular/core';

import { ApiService } from '../../api-service.service';
import { Machine } from './machine';

@Component({
  selector: 'app-machine',
  templateUrl: './machine.component.html',
  styleUrls: ['./machine.component.css'],
  providers: [ApiService]
})
export class MachineComponent implements OnInit {

  constructor(private api: ApiService) { }

  ngOnInit() {
    this.api.machines().toPromise().then(ms => this.machines = ms
    )
  }

  machines: Machine[] = []
}
