import { Container } from "@mui/material"
import CommunityHeader from "../components/CommunityHeader"
import CommunityFeed from "../components/CommunityFeed"
import { useCommunityById } from "../hooks/useCommunityById"
import { useParams } from "react-router-dom"

export default function CommunityPage() {
    const { id } = useParams<{ id: string }>()
    const communityId = Number(id)
    const { data: community } = useCommunityById(communityId)

    return (
        <Container maxWidth="md" sx={{ py: 4 }}>
            <CommunityHeader />
            {community && <CommunityFeed authorId={community.author!.authorId!} />}
        </Container>
    )
}
