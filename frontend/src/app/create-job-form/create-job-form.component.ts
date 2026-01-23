import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { JobserviceService } from '../service/jobServices/jobservice.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-job-form',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './create-job-form.component.html',
  styleUrl: './create-job-form.component.css'
})
export class CreateJobFormComponent {
  @Output() close = new EventEmitter<void>();

  jobForm: FormGroup;
  jobTypes = ['JOB', 'INTERNSHIP'];
  selectedFile: File | null = null;

  constructor(private fb: FormBuilder, private jobService: JobserviceService) {
    this.jobForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      type: ['', Validators.required],
      image: [null],
      company: ['', Validators.required],
      field: ['', Validators.required],
      jobFunction: ['', Validators.required],
      contract_type: ['', Validators.required],
      experienceMin: ['', Validators.required],
      experienceMax: ['', Validators.required],
      educationLevel: ['', Validators.required]
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  getCurrentUserId(): number {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    return user.id || 0;
  }

  onSubmit(): void {
    if (this.jobForm.valid) {
      const formValue = this.jobForm.value;

      this.jobService.createJob(
        formValue.title,
        formValue.description,
        formValue.location,
        formValue.type,
        this.selectedFile || undefined,
        formValue.company,
        formValue.field,
        formValue.jobFunction,
        formValue.contract_type,
        formValue.experienceMin,
        formValue.experienceMax,
        formValue.educationLevel
      ).subscribe({
        next: (response) => {
          // alert('Job created successfully');
          // Reset form or navigate
          this.jobForm.reset();
          this.selectedFile = null;
        },
        error: (error) => {
          console.error('Error creating job', error);
          alert('Failed to create job');
        }
      });
    }
  }
}
