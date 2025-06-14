import { Box, Card, CardActions, CardContent, CardHeader, IconButton, Tooltip, Typography } from "@mui/material"
import FavoriteIcon from "@mui/icons-material/Favorite"
import DeleteIcon from "@mui/icons-material/Delete"
import { useState } from "react"
import { useAuth } from "@/context/AuthContext"
import { useToggleLikePost } from "../../hooks/posts/useToggleLikePost"
import { useDeletePost } from "../../hooks/posts/useDeletePost"
import CommentsSection from "../comments/CommentsSection"
import type { PostResponse } from "@/api/generated"

interface Props {
    post: PostResponse
    communityId: number
    authorId: number
}

export default function PostCard({ post, communityId, authorId }: Props) {
    const { user } = useAuth()
    const toggleLike = useToggleLikePost()
    const deletePost = useDeletePost(communityId)

    const isBasicUser = user?.roles.includes("USER")
    const canDelete = user?.id === authorId || user?.roles.includes("ADMIN")

    const [liked, setLiked] = useState(Boolean(post.likedByMe))
    const [likes, setLikes] = useState(post.likeCount ?? 0)

    const handleLike = () => {
        if (!user || !isBasicUser) return
        setLiked((prev) => !prev)
        setLikes((c) => c + (liked ? -1 : 1))
        toggleLike.mutate({
            postId: post.postId!,
            userId: user.id,
            liked,
            communityId,
        })
    }

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
                <IconButton onClick={handleLike} disabled={!isBasicUser}>
                    <FavoriteIcon color={liked ? "error" : "inherit"} sx={{ mr: 0.5 }} />
                    <Typography variant="body2">{likes}</Typography>
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
