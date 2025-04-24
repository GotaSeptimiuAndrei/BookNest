// src/pages/LoginPage.tsx
import React, { useState, useContext } from "react"
import { Grid, Box, Typography, TextField, Button } from "@mui/material"
import { useNavigate, Link } from "react-router-dom" // If using react-router
import { LoginRequest } from "../../models/LoginRequest"
import { loginUser } from "@/services/authService"
import { AuthContext } from "@/context/AuthContext"

const LoginPage: React.FC = () => {
    const { login } = useContext(AuthContext)
    const navigate = useNavigate()

    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")

    const handleLogin = async () => {
        try {
            const payload: LoginRequest = { email, password }
            const response = await loginUser(payload)
            login(response.token)

            alert("Login successful!")
            navigate("/")
        } catch (err: any) {
            alert("Login failed. Check your credentials.")
            console.error(err)
        }
    }

    return (
        <Grid container sx={{ height: "100vh" }}>
            <Grid item xs={12} md={6} display="flex" alignItems="center" justifyContent="center">
                <Box sx={{ width: "80%", maxWidth: 400, p: 4 }}>
                    <Typography variant="h4" mb={2}>
                        Login
                    </Typography>

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

                    <Button variant="contained" sx={{ mt: 2 }} fullWidth onClick={handleLogin}>
                        Login
                    </Button>

                    <Box sx={{ mt: 2 }}>
                        <Typography variant="body2">
                            Don&apos;t have an account? <Link to="/register">Click Sign Up</Link>
                        </Typography>
                    </Box>
                </Box>
            </Grid>

            {/* Right side: image */}
            <Grid
                item
                xs={12}
                md={6}
                sx={{
                    backgroundImage: "url(/signup_img.jpg)",
                    backgroundRepeat: "no-repeat",
                    backgroundSize: "cover",
                    backgroundPosition: "center",
                }}
            />
        </Grid>
    )
}

export default LoginPage
