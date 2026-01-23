import { Component , OnInit , signal } from '@angular/core';
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
  creator?: any;
}

interface Job {
  id: number;
  title: string;
  description: string;
  location: string;
  type: string;
  image?: string;
  imageUrl?: string; // Add separate property for blob URL
  creator?: any;
}


@Component({
  selector: 'app-trending-jobs',
  imports: [CommonModule ],
  templateUrl: './trending-jobs.component.html',
  styleUrl: './trending-jobs.component.css'
})



export class TrendingJobsComponent implements OnInit {

  constructor(private jobService: JobserviceService,private toastr:ToastrService) {}

  jobs: Job[] = [];
  internships: Job[] = [];
  savedJobs: Job[] = [];
  jobFetched = signal(true);
  jobImagesFetched = signal(true);

  ngOnInit(): void {
    this.jobService.getAllJobs().subscribe({
    next: (jobs: JobResponse[]) => {
        this.jobFetched.set(false);

        jobs.forEach(jobResponse => {
          const job = this.mapToJob(jobResponse);

          
          this.jobService.getJobImage(job.image?? '').subscribe({
            next: (imageUrl) => {
              this.jobImagesFetched.set(false);
              job.imageUrl = imageUrl; // Set blob URL when loaded
            },
            error: (err) => console.error('Image fetch error', err)
        });

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
  activeTab: string = 'trending-jobs';
  
  

  get displayedJobs(): Job[] {
    switch(this.activeTab) {
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
  }

  saveJob(jobId: number) {
    if(localStorage.getItem('Token')===null){
      this.toastr.info('Please login to save jobs','Info');
    }
    else{
      this.toastr.info('this service is not active right now','Info');
    }
  }

  applyJob(jobId: number) {
    if(localStorage.getItem('Token')===null){
      this.toastr.info('Please login to apply for jobs','Info');
    }
    else{
      this.jobService.applyJob(jobId).subscribe({
      next : (res) => {
        console.log('Applied Successfully',res);
        this.showSuccess();
      },
      error: (err) => {
        console.log('Error applying for job',err);
      }
    });
    }
    
  }
  
  showSuccess() {
    this.toastr.success('Job Applied Successfully, You can see it in My Applications', 'Success');
  }
}
