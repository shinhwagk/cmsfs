import { Component, OnInit } from '@angular/core';
import { Monitor } from '../monitor';

@Component({
  selector: 'app-monitor-add',
  templateUrl: './monitor-add.component.html',
  styleUrls: ['./monitor-add.component.css']
})
export class MonitorAddComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  monitor: Monitor = new Monitor()

  onSubmit() {
    alert(JSON.stringify(this.monitor))
  }
}
