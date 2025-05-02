import { Button, Stack, TextField } from "@mui/material"
import { Controller, useForm } from "react-hook-form"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"

const schema = z.object({
    email: z.string().email(),
    password: z.string().min(5),
    fullName: z.string().min(3),
    dateOfBirth: z.string(),
    city: z.string().min(2),
    country: z.string().min(2),
    bio: z.string().min(10),
    photo: z.instanceof(File),
})
export type AuthorForm = z.infer<typeof schema>

interface Props {
    onSubmit: (data: AuthorForm) => Promise<unknown>
    loading: boolean
}

export default function AuthorRegisterForm({ onSubmit, loading }: Props) {
    const {
        control,
        handleSubmit,
        formState: { errors },
    } = useForm<AuthorForm>({ resolver: zodResolver(schema) })

    return (
        <Stack spacing={3} component="form" onSubmit={handleSubmit(onSubmit)}>
            {["email", "fullName", "dateOfBirth", "city", "country", "bio"].map((field) => (
                <Controller
                    key={field}
                    name={field as keyof AuthorForm}
                    control={control}
                    render={({ field: f }) => (
                        <TextField
                            {...f}
                            type={field === "dateOfBirth" ? "date" : "text"}
                            label={field.replace(/([A-Z])/g, " $1")}
                            InputLabelProps={field === "dateOfBirth" ? { shrink: true } : undefined}
                            error={!!errors[field as keyof AuthorForm]}
                            helperText={(errors[field as keyof AuthorForm] as any)?.message}
                        />
                    )}
                />
            ))}
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
            <Controller
                name="photo"
                control={control}
                render={({ field }) => (
                    <Button variant="outlined" component="label">
                        {field.value ? (field.value as File).name : "Upload photo"}
                        <input
                            type="file"
                            hidden
                            accept="image/*"
                            onChange={(e) => field.onChange(e.target.files?.[0])}
                        />
                    </Button>
                )}
            />
            <Button variant="contained" type="submit" disabled={loading}>
                Register
            </Button>
        </Stack>
    )
}
