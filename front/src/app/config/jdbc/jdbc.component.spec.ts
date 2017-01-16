/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { JdbcComponent } from './jdbc.component';

describe('JdbcComponent', () => {
  let component: JdbcComponent;
  let fixture: ComponentFixture<JdbcComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JdbcComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JdbcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
