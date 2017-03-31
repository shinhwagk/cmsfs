import { Component } from '@angular/core';
import { ApiService } from './api.service';
import { testData, CoreMonitorStatus, CoreMonitorStatusCollect } from './monitor-status';

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
      this.data = p
    })

  }

  data: CoreMonitorStatus[] = []

  setCurrentClasses(state) {
    return {
      "alert-success": state,
      "alert-danger": !state
    };
  }

}
