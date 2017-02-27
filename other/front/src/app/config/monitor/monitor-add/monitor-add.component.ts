import { Component, OnInit } from '@angular/core';
import { Monitor } from '../monitor';
import { ApiService } from '../../../api-service.service';

@Component({
  selector: 'app-monitor-add',
  templateUrl: './monitor-add.component.html',
  styleUrls: ['./monitor-add.component.css'],
  providers: [ApiService]
})
export class MonitorAddComponent implements OnInit {

  constructor(private api: ApiService) { }

  ngOnInit() {
  }

  monitor: Monitor = new Monitor()

  onSubmit() {
    this.monitor.mode = "SSH"
    this.monitor.modeId = 1
    // this.monitor.category = "ORACLE"
    // this.monitor.category_version = "ALL"
    alert(JSON.stringify(this.monitor))

    this.api.monitor_add(this.monitor).toPromise().then(p => alert("monitor add success"))
  }
}
