import { Container } from "@mui/material"
import CommunityHeader from "../components/CommunityHeader"
import CommunityFeed from "../components/CommunityFeed"
import { useCommunityById } from "../hooks/useCommunityById"
import { useParams } from "react-router-dom"
import { useState } from "react"

export default function CommunityPage() {
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
