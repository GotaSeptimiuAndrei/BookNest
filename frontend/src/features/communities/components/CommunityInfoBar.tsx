import { Box, Button, Chip, Stack, Typography, Tooltip } from "@mui/material"
import { Link as RouterLink } from "react-router-dom"

interface Props {
    name: string
    description?: string
    memberCount: number
    canEdit: boolean
}

export default function CommunityInfoBar({ name, description, memberCount, canEdit }: Props) {
    return (
        <Stack
            direction={{ xs: "column", sm: "row" }}
            justifyContent="space-between"
            spacing={2}
            sx={{ mt: { xs: 8, sm: 10 } }}
        >
            <Box>
                <Typography variant="h4" fontWeight={700}>
                    {name}
                </Typography>
                {description && (
                    <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }}>
                        {description}
                    </Typography>
                )}
            </Box>

            <Stack direction="row" spacing={2} alignItems="center">
                <Chip
                    label={`${memberCount} member${memberCount === 1 ? "" : "s"}`}
                    color="primary"
                    variant="outlined"
                />

                {canEdit && (
                    <Tooltip title="Edit community">
                        <Button component={RouterLink} to="/author/community/edit" sx={{ minWidth: 40, p: 0 }}>
                            <Box
                                component="img"
                                src="/edit-community.png"
                                alt="edit community"
                                sx={{ width: 32, height: 32 }}
                            />
                        </Button>
                    </Tooltip>
                )}
            </Stack>
        </Stack>
    )
}
