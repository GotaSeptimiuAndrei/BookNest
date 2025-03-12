export interface AuthorSignupRequest {
    fullName: string
    email: string
    password: string
    dateOfBirth: string
    city: string
    country: string
    bio: string
    photoFile: File | null
}
