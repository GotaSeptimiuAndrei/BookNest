import { useState } from "react"
import { Box, Checkbox, FormControlLabel, Grid, Typography } from "@mui/material"
import UserRegisterForm, { UserForm } from "../components/UserRegisterForm"
import AuthorRegisterForm, { AuthorForm } from "../components/AuthorRegisterForm"
import { useRegisterUser } from "../hooks/useRegisterUser"
import { useRegisterAuthor } from "../hooks/useRegisterAuthor"
import { useNavigate } from "react-router-dom"

export default function RegisterPage() {
    const [asAuthor, setAsAuthor] = useState(false)
    const navigate = useNavigate()

    const registerUser = useRegisterUser()
    const registerAuthor = useRegisterAuthor()

    const handleUser = async (data: UserForm) => {
        await registerUser.mutateAsync(data)
        navigate("/verify-email", { state: { email: data.email } })
    }

    const handleAuthor = async (data: AuthorForm) => {
        console.log(data)
        await registerAuthor.mutateAsync(data)
        navigate("/verify-email", { state: { email: data.email } })
    }
    return (
        <Grid container sx={{ height: "100vh" }}>
            <Grid item xs={12} md={6} p={4}>
                <Typography variant="h4" fontWeight={700} mb={3} textAlign="center">
                    {asAuthor ? "Author" : "User"} Registration
                </Typography>

                {asAuthor ? (
                    <AuthorRegisterForm onSubmit={handleAuthor} loading={registerAuthor.isPending} />
                ) : (
                    <UserRegisterForm onSubmit={handleUser} loading={registerUser.isPending} />
                )}

                <FormControlLabel
                    sx={{ mt: 2 }}
                    control={<Checkbox checked={asAuthor} onChange={(e) => setAsAuthor(e.target.checked)} />}
                    label="Register as author?"
                />
            </Grid>

            <Grid item xs={0} md={6} sx={{ display: { xs: "none", md: "block" } }}>
                <Box component="img" src="/signup_img.jpg" sx={{ width: "100%", height: "100%", objectFit: "cover" }} />
            </Grid>
        </Grid>
    )
}
