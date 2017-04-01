import { Component } from '@angular/core';
import { ApiService } from './api.service';
import { CoreMonitorStatus, CoreMonitorStatusCollect } from './monitor-status';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [ApiService]
})
export class AppComponent {
  title = 'test monitor!';

  constructor(private apiServicer: ApiService) {
    this.apiServicer.getMonitorStatuses().then(p => this.data = p)
    setInterval(() => this.apiServicer.getMonitorStatuses().then(p => this.data = p), 5000)
  }

  data: CoreMonitorStatus[] = []

  setCurrentClasses(state) {
    return {
      "alert-success": state,
      "alert-danger": !state
    };
  }

  chartUrl(metric, name) {
    return `http://10.65.103.63:3000/dashboard/db/${metric}?var-name=${name}`
  }

}
