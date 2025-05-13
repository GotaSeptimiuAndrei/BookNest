import { Button, Stack, TextField, Typography } from "@mui/material"
import { z } from "zod"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

const schema = z.object({
    authorId: z.number(),
    name: z.string().min(3),
    description: z.string().min(10),
    photo: z.instanceof(File),
})

export type CommunityFormData = z.infer<typeof schema>

interface Props {
    authorId: number
    onSubmit: (d: CommunityFormData) => Promise<unknown>
    loading: boolean
}

export default function CommunityForm({ authorId, onSubmit, loading }: Props) {
    const {
        control,
        handleSubmit,
        setValue,
        formState: { errors },
    } = useForm<CommunityFormData>({
        resolver: zodResolver(schema),
        defaultValues: { authorId } as any,
    })

    return (
        <Stack spacing={3} component="form" onSubmit={handleSubmit(onSubmit)}>
            <Typography variant="h5">Create Community</Typography>

            <Controller
                name="name"
                control={control}
                render={({ field }) => (
                    <TextField {...field} label="Name" error={!!errors.name} helperText={errors.name?.message} />
                )}
            />

            <Controller
                name="description"
                control={control}
                render={({ field }) => (
                    <TextField
                        {...field}
                        multiline
                        minRows={3}
                        label="Description"
                        error={!!errors.description}
                        helperText={errors.description?.message}
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
                            onChange={(e) => {
                                const file = e.target.files?.[0]
                                if (file) setValue("photo", file as any)
                            }}
                        />
                    </Button>
                )}
            />

            {errors.photo && (
                <Typography variant="caption" color="error">
                    {errors.photo.message}
                </Typography>
            )}

            <Button variant="contained" type="submit" disabled={loading}>
                Create community
            </Button>
        </Stack>
    )
}
