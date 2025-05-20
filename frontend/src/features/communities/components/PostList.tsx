import { Box, CircularProgress, Stack, Typography } from "@mui/material"
import { useInView } from "react-intersection-observer"
import { useCommunityPosts } from "../hooks/useCommunityPosts"
import PostCard from "./PostCard"

interface Props {
    communityId: number
    authorId: number
    sort: "newest" | "oldest" | "likes"
}

export default function PostList({ communityId, authorId, sort }: Props) {
    const { data, isLoading, isFetchingNextPage, fetchNextPage, hasNextPage } = useCommunityPosts(communityId, sort)

    const { ref } = useInView({
        threshold: 0,
        onChange: (inView) => {
            if (inView && hasNextPage && !isFetchingNextPage) fetchNextPage()
        },
    })

    if (isLoading) return <CircularProgress color="secondary" />

    const posts = data?.pages.flatMap((p) => p.content) ?? []

    if (!posts.length) return <Typography>No posts yet.</Typography>

    return (
        <Stack spacing={3}>
            {posts.map((p) => (
                <PostCard
                    key={p.datePosted + p.authorFullName}
                    post={p}
                    communityId={communityId}
                    authorId={authorId}
                />
            ))}

            <Box ref={ref} />

            {isFetchingNextPage && (
                <Box sx={{ textAlign: "center", py: 2 }}>
                    <CircularProgress size={24} />
                </Box>
            )}
        </Stack>
    )
}
