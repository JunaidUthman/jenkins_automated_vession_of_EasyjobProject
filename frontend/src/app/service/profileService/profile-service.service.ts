import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CandidateProfile {
    username: string;
    email: string;
    dateOfBirth?: string;
    phone?: string;
    city?: string;
    country?: string;
    currentJobTitle?: string;
    yearsOfExperience?: number;
    educationLevel?: string;
    skills?: string;
    arabicPercent?: number;
    frenchPercent?: number;
    englishPercent?: number;
    preferredContract?: string;
    preferredLocation?: string;
    openToRemote?: boolean;
    cvUrl?: string;
    portfolioUrl?: string;
    linkedinUrl?: string;
    profilePicture?: string;
}

@Injectable({
    providedIn: 'root'
})
export class ProfileService {
    private apiUrl = 'http://localhost:8080/api/profile';

    constructor(private http: HttpClient) { }

    private getHeaders(): HttpHeaders {
        const token = localStorage.getItem('Token');
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });
    }

    getProfile(): Observable<CandidateProfile> {
        return this.http.get<CandidateProfile>(this.apiUrl, { headers: this.getHeaders() });
    }

    updateProfile(profile: CandidateProfile): Observable<any> {
        return this.http.put(this.apiUrl, profile, { headers: this.getHeaders() });
    }

    uploadProfilePicture(file: File): Observable<any> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post(`${this.apiUrl}/upload-picture`, formData, { headers: this.getHeaders() });
    }

    getProfilePictureUrl(filename: string): string {
        return `http://localhost:8080/api/jobs/images/${filename}`;
    }
}
