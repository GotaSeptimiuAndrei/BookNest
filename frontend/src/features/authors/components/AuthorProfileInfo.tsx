import { Avatar, Stack, Typography } from "@mui/material"
import type { AuthorResponse } from "@/api/generated"

export default function AuthorProfileInfo({ author }: { author: AuthorResponse }) {
    const dob = new Date(author.dateOfBirth ?? "").toLocaleDateString()
    return (
        <Stack spacing={1}>
            <Avatar src={author.photo} alt={author.fullName} sx={{ width: 140, height: 140 }} />
            <Typography variant="h4">{author.fullName}</Typography>

            <Typography variant="body2">
                {author.city}, {author.country}
            </Typography>

            <Typography variant="body1" sx={{ mt: 2 }}>
                {author.bio}
            </Typography>

            <Typography variant="body1" sx={{ mt: 2 }}>
                {dob}
            </Typography>
        </Stack>
    )
}
