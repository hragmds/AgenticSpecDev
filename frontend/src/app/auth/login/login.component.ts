import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

/**
 * Login component for user authentication.
 * 
 * Features:
 * - Reactive form with validation
 * - Demo credentials (demo/password)
 * - Error handling and display
 * - Loading state management
 * - Redirect to dashboard on success
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  
  loginForm: FormGroup;
  loading = false;
  error: string | null = null;
  showPassword = false;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(1)]],
      password: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit(): void {
    // Redirect if already authenticated
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  /**
   * Handle form submission.
   */
  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.markFormGroupTouched();
      return;
    }

    const { username, password } = this.loginForm.value;
    this.loading = true;
    this.error = null;

    this.authService.login(username, password).subscribe({
      next: (response) => {
        console.log('Login successful:', response.username);
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Login failed:', error);
        this.error = error.message || 'Login failed. Please check your credentials.';
        this.loading = false;
      }
    });
  }

  /**
   * Fill form with demo credentials.
   */
  fillDemoCredentials(): void {
    this.loginForm.patchValue({
      username: 'demo',
      password: 'password'
    });
  }

  /**
   * Toggle password visibility.
   */
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * Check if specific field has error.
   */
  hasFieldError(fieldName: string, errorType: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.hasError(errorType) && (field.dirty || field.touched));
  }

  /**
   * Get field error message.
   */
  getFieldErrorMessage(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    if (!field || !field.errors) return '';

    if (field.hasError('required')) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required`;
    }
    if (field.hasError('minlength')) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} must be at least 1 character`;
    }
    return 'Invalid input';
  }

  /**
   * Mark all form fields as touched to show validation errors.
   */
  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      if (control) {
        control.markAsTouched();
      }
    });
  }
}