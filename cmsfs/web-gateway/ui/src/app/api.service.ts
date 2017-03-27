import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';

import 'rxjs/add/operator/toPromise';

import { CoreMonitorStatus } from './monitor-status';

@Injectable()
export class ApiService {

  private headers = new Headers({ 'Content-Type': 'application/json' });
  private monitorStatusUrl = 'v1/core/monitor';  // URL to web api

  constructor(private http: Http) { }

  getMonitorStatuses(): Promise<CoreMonitorStatus[]> {
    const url = `${this.monitorStatusUrl}/statues`
    return this.http.get(url)
      .toPromise()
      .then(response => response.json().data as CoreMonitorStatus[])
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

}
