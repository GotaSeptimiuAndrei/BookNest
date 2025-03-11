// src/components/UserFields.tsx
import React from "react"
import { TextField } from "@mui/material"

interface UserFieldsProps {
    email: string
    setEmail: (value: string) => void
    password: string
    setPassword: (value: string) => void
    username: string
    setUsername: (value: string) => void
}

const UserSignupForm: React.FC<UserFieldsProps> = ({
    email,
    setEmail,
    password,
    setPassword,
    username,
    setUsername,
}) => {
    return (
        <>
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
                label="Username"
                fullWidth
                margin="normal"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />
        </>
    )
}

export default UserSignupForm
