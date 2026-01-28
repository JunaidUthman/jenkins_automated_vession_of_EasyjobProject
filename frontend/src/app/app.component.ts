import { Component, OnInit } from '@angular/core';
import { HeaderComponent } from './header/header.component';
import { HeroComponent } from './hero/hero.component';
import { InfoComponent } from './info/info.component';
import { FooterComponent } from './footer/footer.component';
import { LandingPageComponent } from "./landing-page/landing-page.component";
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, HeaderComponent, HeroComponent, InfoComponent, FooterComponent, LandingPageComponent, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  hideHeaderFooter = false;
  isRecrutter = false;

  constructor(private router: Router) {
    router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const url = (event as NavigationEnd).urlAfterRedirects;
      this.hideHeaderFooter = url === '/signup' || url === '/login';

      const role = localStorage.getItem('Roles');
      this.isRecrutter = role === 'Recrutter';


      if (this.isRecrutter && (url === '/' || url === '' || url === '/home')) {
        this.router.navigate(['/dashboard/home']);
      }
    });
  }

  ngOnInit() {
    const role = localStorage.getItem('Roles');
    this.isRecrutter = role === 'Recrutter';
  }
}

