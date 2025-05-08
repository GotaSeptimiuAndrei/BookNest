import { Button, Rating, Stack, TextField } from "@mui/material"
import { Controller, useForm } from "react-hook-form"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { useCreateReview } from "../hooks/useCreateReview"

const schema = z.object({
    bookId: z.number(),
    rating: z.number().min(0.5),
    reviewDescription: z.string().optional(),
})

type Form = z.infer<typeof schema>

interface Props {
    bookId: number
    onSuccess?: () => void
}

export default function ReviewForm({ bookId }: Props) {
    const createReview = useCreateReview()

    const { control, handleSubmit, watch, reset } = useForm<Form>({
        resolver: zodResolver(schema),
        defaultValues: { bookId, rating: 0 },
    })

    const rating = watch("rating")

    const onSubmit = async (data: Form) => {
        await createReview.mutateAsync(data)
        reset({ bookId, rating: 0, reviewDescription: "" })
    }

    return (
        <Stack spacing={2} component="form" onSubmit={handleSubmit(onSubmit)}>
            <Controller
                name="rating"
                control={control}
                render={({ field }) => (
                    <Rating {...field} precision={0.5} onChange={(_, v) => field.onChange(v ?? 0)} />
                )}
            />
            {rating > 0 && (
                <Controller
                    name="reviewDescription"
                    control={control}
                    render={({ field }) => <TextField {...field} label="Description" multiline minRows={3} />}
                />
            )}
            <Button variant="contained" type="submit" disabled={createReview.isPending || rating === 0}>
                Submit review
            </Button>
        </Stack>
    )
}
