import { TestBed } from '@angular/core/testing';
import { AuthService, LoginRequest, LoginResponse } from '../../app/auth/auth.service';
import { ApiService } from '../../app/shared/api.service';
import { of, throwError } from 'rxjs';

describe('AuthService', () => {
  let service: AuthService;
  let apiService: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    const apiServiceSpy = jasmine.createSpyObj('ApiService', ['post']);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: ApiService, useValue: apiServiceSpy }
      ]
    });

    service = TestBed.inject(AuthService);
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;

    // Clear localStorage before each test
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  describe('Service Creation', () => {
    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should initialize with no authentication', () => {
      expect(service.isAuthenticated()).toBeFalse();
      expect(service.getToken()).toBeNull();
      expect(service.getCurrentUsername()).toBeNull();
    });
  });

  describe('login() method', () => {
    const mockLoginRequest: LoginRequest = { username: 'demo', password: 'password' };
    const mockLoginResponse: LoginResponse = {
      token: 'mock-token-123',
      username: 'demo',
      message: 'Login successful'
    };

    it('should login successfully with valid credentials', (done) => {
      apiService.post.and.returnValue(of(mockLoginResponse));

      service.login(mockLoginRequest.username, mockLoginRequest.password).subscribe({
        next: (response) => {
          expect(response.token).toBe(mockLoginResponse.token);
          expect(response.username).toBe(mockLoginResponse.username);
          expect(service.isAuthenticated()).toBeTrue();
          expect(service.getToken()).toBe('mock-token-123');
          expect(service.getCurrentUsername()).toBe('demo');
          done();
        },
        error: done.fail
      });
    });

    it('should store token and username in localStorage after successful login', (done) => {
      apiService.post.and.returnValue(of(mockLoginResponse));

      service.login(mockLoginRequest.username, mockLoginRequest.password).subscribe({
        next: () => {
          expect(localStorage.getItem('banking_session_token')).toBe('mock-token-123');
          expect(localStorage.getItem('banking_username')).toBe('demo');
          done();
        },
        error: done.fail
      });
    });

    it('should update isAuthenticated$ observable on successful login', (done) => {
      apiService.post.and.returnValue(of(mockLoginResponse));

      service.isAuthenticated$.subscribe(isAuth => {
        if (isAuth) {
          expect(isAuth).toBeTrue();
          done();
        }
      });

      service.login(mockLoginRequest.username, mockLoginRequest.password).subscribe();
    });

    it('should update currentUser$ observable on successful login', (done) => {
      apiService.post.and.returnValue(of(mockLoginResponse));

      service.currentUser$.subscribe(username => {
        if (username) {
          expect(username).toBe('demo');
          done();
        }
      });

      service.login(mockLoginRequest.username, mockLoginRequest.password).subscribe();
    });

    it('should handle login failure with error', (done) => {
      const error = { error: { message: 'Invalid credentials' } };
      apiService.post.and.returnValue(throwError(() => error));

      service.login(mockLoginRequest.username, mockLoginRequest.password).subscribe({
        next: () => done.fail('Should have failed'),
        error: (err) => {
          expect(err.message).toContain('Invalid credentials');
          expect(service.isAuthenticated()).toBeFalse();
          done();
        }
      });
    });

    it('should handle login failure without error message', (done) => {
      apiService.post.and.returnValue(throwError(() => ({})));

      service.login(mockLoginRequest.username, mockLoginRequest.password).subscribe({
        next: () => done.fail('Should have failed'),
        error: (err) => {
          expect(err.message).toBe('Login failed');
          done();
        }
      });
    });
  });

  describe('logout() method', () => {
    beforeEach(() => {
      // Set up authenticated session
      localStorage.setItem('banking_session_token', 'test-token');
      localStorage.setItem('banking_username', 'demo');
    });

    it('should clear authentication state', () => {
      service.logout();
      expect(service.isAuthenticated()).toBeFalse();
    });

    it('should clear token from localStorage', () => {
      service.logout();
      expect(localStorage.getItem('banking_session_token')).toBeNull();
    });

    it('should clear username from localStorage', () => {
      service.logout();
      expect(localStorage.getItem('banking_username')).toBeNull();
    });

    it('should update isAuthenticated$ observable', (done) => {
      service.logout();

      service.isAuthenticated$.subscribe(isAuth => {
        expect(isAuth).toBeFalse();
        done();
      });
    });

    it('should update currentUser$ observable to null', (done) => {
      service.logout();

      service.currentUser$.subscribe(username => {
        expect(username).toBeNull();
        done();
      });
    });
  });

  describe('isAuthenticated() method', () => {
    it('should return false when no session exists', () => {
      expect(service.isAuthenticated()).toBeFalse();
    });

    it('should return true when valid session exists', () => {
      localStorage.setItem('banking_session_token', 'test-token');
      localStorage.setItem('banking_username', 'demo');

      expect(service.isAuthenticated()).toBeTrue();
    });

    it('should return false when only token exists', () => {
      localStorage.setItem('banking_session_token', 'test-token');

      expect(service.isAuthenticated()).toBeFalse();
    });

    it('should return false when only username exists', () => {
      localStorage.setItem('banking_username', 'demo');

      expect(service.isAuthenticated()).toBeFalse();
    });
  });

  describe('Token Management', () => {
    it('should get stored token', () => {
      localStorage.setItem('banking_session_token', 'test-token-123');

      expect(service.getToken()).toBe('test-token-123');
    });

    it('should return null when no token exists', () => {
      expect(service.getToken()).toBeNull();
    });

    it('should get stored username', () => {
      localStorage.setItem('banking_username', 'test-user');

      expect(service.getCurrentUsername()).toBe('test-user');
    });

    it('should return null when no username exists', () => {
      expect(service.getCurrentUsername()).toBeNull();
    });
  });

  describe('Authentication Observables', () => {
    it('should emit false for isAuthenticated$ when not authenticated', (done) => {
      service.isAuthenticated$.subscribe(isAuth => {
        expect(isAuth).toBeFalse();
        done();
      });
    });

    it('should emit null for currentUser$ when not authenticated', (done) => {
      service.currentUser$.subscribe(username => {
        expect(username).toBeNull();
        done();
      });
    });

    it('should emit true and username after login', (done) => {
      const mockResponse: LoginResponse = {
        token: 'test-token',
        username: 'demo',
        message: 'Success'
      };

      apiService.post.and.returnValue(of(mockResponse));

      let authChecks = 0;
      service.isAuthenticated$.subscribe(isAuth => {
        if (isAuth) {
          authChecks++;
        }
      });

      service.login('demo', 'password').subscribe(() => {
        expect(authChecks).toBeGreaterThan(0);
        done();
      });
    });
  });
});