// features/communities/components/CommunityFeed.tsx
import { Stack } from "@mui/material"
import { useAuth } from "@/context/AuthContext"
import { useParams } from "react-router-dom"
import PostComposer from "./PostComposer"
import PostComposerFAB from "./PostComposerFAB"
import PostList from "./PostList"

interface Props {
    authorId: number
    sort: "newest" | "oldest" | "likes"
}

export default function CommunityFeed({ authorId, sort }: Props) {
    const { id } = useParams<{ id: string }>()
    const communityId = Number(id)
    const { user } = useAuth()

    const isAuthor = user?.roles.includes("AUTHOR") && user.id === authorId

    return (
        <Stack spacing={3} sx={{ mt: 4 }}>
            {isAuthor && (
                <>
                    <PostComposer communityId={communityId} authorId={authorId} />
                    <PostComposerFAB communityId={communityId} authorId={authorId} />
                </>
            )}

            <PostList communityId={communityId} authorId={authorId} sort={sort} />
        </Stack>
    )
}
