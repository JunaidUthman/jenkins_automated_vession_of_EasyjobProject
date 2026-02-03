import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JobserviceService } from '../service/jobServices/jobservice.service';
import { ToastrService } from 'ngx-toastr';
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
  logoUrl?: string; // 🔹 Add logoUrl
}


@Component({
  selector: 'app-trending-jobs',
  imports: [CommonModule],
  templateUrl: './trending-jobs.component.html',
  styleUrl: './trending-jobs.component.css'
})



export class TrendingJobsComponent implements OnInit {

  constructor(private jobService: JobserviceService, private toastr: ToastrService) { }

  jobs: Job[] = [];
  internships: Job[] = [];
  savedJobs: Job[] = [];
  jobFetched = signal(true);
  jobImagesFetched = signal(true);
  applyingJobId: number | null = null;

  ngOnInit(): void {
    this.jobService.getAllJobs().subscribe({
      next: (jobs: JobResponse[]) => {
        this.jobFetched.set(false);

        jobs.forEach(jobResponse => {
          const job = this.mapToJob(jobResponse);

          if (job.image) {
            this.jobService.getJobImage(job.image).subscribe({
              next: (imageUrl) => {
                this.jobImagesFetched.set(false);
                job.imageUrl = imageUrl;
              },
              error: (err) => console.error('Image fetch error', err)
            });
          }

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
        console.error('Error fetching jobs:', err);
      }


    });

    // Load saved jobs from localStorage
    this.loadSavedJobs();
  }

  private loadSavedJobs(): void {
    const savedJobIds = JSON.parse(localStorage.getItem('savedJobs') || '[]');
    const allJobs = [...this.jobs, ...this.internships];
    this.savedJobs = allJobs.filter(job => savedJobIds.includes(job.id));
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
  activeTab: string = localStorage.getItem('trendingActiveTab') || 'trending-jobs';

  get displayedJobs(): Job[] {
    switch (this.activeTab) {
      case 'trending-jobs':
        return this.jobs;
      case 'trending-internships':
        return this.internships;
      case 'saved-jobs':
        return this.savedJobs;
      default:
        return this.jobs;
    }
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
    localStorage.setItem('trendingActiveTab', tab);
  }

  saveJob(jobId: number) {
    if (localStorage.getItem('Token') === null) {
      this.toastr.info('Please login to save jobs', 'Info');
      return;
    }

    const savedJobIds: number[] = JSON.parse(localStorage.getItem('savedJobs') || '[]');
    const index = savedJobIds.indexOf(jobId);

    if (index > -1) {
      // Job is already saved, remove it
      savedJobIds.splice(index, 1);
      this.savedJobs = this.savedJobs.filter(job => job.id !== jobId);
      this.toastr.info('Job removed from saved', 'Removed');
    } else {
      // Job is not saved, add it
      savedJobIds.push(jobId);
      const allJobs = [...this.jobs, ...this.internships];
      const jobToSave = allJobs.find(job => job.id === jobId);
      if (jobToSave && !this.savedJobs.find(job => job.id === jobId)) {
        this.savedJobs.push(jobToSave);
      }
      this.toastr.success('Job saved successfully', 'Saved');
    }

    localStorage.setItem('savedJobs', JSON.stringify(savedJobIds));
  }

  isJobSaved(jobId: number): boolean {
    const savedJobIds: number[] = JSON.parse(localStorage.getItem('savedJobs') || '[]');
    return savedJobIds.includes(jobId);
  }

  applyJob(jobId: number) {
    if (localStorage.getItem('Token') === null) {
      this.toastr.info('Please login to apply for jobs', 'Info');
    }
    else {
      this.applyingJobId = jobId;
      this.jobService.applyJob(jobId).subscribe({
        next: (res) => {
          console.log('Application response:', res);
          this.applyingJobId = null;

          // Check if already applied
          if (res.alreadyApplied === 'true') {
            this.toastr.warning(res.message, 'Already Applied');
          }
          // Check if successful
          else if (res.success === 'true') {
            this.toastr.success(res.message, 'Success');
          }
          // Fallback for any other response
          else {
            this.toastr.info(res.message, 'Info');
          }
        },
        error: (err) => {
          console.log('Error applying for job', err);
          this.applyingJobId = null;
          this.toastr.error('Failed to apply for job. Please try again.', 'Error');
        }
      });
    }
  }

  showSuccess() {
    this.toastr.success('Job Applied Successfully, You can see it in My Applications', 'Success');
  }
}
