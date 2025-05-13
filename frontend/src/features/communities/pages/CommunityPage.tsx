import { Container, Typography } from "@mui/material"
import { useParams } from "react-router-dom"
import { useQuery } from "@tanstack/react-query"
import { CommunityControllerService } from "@/api/generated"
import type { Community } from "@/api/generated"

export default function CommunityPage() {
    const { id } = useParams()

    const {
        data: community,
        isLoading,
        error,
    } = useQuery<Community>({
        queryKey: ["community", id],
        enabled: !!id,
        queryFn: () =>
            CommunityControllerService.getCommunityById({
                id: Number(id),
            }).then((res) => res.results!),
    })

    if (isLoading) return <Typography sx={{ p: 4 }}>Loading…</Typography>
    if (error) return <Typography sx={{ p: 4 }}>Failed to load community.</Typography>
    if (!community) return <Typography sx={{ p: 4 }}>Community not found.</Typography>

    return (
        <Container sx={{ py: 4 }}>
            <Typography variant="h4" gutterBottom>
                {community.name}
            </Typography>
            <Typography variant="body1" sx={{ mb: 3 }}>
                {community.description}
            </Typography>

            {/* TODO: posts, members list, join/leave button, etc. */}
        </Container>
    )
}
