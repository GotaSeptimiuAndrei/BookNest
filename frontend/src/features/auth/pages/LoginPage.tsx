import { Box, Grid, Typography } from "@mui/material"
import { useNavigate } from "react-router-dom"
import LoginForm, { LoginFormData } from "../components/LoginForm"
import { useLogin } from "../hooks/useLogin"
import { useEffect } from "react"
import { useAuth } from "@/context/AuthContext"

export default function LoginPage() {
    const login = useLogin()
    const { user } = useAuth()
    const navigate = useNavigate()

    /* redirect if already logged in */
    useEffect(() => {
        if (user) navigate("/")
    }, [user, navigate])

    const handleSubmit = async (data: LoginFormData) => {
        const token = await login.mutateAsync(data)
        if (token) navigate("/") // go to homepage
    }

    return (
        <Grid container sx={{ height: "100vh" }}>
            <Grid item xs={12} md={6} p={4}>
                <Typography variant="h4" fontWeight={700} textAlign="center" mb={3}>
                    Log in
                </Typography>

                <LoginForm onSubmit={handleSubmit} loading={login.isPending} />
            </Grid>

            <Grid item xs={0} md={6} sx={{ display: { xs: "none", md: "block" } }}>
                <Box component="img" src="/signup_img.jpg" sx={{ width: "100%", height: "100%", objectFit: "cover" }} />
            </Grid>
        </Grid>
    )
}
