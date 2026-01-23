import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// --------------------
// Request model (what we send to backend)
export interface JobRequest {
  title: string;
  description: string;
  location: string;
  type: string;
  image?: File;
  company: string;
  field: string;
  jobFunction: string;
  contract_type: string;
  experienceMin: string;
  experienceMax: string;
  educationLevel: string;
}

// --------------------
// Response model (what backend returns)
export interface JobResponse {
  id: number;
  title: string;
  description: string;
  location: string;
  type: string;
  image?: string; // filename or URL returned from backend
  company: string;
  field: string;
  jobFunction: string;
  contract_type: string;
  experienceMin: string;
  experienceMax: string;
  educationLevel: string;
  creator?: any;
}

@Injectable({
  providedIn: 'root'
})
export class JobserviceService {

  private apiUrl = `${environment.apiUrl}/jobs`;

  constructor(private http: HttpClient) {}

  // Create job method
  createJob(
    title: string,
    description: string,
    location: string,
    type: string,
    image?: File,
    company?: string,
    field?: string,
    jobFunction?: string,
    contract_type?: string,
    experienceMin?: string,
    experienceMax?: string,
    educationLevel?: string
  ): Observable<JobResponse> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('location', location);
    formData.append('type', type);
    if (image) {
      formData.append('image', image); // file uploaded to backend
    }
    if (company) formData.append('company', company);
    if (field) formData.append('field', field);
    if (jobFunction) formData.append('function', jobFunction);
    if (contract_type) formData.append('contract_type', contract_type);
    if (experienceMin) formData.append('experienceMin', experienceMin);
    if (experienceMax) formData.append('experienceMax', experienceMax);
    if (educationLevel) formData.append('educationLevel', educationLevel);

    console.log('FormData contents for debug:');
    formData.forEach((value, key) => {
      console.log(`${key}:`, value);
    });

    return this.http.post<JobResponse>(`${this.apiUrl}/createJob`, formData);
  }

  getJobs(): Observable<JobResponse[]> {
    return this.http.get<JobResponse[]>(`${this.apiUrl}/getJobs`);
  }

  getAllJobs(): Observable<JobResponse[]> {
    return this.http.get<JobResponse[]>(`${this.apiUrl}/getAllJobs`);
  }

  getJobImage(filename: string): Observable<string> {
    const url = `${environment.apiUrl}/jobs/images/${filename}`;
    return this.http.get(url, { responseType: 'blob' }).pipe(
      map(blob => URL.createObjectURL(blob)) 
    );
  }

  applyJob(jobId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/apply/${jobId}`, {});
  }

  getMyApplications(): Observable<JobResponse[]> {
    return this.http.get<JobResponse[]>(`${this.apiUrl}/getApplications`);
  }

  cancelApplication(jobId: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/cancelApplication/${jobId}`);
  }



}
