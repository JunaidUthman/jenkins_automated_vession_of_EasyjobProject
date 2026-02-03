import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService, CandidateProfile } from '../service/profileService/profile-service.service';
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
    profile: CandidateProfile = {
        username: '',
        email: '',
        currentJobTitle: 'Job Seeker',
        arabicPercent: 0,
        frenchPercent: 0,
        englishPercent: 0,
        openToRemote: false
    };
    isLoading = true;
    isUploading = false;
    activeTab = 'personal';
    selectedFile: File | null = null;
    imagePreview: string | null = null;

    constructor(
        private profileService: ProfileService,
        private toastr: ToastrService
    ) { }

    ngOnInit(): void {
        this.loadProfile();
    }

    loadProfile(): void {
        this.profileService.getProfile().subscribe({
            next: (data) => {
                this.profile = { ...this.profile, ...data };
                this.isLoading = false;
            },
            error: (err) => {
                this.toastr.error('Failed to load profile', 'Error');
                this.isLoading = false;
            }
        });
    }

    setTab(tab: string): void {
        this.activeTab = tab;
    }

    onFileSelected(event: any): void {
        const file = event.target.files[0];
        if (file) {
            this.selectedFile = file;

            // Create preview
            const reader = new FileReader();
            reader.onload = (e: any) => {
                this.imagePreview = e.target.result;
            };
            reader.readAsDataURL(file);

            // Upload immediately like most modern profile pages
            this.uploadPicture();
        }
    }

    uploadPicture(): void {
        if (this.selectedFile) {
            this.isUploading = true;
            this.profileService.uploadProfilePicture(this.selectedFile).subscribe({
                next: (res) => {
                    this.toastr.success('Profile picture updated', 'Success');
                    this.loadProfile(); // Reload to get the new filename
                    this.selectedFile = null;
                    this.imagePreview = null;
                    this.isUploading = false;
                },
                error: (err) => {
                    this.toastr.error('Failed to upload picture', 'Error');
                    this.isUploading = false;
                }
            });
        }
    }

    getProfileImageUrl(): string {
        if (this.imagePreview) return this.imagePreview;
        if (this.profile.profilePicture) {
            return this.profileService.getProfilePictureUrl(this.profile.profilePicture);
        }
        return 'assets/default-avatar.png'; // Make sure this exists or use a placeholder URL
    }

    onSubmit(): void {
        this.profileService.updateProfile(this.profile).subscribe({
            next: (res) => {
                this.toastr.success('Profile updated successfully', 'Success');
            },
            error: (err) => {
                this.toastr.error('Failed to update profile', 'Error');
            }
        });
    }
}
