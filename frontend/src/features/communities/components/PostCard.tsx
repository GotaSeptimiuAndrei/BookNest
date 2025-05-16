import { Card, CardHeader, CardContent, CardActions, IconButton, Typography, Box, Tooltip } from "@mui/material"
import FavoriteIcon from "@mui/icons-material/Favorite"
import DeleteIcon from "@mui/icons-material/Delete"
import { useAuth } from "@/context/AuthContext"
import { useToggleLikePost } from "../hooks/useToggleLikePost"
import { useDeletePost } from "../hooks/useDeletePost"
import type { PostResponse } from "@/api/generated"
import CommentsSection from "./CommentsSection"

interface Props {
    post: PostResponse
    communityId: number
    authorId: number //community author id, used for delete rights
}

export default function PostCard({ post, communityId, authorId }: Props) {
    const { user } = useAuth()
    const toggleLike = useToggleLikePost()
    const deletePost = useDeletePost(communityId)

    const canDelete = user?.id === authorId

    const likedByMe = false // TODO: backend flag later

    return (
        <Card elevation={2}>
            <CardHeader title={post.authorFullName} subheader={new Date(post.datePosted ?? "").toLocaleString()} />
            <CardContent>
                {post.text && (
                    <Typography
                        variant="body1"
                        dangerouslySetInnerHTML={{ __html: post.text }}
                        sx={{ whiteSpace: "pre-wrap" }}
                    />
                )}
                {post.imageUrl && (
                    <Box
                        component="img"
                        src={post.imageUrl}
                        alt="post attachment"
                        sx={{
                            display: "block",
                            maxWidth: "100%",
                            width: "100%",
                            height: "auto",
                            maxHeight: 512,
                            borderRadius: 2,
                            mt: 2,
                            objectFit: "contain",
                        }}
                    />
                )}
            </CardContent>
            <CardActions>
                <IconButton
                    onClick={() =>
                        toggleLike.mutate({
                            postId: post.postId!,
                            userId: user!.id,
                            liked: likedByMe,
                            communityId,
                        })
                    }
                >
                    <FavoriteIcon color={likedByMe ? "error" : "inherit"} sx={{ mr: 0.5 }} />
                    <Typography variant="body2">{post.likeCount}</Typography>
                </IconButton>

                {canDelete && (
                    <Tooltip title="Delete post">
                        <IconButton
                            onClick={() => deletePost.mutate(post.postId!)}
                            sx={{
                                ml: "auto",
                                color: "text.secondary",
                                "&:hover": { color: "error.main" },
                            }}
                        >
                            <DeleteIcon />
                        </IconButton>
                    </Tooltip>
                )}
            </CardActions>
            <CommentsSection postId={post.postId!} />
        </Card>
    )
}
