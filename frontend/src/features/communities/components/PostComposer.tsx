// features/communities/components/PostComposer.tsx
import { useForm, Controller } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import ReactQuill from "react-quill"
import { Box, Button, Card, CardContent, Stack, Typography } from "@mui/material"
import { useCreatePost } from "../hooks/posts/useCreatePost"

const schema = z.object({
    text: z.string().min(1, "Write something…"),
    image: z.instanceof(File).optional(),
})

export type PostComposerValues = z.infer<typeof schema>

interface Props {
    communityId: number
    authorId: number
    onSuccess?: () => void // for dialog close
}

export default function PostComposer({ communityId, authorId, onSuccess }: Props) {
    const {
        control,
        handleSubmit,
        setValue,
        reset,
        formState: { errors },
    } = useForm<PostComposerValues>({
        resolver: zodResolver(schema),
        defaultValues: { text: "" },
    })

    const create = useCreatePost(communityId)

    const submit = (vals: PostComposerValues) => {
        create.mutate(
            {
                communityId,
                authorId,
                text: vals.text,
                image: vals.image,
            },
            {
                onSuccess: () => {
                    reset()
                    onSuccess?.()
                },
            }
        )
    }

    return (
        <Card elevation={1}>
            <CardContent>
                <Stack spacing={2} component="form" onSubmit={handleSubmit(submit)}>
                    <Controller
                        name="text"
                        control={control}
                        render={({ field }) => (
                            <>
                                <ReactQuill
                                    theme="snow"
                                    value={field.value}
                                    onChange={field.onChange}
                                    placeholder="Share something with your community…"
                                />
                                {errors.text && (
                                    <Typography color="error" variant="caption">
                                        {errors.text.message}
                                    </Typography>
                                )}
                            </>
                        )}
                    />

                    <Controller
                        name="image"
                        control={control}
                        render={({ field }) => (
                            <Button variant="outlined" component="label">
                                {field.value ? (field.value as File).name : "Attach image"}
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

                    <Box textAlign="right">
                        <Button type="submit" variant="contained" disabled={create.isPending}>
                            Post
                        </Button>
                    </Box>
                </Stack>
            </CardContent>
        </Card>
    )
}
