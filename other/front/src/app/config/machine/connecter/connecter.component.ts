import { Component, OnInit } from '@angular/core';

import { ApiService } from '../../../api-service.service';
import { Machine } from '../machine';

@Component({
  selector: 'app-connecter',
  templateUrl: './connecter.component.html',
  styleUrls: ['./connecter.component.css'],
  providers:[ApiService]
})
export class ConnecterComponent implements OnInit {

  constructor(private api: ApiService) { }

  ngOnInit() {
    this.api.machines().toPromise().then(ms => this.machines = ms
    )
  }

  machines: Machine[] = []

}
