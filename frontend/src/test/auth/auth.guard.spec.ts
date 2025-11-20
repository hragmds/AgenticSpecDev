import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthGuard, LoginGuard } from '../../app/auth/auth.guard';
import { AuthService } from '../../app/auth/auth.service';
import { BehaviorSubject } from 'rxjs';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;
  let isAuthenticatedSubject: BehaviorSubject<boolean>;

  beforeEach(() => {
    isAuthenticatedSubject = new BehaviorSubject<boolean>(false);

    const authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      isAuthenticated$: isAuthenticatedSubject.asObservable()
    });
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    guard = TestBed.inject(AuthGuard);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  describe('canActivate()', () => {
    let route: ActivatedRouteSnapshot;
    let state: RouterStateSnapshot;

    beforeEach(() => {
      route = {} as ActivatedRouteSnapshot;
      state = { url: '/dashboard' } as RouterStateSnapshot;
    });

    it('should allow navigation when user is authenticated', (done) => {
      isAuthenticatedSubject.next(true);

      const result = guard.canActivate(route, state);

      // Handle both Observable and boolean returns
      if (result === true) {
        expect(result).toBeTrue();
        done();
      } else {
        (result as any).subscribe((canActivate: boolean) => {
          expect(canActivate).toBeTrue();
          done();
        });
      }
    });

    it('should deny navigation when user is not authenticated', (done) => {
      isAuthenticatedSubject.next(false);

      const result = guard.canActivate(route, state);

      (result as any).subscribe((canActivate: boolean) => {
        expect(canActivate).toBeFalse();
        done();
      });
    });

    it('should redirect to login when user is not authenticated', (done) => {
      isAuthenticatedSubject.next(false);
      const testUrl = '/protected-page';
      state = { url: testUrl } as RouterStateSnapshot;

      const result = guard.canActivate(route, state);

      (result as any).subscribe(() => {
        expect(router.navigate).toHaveBeenCalledWith(
          ['/login'],
          { queryParams: { returnUrl: testUrl } }
        );
        done();
      });
    });

    it('should pass the attempted URL as returnUrl parameter', (done) => {
      isAuthenticatedSubject.next(false);
      state = { url: '/accounts' } as RouterStateSnapshot;

      const result = guard.canActivate(route, state);

      (result as any).subscribe(() => {
        expect(router.navigate).toHaveBeenCalledWith(
          ['/login'],
          jasmine.objectContaining({
            queryParams: { returnUrl: '/accounts' }
          })
        );
        done();
      });
    });

    it('should not redirect when user is authenticated', (done) => {
      isAuthenticatedSubject.next(true);

      const result = guard.canActivate(route, state);

      (result as any).subscribe(() => {
        expect(router.navigate).not.toHaveBeenCalled();
        done();
      });
    });

    it('should handle state with query parameters', (done) => {
      isAuthenticatedSubject.next(false);
      state = { url: '/transfers?from=1&to=2' } as RouterStateSnapshot;

      const result = guard.canActivate(route, state);

      (result as any).subscribe(() => {
        expect(router.navigate).toHaveBeenCalledWith(
          ['/login'],
          { queryParams: { returnUrl: '/transfers?from=1&to=2' } }
        );
        done();
      });
    });

    it('should only take one emission from isAuthenticated$ observable', (done) => {
      let emissionCount = 0;
      isAuthenticatedSubject.next(false);

      const result = guard.canActivate(route, state);

      (result as any).subscribe(() => {
        emissionCount++;
        expect(emissionCount).toBe(1);
        done();
      });
    });
  });

  describe('Guard Initialization', () => {
    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should have canActivate method', () => {
      expect(guard.canActivate).toBeDefined();
      expect(typeof guard.canActivate).toBe('function');
    });
  });
});

describe('LoginGuard', () => {
  let guard: LoginGuard;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;
  let isAuthenticatedSubject: BehaviorSubject<boolean>;

  beforeEach(() => {
    isAuthenticatedSubject = new BehaviorSubject<boolean>(false);

    const authServiceSpy = jasmine.createSpyObj('AuthService', [], {
      isAuthenticated$: isAuthenticatedSubject.asObservable()
    });
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        LoginGuard,
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    guard = TestBed.inject(LoginGuard);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  describe('canActivate()', () => {
    it('should allow navigation when user is not authenticated', (done) => {
      isAuthenticatedSubject.next(false);

      const result = guard.canActivate();

      if (result === true) {
        expect(result).toBeTrue();
        done();
      } else {
        (result as any).subscribe((canActivate: boolean) => {
          expect(canActivate).toBeTrue();
          done();
        });
      }
    });

    it('should deny navigation when user is authenticated', (done) => {
      isAuthenticatedSubject.next(true);

      const result = guard.canActivate();

      (result as any).subscribe((canActivate: boolean) => {
        expect(canActivate).toBeFalse();
        done();
      });
    });

    it('should redirect authenticated users to dashboard', (done) => {
      isAuthenticatedSubject.next(true);

      const result = guard.canActivate();

      (result as any).subscribe(() => {
        expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
        done();
      });
    });

    it('should not redirect unauthenticated users', (done) => {
      isAuthenticatedSubject.next(false);

      const result = guard.canActivate();

      (result as any).subscribe(() => {
        expect(router.navigate).not.toHaveBeenCalled();
        done();
      });
    });

    it('should only take one emission from isAuthenticated$ observable', (done) => {
      let emissionCount = 0;
      isAuthenticatedSubject.next(false);

      const result = guard.canActivate();

      (result as any).subscribe(() => {
        emissionCount++;
        expect(emissionCount).toBe(1);
        done();
      });
    });

    it('should prevent already authenticated users from accessing login page', (done) => {
      isAuthenticatedSubject.next(true);

      const result = guard.canActivate();

      (result as any).subscribe((canActivate: boolean) => {
        expect(canActivate).toBeFalse();
        expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
        done();
      });
    });
  });

  describe('Guard Initialization', () => {
    it('should be created', () => {
      expect(guard).toBeTruthy();
    });

    it('should have canActivate method', () => {
      expect(guard.canActivate).toBeDefined();
      expect(typeof guard.canActivate).toBe('function');
    });
  });

  describe('Multiple Authentication State Changes', () => {
    it('should handle transition from unauthenticated to authenticated', (done) => {
      isAuthenticatedSubject.next(false);

      const firstResult = guard.canActivate();

      (firstResult as any).subscribe((canActivate: boolean) => {
        expect(canActivate).toBeTrue();

        // Simulate user logging in
        isAuthenticatedSubject.next(true);

        const secondResult = guard.canActivate();
        (secondResult as any).subscribe((secondCanActivate: boolean) => {
          expect(secondCanActivate).toBeFalse();
          done();
        });
      });
    });
  });
});