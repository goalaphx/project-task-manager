import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from './services/auth';
import { UserService } from './services/user.service';
import { of } from 'rxjs';
import { HttpClientModule } from '@angular/common/http';
describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App, RouterTestingModule, HttpClientModule],
      providers: [
        {
          provide: AuthService,
          useValue: {
            getCurrentUser: () => Promise.resolve('test-user'),
            logout: () => Promise.resolve(),
          },
        },
        {
          provide: UserService,
          useValue: {
            getUser$: () => of('test-user'),
          },
        },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
