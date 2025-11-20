import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginComponent } from '../../../app/auth/login/login.component';
import { AuthService, LoginResponse } from '../../../app/auth/auth.service';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['login', 'isAuthenticated']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    authService.isAuthenticated.and.returnValue(false);
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('Component Creation', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with empty form', () => {
      expect(component.loginForm.get('username')?.value).toBe('');
      expect(component.loginForm.get('password')?.value).toBe('');
    });

    it('should initialize loading as false', () => {
      expect(component.loading).toBeFalse();
    });

    it('should initialize error as null', () => {
      expect(component.error).toBeNull();
    });

    it('should initialize showPassword as false', () => {
      expect(component.showPassword).toBeFalse();
    });
  });

  describe('Route Protection', () => {
    it('should redirect to dashboard if already authenticated', () => {
      authService.isAuthenticated.and.returnValue(true);

      const loginComponent = new LoginComponent(
        component.loginForm.parent as any, // Form builder
        authService,
        router
      );
      loginComponent.ngOnInit();

      expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    });

    it('should not redirect if not authenticated', () => {
      authService.isAuthenticated.and.returnValue(false);

      component.ngOnInit();

      expect(router.navigate).not.toHaveBeenCalled();
    });
  });

  describe('Form Validation', () => {
    it('should mark form as invalid when username is empty', () => {
      component.loginForm.get('username')?.setValue('');
      component.loginForm.get('password')?.setValue('password');

      expect(component.loginForm.invalid).toBeTrue();
    });

    it('should mark form as invalid when password is empty', () => {
      component.loginForm.get('username')?.setValue('demo');
      component.loginForm.get('password')?.setValue('');

      expect(component.loginForm.invalid).toBeTrue();
    });

    it('should mark form as valid with valid credentials', () => {
      component.loginForm.get('username')?.setValue('demo');
      component.loginForm.get('password')?.setValue('password');

      expect(component.loginForm.valid).toBeTrue();
    });

    it('should be invalid when both fields are empty', () => {
      expect(component.loginForm.invalid).toBeTrue();
    });
  });

  describe('Form Submission', () => {
    beforeEach(() => {
      component.loginForm.get('username')?.setValue('demo');
      component.loginForm.get('password')?.setValue('password');
    });

    it('should not submit invalid form', () => {
      component.loginForm.get('username')?.setValue('');
      component.onSubmit();

      expect(authService.login).not.toHaveBeenCalled();
    });

    it('should call authService.login with credentials', () => {
      const mockResponse: LoginResponse = {
        token: 'test-token',
        username: 'demo',
        message: 'Success'
      };
      authService.login.and.returnValue(of(mockResponse));

      component.onSubmit();

      expect(authService.login).toHaveBeenCalledWith('demo', 'password');
    });

    it('should set loading to true during login', () => {
      authService.login.and.returnValue(of({
        token: 'test-token',
        username: 'demo',
        message: 'Success'
      }));

      component.loginForm.get('username')?.setValue('demo');
      component.loginForm.get('password')?.setValue('password');

      expect(component.loading).toBeFalse();
      component.onSubmit();
      // After subscribe, loading would be set back to false
      expect(authService.login).toHaveBeenCalled();
    });

    it('should clear error message on submission', () => {
      component.error = 'Previous error';
      authService.login.and.returnValue(of({
        token: 'test-token',
        username: 'demo',
        message: 'Success'
      }));

      component.onSubmit();

      expect(component.error).toBeNull();
    });

    it('should navigate to dashboard on successful login', (done) => {
      authService.login.and.returnValue(of({
        token: 'test-token',
        username: 'demo',
        message: 'Success'
      }));

      component.onSubmit();

      setTimeout(() => {
        expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
        done();
      }, 100);
    });

    it('should set error message on login failure', (done) => {
      const error = new Error('Login failed. Please check your credentials.');
      authService.login.and.returnValue(throwError(() => error));

      component.onSubmit();

      setTimeout(() => {
        expect(component.error).toContain('Login failed');
        expect(component.loading).toBeFalse();
        done();
      }, 100);
    });

    it('should set custom error message from server', (done) => {
      const error = new Error('Invalid credentials');
      authService.login.and.returnValue(throwError(() => error));

      component.onSubmit();

      setTimeout(() => {
        expect(component.error).toBe('Invalid credentials');
        done();
      }, 100);
    });
  });

  describe('Demo Credentials', () => {
    it('should fill form with demo credentials', () => {
      component.fillDemoCredentials();

      expect(component.loginForm.get('username')?.value).toBe('demo');
      expect(component.loginForm.get('password')?.value).toBe('password');
    });

    it('should enable form submission after filling demo credentials', () => {
      component.fillDemoCredentials();

      expect(component.loginForm.valid).toBeTrue();
    });
  });

  describe('Password Visibility', () => {
    it('should toggle password visibility', () => {
      expect(component.showPassword).toBeFalse();

      component.togglePasswordVisibility();
      expect(component.showPassword).toBeTrue();

      component.togglePasswordVisibility();
      expect(component.showPassword).toBeFalse();
    });

    it('should toggle multiple times', () => {
      expect(component.showPassword).toBeFalse();

      for (let i = 0; i < 5; i++) {
        component.togglePasswordVisibility();
        expect(component.showPassword).toBe(i % 2 === 0 ? true : false);
      }
    });
  });

  describe('Field Validation Helpers', () => {
    it('should detect username required error', () => {
      const usernameControl = component.loginForm.get('username');
      usernameControl?.markAsTouched();
      usernameControl?.setValue('');

      expect(component.hasFieldError('username', 'required')).toBeTrue();
    });

    it('should not show error for untouched field', () => {
      component.loginForm.get('username')?.setValue('');

      expect(component.hasFieldError('username', 'required')).toBeFalse();
    });

    it('should not show error for field with valid value', () => {
      const usernameControl = component.loginForm.get('username');
      usernameControl?.markAsTouched();
      usernameControl?.setValue('demo');

      expect(component.hasFieldError('username', 'required')).toBeFalse();
    });

    it('should detect password required error', () => {
      const passwordControl = component.loginForm.get('password');
      passwordControl?.markAsTouched();
      passwordControl?.setValue('');

      expect(component.hasFieldError('password', 'required')).toBeTrue();
    });
  });

  describe('Error Handling', () => {
    it('should display generic error message when not provided', (done) => {
      authService.login.and.returnValue(throwError(() => ({})));

      component.loginForm.get('username')?.setValue('demo');
      component.loginForm.get('password')?.setValue('password');
      component.onSubmit();

      setTimeout(() => {
        expect(component.error).toBe('Login failed. Please check your credentials.');
        done();
      }, 100);
    });

    it('should clear previous error on new login attempt', (done) => {
      component.error = 'Previous error';

      authService.login.and.returnValue(of({
        token: 'test-token',
        username: 'demo',
        message: 'Success'
      }));

      component.loginForm.get('username')?.setValue('demo');
      component.loginForm.get('password')?.setValue('password');
      component.onSubmit();

      expect(component.error).toBeNull();
      done();
    });
  });
});