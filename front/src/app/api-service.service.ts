import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Observable';

import { Machine } from './config/machine/machine';


@Injectable()
export class ApiService {

  constructor(private http: Http) { }

  machine_add(machine: Machine) {
    return this.http.post('/v1/machine', machine, this.options).map((res: Response) => res.status)
  }

  machines() {
    return this.http.get('/v1/machines').map((res: Response) => res.json() || [])
  }

  machine_connecter_add(machine_connecter) {
    return this.http.post('/v1/machine/connecter', machine_connecter, this.options).map((res: Response) => res.status)
  }

  headers = new Headers({ 'Content-Type': 'application/json' });
  options = new RequestOptions({ headers: this.headers });
}
