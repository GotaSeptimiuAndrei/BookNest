import { Button, Stack, TextField, MenuItem } from "@mui/material"
import { z } from "zod"
import { Controller, useForm } from "react-hook-form"
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
]

const schema = z.object({
    title: z.string().min(2),
    author: z.string().min(2),
    description: z.string().min(10),
    copies: z.coerce.number().min(0),
    copiesAvailable: z.coerce.number().min(0),
    category: z.string().min(2),
    image: z.instanceof(File),
})

export type BookFormData = z.infer<typeof schema>

interface Props {
    initial?: BookResponse
    onSubmit: (d: BookFormData) => Promise<unknown>
    loading: boolean
}

export default function BookForm({ initial, onSubmit, loading }: Props) {
    const {
        control,
        handleSubmit,
        setValue,
        formState: { errors },
    } = useForm<BookFormData>({
        resolver: zodResolver(schema),
        defaultValues: initial
            ? {
                  title: initial.title,
                  author: initial.author,
                  description: initial.description,
                  copies: initial.copies ?? 0,
                  copiesAvailable: initial.copiesAvailable ?? 0,
                  category: initial.category,
                  // image field left empty
              }
            : undefined,
    })

    return (
        <Stack spacing={3} component="form" onSubmit={handleSubmit(onSubmit)}>
            {(["title", "author", "description"] as const).map((f) => (
                <Controller
                    key={f}
                    name={f}
                    control={control}
                    render={({ field }) => (
                        <TextField
                            {...field}
                            multiline={f === "description"}
                            rows={f === "description" ? 3 : 1}
                            label={f.replace(/([A-Z])/g, " $1")}
                            error={!!errors[f]}
                            helperText={(errors[f] as any)?.message}
                        />
                    )}
                />
            ))}

            {(["copies", "copiesAvailable"] as const).map((f) => (
                <Controller
                    key={f}
                    name={f}
                    control={control}
                    render={({ field }) => (
                        <TextField
                            {...field}
                            type="number"
                            label={f.replace(/([A-Z])/g, " $1")}
                            error={!!errors[f]}
                            helperText={(errors[f] as any)?.message}
                            InputProps={{ inputProps: { min: 0 } }}
                        />
                    )}
                />
            ))}

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
                        {field.value ? (field.value as File).name : "Upload image"}
                        <input
                            type="file"
                            hidden
                            accept="image/*"
                            onChange={(e) => {
                                const file = e.target.files?.[0]
                                if (file) setValue("image", file as any)
                            }}
                        />
                    </Button>
                )}
            />

            <Button variant="contained" type="submit" disabled={loading}>
                {initial ? "Update book" : "Create book"}
            </Button>
        </Stack>
    )
}
