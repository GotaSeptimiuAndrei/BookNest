import { Button, Stack, TextField } from "@mui/material"
import { z } from "zod"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

const schema = z.object({
    email: z.string().email(),
    password: z.string().min(5),
})
export type LoginFormData = z.infer<typeof schema>

interface Props {
    onSubmit: (d: LoginFormData) => Promise<unknown>
    loading: boolean
}

export default function LoginForm({ onSubmit, loading }: Props) {
    const {
        control,
        handleSubmit,
        formState: { errors },
    } = useForm<LoginFormData>({ resolver: zodResolver(schema) })

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
                Log in
            </Button>
        </Stack>
    )
}
