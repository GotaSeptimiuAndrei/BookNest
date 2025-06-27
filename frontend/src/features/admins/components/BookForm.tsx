import { Button, MenuItem, Stack, TextField } from "@mui/material"
import { Controller, useForm } from "react-hook-form"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import type { BookResponse } from "@/api/generated"

const bookCategories = [
    "Self-Help",
    "Science-Fiction",
    "Psychology",
    "Poetry",
    "Literary Fiction",
    "History",
    "Thriller",
] as const

const base = {
    title: z.string().min(2),
    author: z.string().min(2),
    description: z.string().min(10),
    copies: z.coerce.number().min(0),
    category: z.enum(bookCategories),
}

const createSchema = z.object({ ...base, image: z.instanceof(File) })
const editSchema = z.object({ ...base, image: z.instanceof(File).optional() })

export type BookFormCreate = z.infer<typeof createSchema>
export type BookFormEdit = z.infer<typeof editSchema>
export type BookFormData = BookFormCreate | BookFormEdit

interface Props {
    initial?: BookResponse
    loading: boolean
    onSubmit: (d: BookFormData & { copiesAvailable: number }) => Promise<unknown>
}

export default function BookForm({ initial, loading, onSubmit }: Props) {
    const isEdit = Boolean(initial)
    const schema = isEdit ? editSchema : createSchema

    const {
        control,
        handleSubmit,
        setValue,
        formState: { errors },
    } = useForm<BookFormData>({
        resolver: zodResolver(schema),
        defaultValues: isEdit
            ? {
                  title: initial!.title,
                  author: initial!.author,
                  description: initial!.description,
                  copies: initial!.copies ?? 0,
                  category: initial!.category as any,
              }
            : undefined,
    })

    const submitHandler = (data: BookFormData) => onSubmit({ ...data, copiesAvailable: data.copies })

    return (
        <Stack spacing={3} component="form" onSubmit={handleSubmit(submitHandler)}>
            {(["title", "author", "description"] as const).map((field) => (
                <Controller
                    key={field}
                    name={field}
                    control={control}
                    render={({ field: f }) => (
                        <TextField
                            {...f}
                            label={field.replace(/([A-Z])/g, " $1")}
                            multiline={field === "description"}
                            rows={field === "description" ? 3 : 1}
                            error={!!errors[field]}
                            helperText={(errors as any)[field]?.message}
                        />
                    )}
                />
            ))}

            <Controller
                name="copies"
                control={control}
                render={({ field }) => (
                    <TextField
                        {...field}
                        type="number"
                        label="Copies"
                        InputProps={{ inputProps: { min: 0 } }}
                        error={!!errors.copies}
                        helperText={errors.copies?.message}
                    />
                )}
            />

            <Controller
                name="category"
                control={control}
                render={({ field }) => (
                    <TextField
                        {...field}
                        select
                        label="Category"
                        error={!!errors.category}
                        helperText={errors.category?.message}
                    >
                        {bookCategories.map((c) => (
                            <MenuItem key={c} value={c}>
                                {c}
                            </MenuItem>
                        ))}
                    </TextField>
                )}
            />

            <Controller
                name="image"
                control={control}
                render={({ field }) => (
                    <Button variant="outlined" component="label">
                        {(field.value as File | undefined)?.name ?? "Upload image"}
                        <input
                            type="file"
                            hidden
                            accept="image/*"
                            onChange={(e) => {
                                const f = e.target.files?.[0]
                                if (f) setValue("image", f as any)
                            }}
                        />
                    </Button>
                )}
            />
            {errors.image && <span style={{ color: "#d32f2f", fontSize: 12 }}>{errors.image.message}</span>}

            <Button variant="contained" type="submit" disabled={loading}>
                {isEdit ? "Update book" : "Create book"}
            </Button>
        </Stack>
    )
}
