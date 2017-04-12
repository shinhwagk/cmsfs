import { Component } from '@angular/core';
import { ApiService } from './api.service';
import { CoreMonitorStatus } from './monitor-status';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [ApiService]
})
export class AppComponent {
  title = 'test monitor!';

  constructor(private apiServicer: ApiService) {
    this.apiServicer.getMonitorStatuses().then(p => {
      this.categorys = Array.from(new Set(this.data.map(p => p.category)))
      this.metrics = Array.from(new Set(this.data.map(p => p.metric)))
      this.names = Array.from(new Set(this.data.map(p => p.name)))
      this.data = p
      this.echoData = p
    });
    setInterval(() => this.apiServicer.getMonitorStatuses().then(p => {
      this.categorys = Array.from(new Set(this.data.map(p => p.category)))
      this.metrics = Array.from(new Set(this.data.map(p => p.metric)))
      this.names = Array.from(new Set(this.data.map(p => p.name)))
      this.data = p
    }), 5000);
  }

  data: CoreMonitorStatus[] = [];
  echoData: CoreMonitorStatus[] = []

  categorys: string[] = [];
  metrics: string[] = [];
  names: string[] = [];

  category: string
  metric: string
  name: string

  filter() {
    var filterData: CoreMonitorStatus[] = this.data

    if (this.name) {
      filterData = filterData.filter(j => j.name == this.name)
    }
    if (this.category) {
      filterData = filterData.filter(j => j.category == this.category)
    }
    if (this.metric) {
      filterData = filterData.filter(j => j.metric == this.metric)
    }

    this.echoData = filterData
  }

  setCurrentClasses(state) {
    return {
      'alert-success': state,
      'alert-danger': !state
    };
  }

  chartUrl(metric, name) {
    console.info(metric)
    return `http://10.65.103.63:3000/dashboard/db/${metric}?var-name=${name}`;
  }

}
