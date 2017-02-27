/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ConnecterJdbcComponent } from './connecter-jdbc.component';

describe('ConnecterJdbcComponent', () => {
  let component: ConnecterJdbcComponent;
  let fixture: ComponentFixture<ConnecterJdbcComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConnecterJdbcComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConnecterJdbcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
