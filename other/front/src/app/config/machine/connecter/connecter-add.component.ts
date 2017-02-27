import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../../api-service.service';
import { Machine } from '../machine';

@Component({
  selector: 'app-connecter-add',
  templateUrl: './connecter-add.component.html',
  styleUrls: ['./connecter-add.component.css'],
  providers: [ApiService]
})
export class ConnecterAddComponent implements OnInit {

  constructor(private api: ApiService) { }

  ngOnInit() {
    this.api.machines().toPromise().then(ms => this.machines = ms)
  }

  machines: Machine[] = []

  chooseTemplate(mode, category, mheId) {
    this.templateName = mode + '-' + category
    if (mode === "JDBC" && category === "ORACLE") {
      this.connecter = new Connecter(mode, category, mheId)
      this.jdbcConnecter = new JdbcConnecter()
    } else if (mode === "JDBC" && category === "MYSQL") {
      this.connecter = new Connecter(mode, category, mheId)
      this.jdbcConnecter = new JdbcConnecter()
    } else if (mode === "SSH" && category === "CENTOS") {
      this.connecter = new Connecter(mode, category, mheId)
      this.sshConnecter = new SshConnecter()
    }
  }

  connecter: Connecter
  jdbcConnecter: JdbcConnecter
  sshConnecter: SshConnecter

  templateName: string

  onSubmitForJdbc() {
    const modeInfo = JSON.stringify(this.jdbcConnecter)
    this.connecter.modeInfo = modeInfo
    // alert(JSON.stringify(this.connecter))
    this.api.machine_connecter_add(this.connecter).toPromise().then(p => alert(p))
  }
}


class Connecter {
  constructor(mode: string, category: string, mheId: number) {
    this.category = category
    this.mode = mode
    this.mheId = mheId
  }
  category: string
  categoryVersion: string
  label: string
  modeInfo: string
  state: boolean = true
  mode: string
  mheId: number
}

class JdbcConnecter {
  user: string
  password: string
  jdbcUrl: string
}

class SshConnecter {
  port: number
  user: string
  password: string
  privateKey: string
}


