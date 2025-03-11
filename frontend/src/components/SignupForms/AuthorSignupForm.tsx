// src/components/AuthorFields.tsx
import React from "react"
import { TextField } from "@mui/material"

interface AuthorFieldsProps {
    fullName: string
    setFullName: (value: string) => void
    email: string
    setEmail: (value: string) => void
    password: string
    setPassword: (value: string) => void
    dateOfBirth: string
    setDateOfBirth: (value: string) => void
    city: string
    setCity: (value: string) => void
    country: string
    setCountry: (value: string) => void
    bio: string
    setBio: (value: string) => void
    photoFile: File | null
    setPhotoFile: (file: File | null) => void
}

const AuthorSignupForm: React.FC<AuthorFieldsProps> = ({
    fullName,
    setFullName,
    email,
    setEmail,
    password,
    setPassword,
    dateOfBirth,
    setDateOfBirth,
    city,
    setCity,
    country,
    setCountry,
    bio,
    setBio,
    photoFile,
    setPhotoFile,
}) => {
    return (
        <>
            <TextField
                label="Full Name"
                fullWidth
                margin="normal"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
            />
            <TextField
                label="Email"
                fullWidth
                margin="normal"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <TextField
                label="Password"
                type="password"
                fullWidth
                margin="normal"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <TextField
                label="Date of Birth"
                type="date"
                fullWidth
                margin="normal"
                InputLabelProps={{ shrink: true }}
                value={dateOfBirth}
                onChange={(e) => setDateOfBirth(e.target.value)}
            />
            <TextField label="City" fullWidth margin="normal" value={city} onChange={(e) => setCity(e.target.value)} />
            <TextField
                label="Country"
                fullWidth
                margin="normal"
                value={country}
                onChange={(e) => setCountry(e.target.value)}
            />
            <TextField
                label="Bio"
                fullWidth
                margin="normal"
                multiline
                rows={3}
                value={bio}
                onChange={(e) => setBio(e.target.value)}
            />

            <label htmlFor="author-photo-file" style={{ marginTop: "1rem" }}>
                Photo
            </label>
            <input
                id="author-photo-file"
                type="file"
                accept="image/*"
                onChange={(e) => {
                    if (e.target.files && e.target.files[0]) {
                        setPhotoFile(e.target.files[0])
                    }
                }}
                style={{ display: "block", marginTop: "0.5rem" }}
            />
            {photoFile && <p>Selected file: {photoFile.name}</p>}
        </>
    )
}

export default AuthorSignupForm
