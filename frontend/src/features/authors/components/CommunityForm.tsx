import { z } from "zod"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Button, Stack, TextField, Typography } from "@mui/material"
import { useEffect } from "react"
import type { Community } from "@/api/generated"

const base = {
    authorId: z.number(),
    name: z.string().min(3),
    description: z.string().min(10),
}

const createSchema = z.object({
    ...base,
    photo: z.instanceof(File),
})

const editSchema = z.object({
    ...base,
    photo: z.instanceof(File).optional(),
})

export type CommunityFormCreate = z.infer<typeof createSchema>
export type CommunityFormEdit = z.infer<typeof editSchema>
export type CommunityFormData = CommunityFormCreate | CommunityFormEdit

interface Props {
    authorId: number
    loading: boolean
    onSubmit: (d: CommunityFormData) => Promise<unknown>
    initial?: Community
}

export default function CommunityForm({ authorId, loading, onSubmit, initial }: Props) {
    const isEdit = Boolean(initial)
    const schema = isEdit ? editSchema : createSchema

    const {
        control,
        handleSubmit,
        setValue,
        reset,
        formState: { errors },
    } = useForm<CommunityFormData>({
        resolver: zodResolver(schema),
        defaultValues: isEdit
            ? {
                  authorId,
                  name: initial!.name ?? "",
                  description: initial!.description ?? "",
              }
            : ({ authorId } as any),
    })

    useEffect(() => {
        if (initial) {
            reset({
                authorId,
                name: initial.name ?? "",
                description: initial.description ?? "",
            } as any)
        }
    }, [initial, authorId, reset])

    return (
        <Stack spacing={3} component="form" onSubmit={handleSubmit(onSubmit)}>
            <Typography variant="h5">{isEdit ? "Update Community" : "Create Community"}</Typography>

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
                        {(field.value as File | undefined)?.name ?? "Upload photo"}
                        <input
                            type="file"
                            hidden
                            accept="image/*"
                            onChange={(e) => {
                                const f = e.target.files?.[0]
                                if (f) setValue("photo", f as any)
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
                {isEdit ? "Save changes" : "Create community"}
            </Button>
        </Stack>
    )
}
