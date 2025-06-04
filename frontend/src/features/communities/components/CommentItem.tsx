import { Avatar, Box, Collapse, IconButton, Stack, Tooltip, Typography } from "@mui/material"
import ReplyIcon from "@mui/icons-material/Reply"
import DeleteIcon from "@mui/icons-material/Delete"
import { useState } from "react"
import { useAuth } from "@/context/AuthContext"
import { useReplyToComment } from "../hooks/comments/useReplyToComment"
import { useDeleteComment } from "../hooks/comments/useDeleteComment"
import CommentComposer from "./CommentComposer"
import type { PostCommentResponse } from "@/api/generated"

interface Props {
    comment: PostCommentResponse
    postId: number
}

export default function CommentItem({ comment, postId }: Props) {
    const { user } = useAuth()
    const [showReply, setShowReply] = useState(false)

    const replyMut = useReplyToComment(postId)
    const delMut = useDeleteComment(postId)

    const canDelete =
        comment.commenterId === user?.id || user?.roles.includes("ADMIN") || user?.roles.includes("AUTHOR")

    const handleReply = (vals: { text: string }) => {
        replyMut.mutate({ parentId: comment.commentId!, text: vals.text })
        setShowReply(false)
    }

    return (
        <Stack direction="row" spacing={1} alignItems="flex-start">
            <Avatar sx={{ width: 32, height: 32 }}>{comment.commenterName?.[0].toUpperCase()}</Avatar>

            <Box flexGrow={1}>
                <Stack direction="row" alignItems="center" spacing={1}>
                    <Typography fontWeight={600}>{comment.commenterName}</Typography>
                    <Typography variant="caption" color="text.secondary">
                        {new Date(comment.datePosted ?? "").toLocaleString()}
                    </Typography>
                    {comment.commenterType === "AUTHOR" && (
                        <Typography
                            variant="caption"
                            sx={{ bgcolor: "secondary.main", color: "common.white", px: 0.5, borderRadius: 0.5 }}
                        >
                            Author
                        </Typography>
                    )}
                </Stack>

                <Typography sx={{ whiteSpace: "pre-wrap", mt: 0.5 }}>{comment.text}</Typography>

                <Stack direction="row" spacing={1} mt={0.5}>
                    <IconButton size="small" onClick={() => setShowReply((s) => !s)} aria-label="reply">
                        <ReplyIcon fontSize="inherit" />
                    </IconButton>

                    {canDelete && (
                        <Tooltip title="Delete">
                            <IconButton
                                size="small"
                                onClick={() => delMut.mutate(comment.commentId!)}
                                sx={{
                                    color: "text.secondary",
                                    "&:hover": { color: "error.main" },
                                }}
                            >
                                <DeleteIcon fontSize="inherit" />
                            </IconButton>
                        </Tooltip>
                    )}
                </Stack>

                <Collapse in={showReply} unmountOnExit>
                    <Box mt={1}>
                        <CommentComposer onSubmit={handleReply} busy={replyMut.isPending} autoFocus />
                    </Box>
                </Collapse>

                {comment.replies?.length && (
                    <Stack mt={1} pl={3} spacing={1}>
                        {comment.replies.map((c) => (
                            <CommentItem key={c.commentId} comment={c} postId={postId} />
                        ))}
                    </Stack>
                )}
            </Box>
        </Stack>
    )
}
