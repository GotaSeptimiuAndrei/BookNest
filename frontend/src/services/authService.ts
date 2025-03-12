import axios from "axios"
import { UserSignupRequest } from "../models/UserSignupRequest"
import { AuthorSignupRequest } from "../models/AuthorSignupRequest"
import { TokenResponse } from "../models/TokenResponse"
import { LoginRequest } from "../models/LoginRequest"
import { EmailVerificationRequest } from "../models/EmailVerificationRequest"

const api = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
})

export async function signUpUser(userData: UserSignupRequest): Promise<void> {
    await api.post("/auth/signup-user", userData)
}

export async function signUpAuthor(authorData: AuthorSignupRequest): Promise<void> {
    const formData = new FormData()
    formData.append("fullName", authorData.fullName)
    formData.append("email", authorData.email)
    formData.append("password", authorData.password)
    formData.append("dateOfBirth", authorData.dateOfBirth)
    formData.append("city", authorData.city)
    formData.append("country", authorData.country)
    formData.append("bio", authorData.bio)

    if (authorData.photoFile) formData.append("photo", authorData.photoFile)

    await api.post("/auth/signup-author", formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    })
}

export async function loginUser(payload: LoginRequest): Promise<TokenResponse> {
    const response = await api.post<TokenResponse>("/auth/login", payload)
    return response.data
}

export async function verifyEmail(payload: EmailVerificationRequest): Promise<void> {
    await api.post("/auth/verify-email", payload)
}

export function setAuthToken(token: string | null) {
    if (token) {
        api.defaults.headers.common["Authorization"] = `Bearer ${token}`
    } else {
        delete api.defaults.headers.common["Authorization"]
    }
}
