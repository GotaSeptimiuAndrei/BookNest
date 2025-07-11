import { Container } from "@mui/material"
import CommunityHeader from "../components/community/CommunityHeader"
import { useCommunityById } from "../hooks/community/useCommunityById"
import { useParams } from "react-router-dom"
import { useState } from "react"
import CommunityFeed from "../components/community/CommunityFeed"

export default function AuthorCommunityPage() {
    const { id } = useParams<{ id: string }>()
    const communityId = Number(id)
    const { data: community } = useCommunityById(communityId)
    const [sort, setSort] = useState<"newest" | "oldest" | "likes">("newest")

    return (
        <Container maxWidth="md" sx={{ py: 4 }}>
            <CommunityHeader sort={sort} onSortChange={setSort} />
            {community && <CommunityFeed authorId={community.author!.authorId!} sort={sort} />}
        </Container>
    )
}
