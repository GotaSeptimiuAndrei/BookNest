import { Card, CardActionArea, CardContent, Typography } from "@mui/material"
import { Link as RouterLink } from "react-router-dom"
import type { Community } from "@/api/generated"

interface Props {
    community: Community
}

export default function CommunityCard({ community }: Props) {
    const { communityId, name, author } = community

    return (
        <Card sx={{ minWidth: 220 }}>
            <CardActionArea component={RouterLink} to={`/communities/${communityId}`}>
                <CardContent>
                    <Typography fontWeight={600}>{name}</Typography>
                    <Typography variant="body2" color="text.secondary">
                        {author?.fullName}
                    </Typography>
                </CardContent>
            </CardActionArea>
        </Card>
    )
}
