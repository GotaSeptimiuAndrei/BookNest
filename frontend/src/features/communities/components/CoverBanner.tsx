import { Box, Avatar } from "@mui/material"

interface Props {
    coverUrl?: string
    authorAvatarUrl?: string
}

const COVER_H = 160

export default function CoverBanner({ coverUrl, authorAvatarUrl }: Props) {
    return (
        <Box sx={{ position: "relative", width: "100%", height: COVER_H }}>
            <Box
                component="img"
                src={coverUrl ?? "/placeholder-cover.jpg"}
                alt="community cover"
                sx={{
                    objectFit: "cover",
                    width: "100%",
                    height: "100%",
                    borderRadius: 1,
                }}
            />

            {authorAvatarUrl && (
                <Avatar
                    src={authorAvatarUrl}
                    alt="author avatar"
                    sx={{
                        width: 128,
                        height: 128,
                        position: "absolute",
                        left: "50%",
                        bottom: -64,
                        transform: "translateX(-50%)",
                        border: (t) => `4px solid ${t.palette.background.paper}`,
                    }}
                />
            )}
        </Box>
    )
}
