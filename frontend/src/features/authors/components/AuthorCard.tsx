import { Avatar, Button, Card, CardContent, Stack, Typography } from "@mui/material"
import { Link as RouterLink } from "react-router-dom"
import type { AuthorResponse } from "@/api/generated"

export default function AuthorCard({ fullName, bio, photo }: AuthorResponse) {
    return (
        <Card sx={{ display: "flex", mb: 2 }}>
            <Stack alignItems="center" justifyContent="center" sx={{ p: 2 }}>
                <Avatar src={photo} alt={fullName} sx={{ width: 72, height: 72 }} />
            </Stack>

            <CardContent sx={{ flex: 1 }}>
                <Typography variant="h6">{fullName}</Typography>

                <Typography variant="body2" sx={{ mt: 1 }}>
                    {bio}
                </Typography>

                <Button
                    size="small"
                    variant="outlined"
                    component={RouterLink}
                    to={`/authors/${fullName}`}
                    sx={{ mt: 1 }}
                >
                    View Profile
                </Button>
            </CardContent>
        </Card>
    )
}
