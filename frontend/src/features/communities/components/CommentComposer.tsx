import { useForm } from "react-hook-form"
import { z } from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import { Box, Button, Stack, TextField } from "@mui/material"

const schema = z.object({ text: z.string().min(1, "Cannot be empty") })
export type CommentVals = z.infer<typeof schema>

interface Props {
    onSubmit: (vals: CommentVals) => void
    busy?: boolean
    autoFocus?: boolean
}

export default function CommentComposer({ onSubmit, busy, autoFocus }: Props) {
    const {
        register,
        handleSubmit,
        reset,
        formState: { errors },
    } = useForm<CommentVals>({ resolver: zodResolver(schema) })

    return (
        <Box
            component="form"
            onSubmit={handleSubmit((d) => {
                onSubmit(d)
                reset()
            })}
        >
            <Stack spacing={1}>
                <TextField
                    {...register("text")}
                    placeholder="Write a comment…"
                    size="small"
                    multiline
                    rows={2}
                    error={!!errors.text}
                    helperText={errors.text?.message}
                    autoFocus={autoFocus}
                />
                <Box textAlign="right">
                    <Button type="submit" variant="contained" size="small" disabled={busy}>
                        Send
                    </Button>
                </Box>
            </Stack>
        </Box>
    )
}
