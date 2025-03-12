// src/pages/VerifyEmailPage.tsx
import React, { useState } from "react"
import { Grid, Box, Typography, TextField, Button } from "@mui/material"
import { useLocation, useNavigate } from "react-router-dom"
import { verifyEmail } from "../../services/authService"
import { EmailVerificationRequest } from "../../models/EmailVerificationRequest"

const VerifyEmailPage: React.FC = () => {
    const navigate = useNavigate()
    const location = useLocation() as { state: { email?: string } }

    const [email] = useState<string>(location.state?.email || "")
    const [verificationCode, setVerificationCode] = useState("")

    const handleVerify = async () => {
        try {
            const payload: EmailVerificationRequest = {
                email,
                verificationCode,
            }

            await verifyEmail(payload)
            alert("Email verified successfully!")
            navigate("/login")
        } catch (error) {
            console.error(error)
            alert("Verification failed. Please check your code or try again.")
        }
    }

    return (
        <Grid container sx={{ height: "100vh" }}>
            {/* LEFT side: instruction + input */}
            <Grid item xs={12} md={6} display="flex" alignItems="center" justifyContent="center">
                <Box sx={{ width: "80%", maxWidth: 400, p: 4 }}>
                    <Typography variant="h4" mb={2} fontWeight="bold">
                        Check your mail
                    </Typography>
                    <Typography variant="subtitle1" mb={2}>
                        Please enter your verification code
                    </Typography>

                    <TextField
                        label="Verification Code"
                        fullWidth
                        margin="normal"
                        value={verificationCode}
                        onChange={(e) => setVerificationCode(e.target.value)}
                    />

                    <Button
                        variant="contained"
                        fullWidth
                        sx={{ mt: 2 }}
                        onClick={handleVerify}
                        disabled={!verificationCode}
                    >
                        Verify
                    </Button>
                </Box>
            </Grid>

            {/* RIGHT side: image */}
            <Grid
                item
                xs={12}
                md={6}
                sx={{
                    backgroundImage: "url(/assets/signup_img.jpg)",
                    backgroundRepeat: "no-repeat",
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                }}
            />
        </Grid>
    )
}

export default VerifyEmailPage
