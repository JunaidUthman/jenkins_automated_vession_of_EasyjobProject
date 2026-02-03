import { Component, OnInit, signal } from '@angular/core';
import { JobserviceService } from '../service/jobServices/jobservice.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';

interface JobResponse {
  id: number;
  title: string;
  description: string;
  location: string;
  type: string;
  image?: string;
  companyLogo?: string;
  company: string;
  field: string;
  jobFunction: string;
  contract_type: string;
  experienceMin: string;
  experienceMax: string;
  educationLevel: string;
  createdAt?: string;
  creator?: any;
}

interface Job extends JobResponse {
  imageUrl?: string;
  logoUrl?: string;
}

@Component({
  selector: 'app-my-applications',
  imports: [CommonModule],
  templateUrl: './my-applications.component.html',
  styleUrl: './my-applications.component.css'
})
export class MyApplicationsComponent implements OnInit {

  constructor(private jobService: JobserviceService, private toastr: ToastrService) { }

  jobs: Job[] = [];
  internships: Job[] = [];
  isLoading = true; // Task 4: Loading spinner state
  cancellingJobId: number | null = null; // Spinner for cancel action

  activeTab: string = 'jobs';

  ngOnInit(): void {
    this.loadApplications();
  }

  loadApplications() {
    this.isLoading = true;
    this.jobs = [];
    this.internships = [];

    this.jobService.getMyApplications().subscribe({
      next: (jobs: JobResponse[]) => {
        this.isLoading = false;

        jobs.forEach(jobResponse => {
          const job = this.mapToJob(jobResponse);

          // Fetch Job Image
          if (job.image) {
            this.jobService.getJobImage(job.image).subscribe({
              next: (imageUrl) => {
                job.imageUrl = imageUrl;
              },
              error: (err) => console.error('Image fetch error', err)
            });
          }

          // Fetch Company Logo
          if (job.companyLogo) {
            this.jobService.getJobImage(job.companyLogo).subscribe({
              next: (logoUrl) => {
                job.logoUrl = logoUrl;
              },
              error: (err) => console.error('Logo fetch error', err)
            });
          }

          if (job.type === 'JOB') {
            this.jobs.push(job);
          } else if (job.type === 'INTERNSHIP') {
            this.internships.push(job);
          }
        });
      },
      error: (err) => {
        console.error('Error fetching applications:', err);
        this.isLoading = false;
        this.toastr.error('Failed to load applications', 'Error');
      }
    });
  }

  private mapToJob(jobResponse: JobResponse): Job {
    const typeUpper = jobResponse.type?.toUpperCase();
    const normalizedType: 'JOB' | 'INTERNSHIP' =
      typeUpper === 'INTERNSHIP' ? 'INTERNSHIP' : 'JOB';

    return {
      ...jobResponse,
      type: normalizedType
    } as Job;
  }

  get displayedJobs(): Job[] {
    switch (this.activeTab) {
      case 'jobs':
        return this.jobs;
      case 'internships':
        return this.internships;
      default:
        return this.jobs;
    }
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  cancelApplication(jobId: number) {
    if (confirm('Are you sure you want to cancel this application?')) {
      this.cancellingJobId = jobId;
      this.jobService.cancelApplication(jobId).subscribe({
        next: (res) => {
          console.log('Application cancelled successfully', res);
          this.cancellingJobId = null;
          this.toastr.success('Application cancelled successfully', 'Success');

          // Remove from list
          this.jobs = this.jobs.filter(j => j.id !== jobId);
          this.internships = this.internships.filter(j => j.id !== jobId);
        },
        error: (err) => {
          console.error('Error cancelling application', err);
          this.cancellingJobId = null;
          this.toastr.error('Failed to cancel application', 'Error');
        }
      });
    }
  }
}
