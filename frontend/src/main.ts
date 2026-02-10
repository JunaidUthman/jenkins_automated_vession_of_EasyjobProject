import { bootstrapApplication } from '@angular/platform-browser';//test if the pipiline only run if it finds PRs(2)
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './app/interceptors/auth.interceptor';
import { appConfig } from './app/app.config';
import { importProvidersFrom } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';

import { provideToastr } from 'ngx-toastr';

bootstrapApplication(AppComponent, {
  providers: [
    // Register HttpClient + Interceptors
    provideHttpClient(withInterceptorsFromDi()),

    // Register your interceptor globally
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    provideAnimations(), // required animations providers
    provideToastr(), // Toastr providers

    // Keep your other providers
    ...(appConfig?.providers || [])
  ]
})
.catch(err => console.error(err));
