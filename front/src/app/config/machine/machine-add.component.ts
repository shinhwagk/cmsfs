import { Component, OnInit } from '@angular/core';
import { Machine } from './machine';
import { ApiService } from '../../api-service.service';

@Component({
  selector: 'app-machine-add',
  templateUrl: './machine-add.component.html',
  styleUrls: ['./machine-add.component.css'],
  providers: [ApiService]
})
export class MachineAddComponent implements OnInit {

  constructor(private _api: ApiService) {
  }

  ngOnInit() {
  }

  machine: Machine = new Machine()

  addMachine() {
    this._api.machine_add(this.machine).toPromise().then(p => alert("machine add success"))
  }
}

