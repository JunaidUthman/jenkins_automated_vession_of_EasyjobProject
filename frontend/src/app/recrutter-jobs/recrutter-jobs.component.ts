import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateJobFormComponent } from '../create-job-form/create-job-form.component';
import { JobserviceService } from '../service/jobServices/jobservice.service';

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
  logoUrl?: string; // 🔹 Add logoUrl
}

@Component({
  selector: 'app-recrutter-jobs',
  imports: [CommonModule, CreateJobFormComponent],
  templateUrl: './recrutter-jobs.component.html',
  styleUrls: ['./recrutter-jobs.component.css']
})
export class RecrutterJobsComponent implements OnInit {
  activeTab: string = localStorage.getItem('recrutterActiveTab') || 'trending-jobs';
  showCreateJobForm: boolean = false;
  showDeleteConfirm: boolean = false;
  showMenu: { [key: number]: boolean } = {};
  selectedJobForUpdate: Job | null = null;
  jobIdToDelete: number | null = null;
  isLoading = signal(false);

  jobs: Job[] = [];
  internships: Job[] = [];
  savedJobs: Job[] = [];

  constructor(private jobService: JobserviceService) { }

  ngOnInit() {
    this.fetchJobs();
  }

  fetchJobs() {
    this.isLoading.set(true);
    this.jobs = [];
    this.internships = [];
    this.jobService.getJobs().subscribe({
      next: (jobs: JobResponse[]) => {
        jobs.forEach(jobResponse => {
          const job = this.mapToJob(jobResponse);
          if (job.image) {
            this.jobService.getJobImage(job.image).subscribe({
              next: (imageUrl) => job.image = imageUrl,
              error: (err) => console.error('Image fetch error', err)
            });
          }
          if (job.companyLogo) {
            this.jobService.getJobImage(job.companyLogo).subscribe({
              next: (logoUrl) => job.logoUrl = logoUrl,
              error: (err) => console.error('Logo fetch error', err)
            });
          }
          if (job.type === 'JOB') {
            this.jobs.push(job);
          } else if (job.type === 'INTERNSHIP') {
            this.internships.push(job);
          }
        });
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error fetching jobs:', err);
        this.isLoading.set(false);
      }
    });
  }

  onJobCreated() {
    this.closeCreateJobForm();
    this.fetchJobs();
  }

  /** Converts a JobResponse (from API) into a typed Job object, preserving redundancy */
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
      case 'trending-jobs':
        return this.jobs;
      case 'trending-internships':
        return this.internships;
      default:
        return this.jobs;
    }
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
    localStorage.setItem('recrutterActiveTab', tab);
  }

  saveJob(jobId: number) {
    console.log('Job saved:', jobId);
  }

  seeCandidates(jobId: number) {
    console.log('See candidates for job:', jobId);
  }

  toggleMenu(jobId: number) {
    // Close all other menus
    Object.keys(this.showMenu).forEach(key => {
      if (+key !== jobId) {
        this.showMenu[+key] = false;
      }
    });
    this.showMenu[jobId] = !this.showMenu[jobId];
  }

  closeMenu(jobId: number) {
    this.showMenu[jobId] = false;
  }

  updateJob(jobId: number) {
    const job = this.jobs.concat(this.internships).find(j => j.id === jobId);
    if (job) {
      this.selectedJobForUpdate = job;
      this.showCreateJobForm = true;
    }
    this.showMenu[jobId] = false;
  }

  deleteJob(jobId: number) {
    this.jobIdToDelete = jobId;
    this.showDeleteConfirm = true;
    this.showMenu[jobId] = false;
  }

  confirmDelete() {
    if (this.jobIdToDelete) {
      this.jobService.deleteJob(this.jobIdToDelete).subscribe({
        next: () => {
          this.fetchJobs();
          this.showDeleteConfirm = false;
          this.jobIdToDelete = null;
        },
        error: (err) => {
          console.error('Delete error', err);
          alert('Failed to delete job');
        }
      });
    }
  }

  cancelDelete() {
    this.showDeleteConfirm = false;
    this.jobIdToDelete = null;
  }

  openCreateJobForm() {
    this.selectedJobForUpdate = null;
    this.showCreateJobForm = true;
  }

  closeCreateJobForm() {
    this.showCreateJobForm = false;
    this.selectedJobForUpdate = null;
  }
}
