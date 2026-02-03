import { Routes } from '@angular/router';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { SignupComponent } from './signup/signup.component';
import { LoginComponent } from './login/login.component';
import { TrendingJobsComponent } from './trending-jobs/trending-jobs.component';
import { RecrutterDashboardComponent } from './recrutter-dashboard/recrutter-dashboard.component';
import { RecrutterDashboardHomeComponent } from './recrutter-dashboard-home/recrutter-dashboard-home.component';
import { RecrutterJobsComponent } from './recrutter-jobs/recrutter-jobs.component';
import { MyApplicationsComponent } from './my-applications/my-applications.component';
import { AboutUsComponent } from './about-us/about-us.component';
import { ProfileComponent } from './profile/profile.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'home', component: LandingPageComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  { path: 'jobs', component: TrendingJobsComponent },
  { path: 'myApplications', component: MyApplicationsComponent },
  { path: 'about', component: AboutUsComponent },
  { path: 'profile', component: ProfileComponent },
  {
    path: 'dashboard',
    component: RecrutterDashboardComponent,
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', component: RecrutterDashboardHomeComponent },
      { path: 'jobs', component: RecrutterJobsComponent },

    ]
  },
];
