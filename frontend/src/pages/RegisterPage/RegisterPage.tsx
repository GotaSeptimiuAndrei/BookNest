// src/pages/RegisterPage.tsx
import React, { useState } from "react"
import { Grid, Box, Typography, Button, FormControlLabel, Checkbox } from "@mui/material"

import UserSignupForm from "../../components/SignupForms/UserSignupForm"
import AuthorSignupForm from "../../components/SignupForms/AuthorSignupForm"

import { UserSignupRequest } from "../../models/UserSignupRequest"
import { AuthorSignupRequest } from "../../models/AuthorSignupRequest"
import { signUpUser, signUpAuthor } from "../../services/authService"
import { useNavigate } from "react-router-dom"

const RegisterPage: React.FC = () => {
    const navigate = useNavigate()

    // Toggle for author vs. user
    const [isAuthor, setIsAuthor] = useState(false)

    // Basic User state
    const [userEmail, setUserEmail] = useState("")
    const [userPassword, setUserPassword] = useState("")
    const [username, setUsername] = useState("")

    // Author state
    const [fullName, setFullName] = useState("")
    const [authorEmail, setAuthorEmail] = useState("")
    const [authorPassword, setAuthorPassword] = useState("")
    const [dateOfBirth, setDateOfBirth] = useState("")
    const [city, setCity] = useState("")
    const [country, setCountry] = useState("")
    const [bio, setBio] = useState("")
    const [photoFile, setPhotoFile] = useState<File | null>(null)

    // SUBMIT
    const handleRegister = async () => {
        try {
            if (isAuthor) {
                // Build an AuthorSignupRequest
                const authorData: AuthorSignupRequest = {
                    fullName,
                    email: authorEmail,
                    password: authorPassword,
                    dateOfBirth,
                    city,
                    country,
                    bio,
                    photoFile,
                }

                await signUpAuthor(authorData)
                alert("Author registered successfully!")
                navigate("/verify-email", { state: { email: authorData.email } })
            } else {
                // Build a UserSignupRequest
                const userData: UserSignupRequest = {
                    email: userEmail,
                    password: userPassword,
                    username,
                }

                await signUpUser(userData)
                alert("User registered successfully!")
                navigate("/verify-email", { state: { email: userData.email } })
            }
        } catch (error: any) {
            console.error(error)
            alert(error?.response?.data || "Something went wrong during signup.")
        }
    }

    return (
        <Grid container sx={{ height: "100vh" }}>
            {/* LEFT SIDE: form */}
            <Grid item xs={12} md={6} display="flex" alignItems="center" justifyContent="center">
                <Box sx={{ width: "80%", maxWidth: 400, p: 4 }}>
                    <Typography variant="h4" mb={2}>
                        Register
                    </Typography>
                    <FormControlLabel
                        control={<Checkbox checked={isAuthor} onChange={(e) => setIsAuthor(e.target.checked)} />}
                        label="Register as author?"
                    />

                    {!isAuthor && (
                        <UserSignupForm
                            email={userEmail}
                            setEmail={setUserEmail}
                            password={userPassword}
                            setPassword={setUserPassword}
                            username={username}
                            setUsername={setUsername}
                        />
                    )}

                    {isAuthor && (
                        <AuthorSignupForm
                            fullName={fullName}
                            setFullName={setFullName}
                            email={authorEmail}
                            setEmail={setAuthorEmail}
                            password={authorPassword}
                            setPassword={setAuthorPassword}
                            dateOfBirth={dateOfBirth}
                            setDateOfBirth={setDateOfBirth}
                            city={city}
                            setCity={setCity}
                            country={country}
                            setCountry={setCountry}
                            bio={bio}
                            setBio={setBio}
                            photoFile={photoFile}
                            setPhotoFile={setPhotoFile}
                        />
                    )}

                    <Button variant="contained" sx={{ mt: 2 }} fullWidth onClick={handleRegister}>
                        Register
                    </Button>
                </Box>
            </Grid>

            <Grid
                item
                xs={12}
                md={6}
                sx={{
                    backgroundImage: "url('/signup_img.jpg')",
                    backgroundRepeat: "no-repeat",
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                }}
            />
        </Grid>
    )
}

export default RegisterPage
