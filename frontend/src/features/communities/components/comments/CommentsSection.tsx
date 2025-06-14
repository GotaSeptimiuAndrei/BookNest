import { Box, Button, Collapse, Divider, Stack, Typography } from "@mui/material"
import { useState } from "react"
import { useAuth } from "@/context/AuthContext"
import { usePostComments } from "../../hooks/comments/usePostComments"
import { useCreateComment } from "../../hooks/comments/useCreateComment"
import CommentComposer from "./CommentComposer"
import CommentItem from "./CommentItem"

interface Props {
    postId: number
    initialOpen?: boolean
}

export default function CommentsSection({ postId, initialOpen }: Props) {
    const [open, setOpen] = useState(Boolean(initialOpen))
    const { user } = useAuth()

    const { data: comments = [], isLoading } = usePostComments(postId)
    const createMut = useCreateComment(postId)

    const toggle = () => setOpen((o) => !o)

    return (
        <Box mt={1}>
            <Divider />
            <Button size="small" onClick={toggle} sx={{ mt: 1 }}>
                {open ? "Hide" : "Show"} {comments.length} comment
                {comments.length !== 1 && "s"}
            </Button>

            <Collapse in={open} unmountOnExit>
                {user && (
                    <Box mt={1}>
                        <CommentComposer onSubmit={(d) => createMut.mutate(d.text)} busy={createMut.isPending} />
                    </Box>
                )}

                <Stack spacing={1} mt={2}>
                    {isLoading ? (
                        <Typography>Loading…</Typography>
                    ) : comments.length ? (
                        comments.map((c) => <CommentItem key={c.commentId} comment={c} postId={postId} />)
                    ) : (
                        <Typography variant="body2" color="text.secondary">
                            No comments yet.
                        </Typography>
                    )}
                </Stack>
            </Collapse>
        </Box>
    )
}
