import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../../services/auth';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('Login', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, RouterTestingModule, HttpClientTestingModule],
      providers: [
        {
          provide: AuthService,
          useValue: {
            login: () => Promise.resolve(true),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
