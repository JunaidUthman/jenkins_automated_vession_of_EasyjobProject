import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { JobserviceService } from '../service/jobServices/jobservice.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-create-job-form',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './create-job-form.component.html',
  styleUrl: './create-job-form.component.css'
})
export class CreateJobFormComponent implements OnInit {
  @Input() jobToUpdate: any = null;
  @Output() close = new EventEmitter<void>();
  @Output() jobCreated = new EventEmitter<void>();

  jobForm: FormGroup;
  jobTypes = ['JOB', 'INTERNSHIP'];

  contractTypes = [
    'CDI', 'CDD', 'INTERIM', 'FREELANCE', 'STAGE',
    'ALTERNANCE', 'CONSULTANT', 'PART_TIME', 'FULL_TIME'
  ];

  educationLevels = [
    'SANS_DIPLOME', 'NIVEAU_BAC', 'BAC', 'BAC_PLUS_2',
    'BAC_PLUS_3', 'BAC_PLUS_5', 'DOCTORAT', 'FORMATION_PRO'
  ];

  selectedLogo: File | null = null;

  constructor(private fb: FormBuilder, private jobService: JobserviceService) {
    this.jobForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required],
      type: ['', Validators.required],
      logo: [null],
      company: ['', Validators.required],
      field: ['', Validators.required],
      jobFunction: ['', Validators.required],
      contract_type: ['', Validators.required],
      experienceMin: ['', Validators.required],
      experienceMax: ['', Validators.required],
      educationLevel: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.jobToUpdate) {
      this.jobForm.patchValue({
        title: this.jobToUpdate.title,
        description: this.jobToUpdate.description,
        location: this.jobToUpdate.location,
        type: this.jobToUpdate.type,
        company: this.jobToUpdate.company,
        field: this.jobToUpdate.field,
        jobFunction: this.jobToUpdate.jobFunction,
        contract_type: this.jobToUpdate.contract_type,
        experienceMin: this.jobToUpdate.experienceMin,
        experienceMax: this.jobToUpdate.experienceMax,
        educationLevel: this.jobToUpdate.educationLevel
      });
    }
  }

  get isUpdate(): boolean {
    return !!this.jobToUpdate;
  }

  onLogoSelected(event: any): void {
    this.selectedLogo = event.target.files[0];
  }

  onSubmit(): void {
    if (this.jobForm.valid) {
      const formValue = this.jobForm.value;

      const action = this.isUpdate
        ? this.jobService.updateJob(
          this.jobToUpdate.id,
          formValue.title,
          formValue.description,
          formValue.location,
          formValue.type,
          this.selectedLogo || undefined,
          formValue.company,
          formValue.field,
          formValue.jobFunction,
          formValue.contract_type,
          formValue.experienceMin,
          formValue.experienceMax,
          formValue.educationLevel
        )
        : this.jobService.createJob(
          formValue.title,
          formValue.description,
          formValue.location,
          formValue.type,
          this.selectedLogo || undefined,
          formValue.company,
          formValue.field,
          formValue.jobFunction,
          formValue.contract_type,
          formValue.experienceMin,
          formValue.experienceMax,
          formValue.educationLevel
        );

      action.subscribe({
        next: (response) => {
          this.jobForm.reset();
          this.selectedLogo = null;
          this.jobCreated.emit();
        },
        error: (error) => {
          console.error('Error with job action', error);
          alert('Failed to process job request');
        }
      });
    }
  }
}
