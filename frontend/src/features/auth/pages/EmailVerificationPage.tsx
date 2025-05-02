import { useState } from "react"
import { Box, Button, Grid, Stack, TextField, Typography } from "@mui/material"
import OtpInput from "../components/OtpInput"
import { useVerifyEmail } from "../hooks/useVerifyEmail"
import { useLocation, useNavigate } from "react-router-dom"

export default function EmailVerificationPage() {
    const { state } = useLocation() as { state: { email: string } }
    const [email, setEmail] = useState(state?.email ?? "")
    const [code, setCode] = useState("")
    const verify = useVerifyEmail()
    const navigate = useNavigate()

    const handleSubmit = async () => {
        await verify.mutateAsync({ email, verificationCode: code })
        navigate("/login")
    }

    return (
        <Grid container sx={{ height: "100vh" }}>
            <Grid item xs={12} md={6} p={4}>
                <Stack spacing={3} maxWidth={420} margin="auto">
                    <Typography variant="h4" fontWeight={700} textAlign="center">
                        Verify your email
                    </Typography>

                    <TextField label="Email" value={email} onChange={(e) => setEmail(e.target.value)} type="email" />

                    <OtpInput value={code} onChange={setCode} disabled={verify.isPending} />

                    <Button variant="contained" disabled={code.length !== 6 || verify.isPending} onClick={handleSubmit}>
                        Verify
                    </Button>
                </Stack>
            </Grid>

            <Grid item xs={0} md={6} sx={{ display: { xs: "none", md: "block" } }}>
                <Box component="img" src="/signup_img.jpg" sx={{ width: "100%", height: "100%", objectFit: "cover" }} />
            </Grid>
        </Grid>
    )
}
