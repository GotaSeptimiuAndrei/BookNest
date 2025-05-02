import { Button, Stack, TextField } from "@mui/material"
import { Controller, useForm } from "react-hook-form"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"

const schema = z.object({
    email: z.string().email(),
    username: z.string().min(3),
    password: z.string().min(6),
})
export type UserForm = z.infer<typeof schema>

interface Props {
    onSubmit: (data: UserForm) => Promise<unknown>
    loading: boolean
}

export default function UserRegisterForm({ onSubmit, loading }: Props) {
    const {
        control,
        handleSubmit,
        formState: { errors },
    } = useForm<UserForm>({ resolver: zodResolver(schema) })

    return (
        <Stack spacing={3} component="form" onSubmit={handleSubmit(onSubmit)}>
            <Controller
                name="email"
                control={control}
                render={({ field }) => (
                    <TextField {...field} label="Email" error={!!errors.email} helperText={errors.email?.message} />
                )}
            />
            <Controller
                name="username"
                control={control}
                render={({ field }) => (
                    <TextField
                        {...field}
                        label="Username"
                        error={!!errors.username}
                        helperText={errors.username?.message}
                    />
                )}
            />
            <Controller
                name="password"
                control={control}
                render={({ field }) => (
                    <TextField
                        {...field}
                        type="password"
                        label="Password"
                        error={!!errors.password}
                        helperText={errors.password?.message}
                    />
                )}
            />
            <Button variant="contained" type="submit" disabled={loading}>
                Register
            </Button>
        </Stack>
    )
}
