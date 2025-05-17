import { IconButton, Stack, Typography, Tooltip, Button } from "@mui/material"
import ExitIcon from "@mui/icons-material/ExitToApp"
import { Link as RouterLink } from "react-router-dom"
import type { CommunityMembership } from "@/api/generated"

interface Props {
    membership: CommunityMembership
    onLeaveRequest: (m: CommunityMembership) => void
}

export default function MembershipRow({ membership, onLeaveRequest }: Props) {
    const community = membership.community!
    const author = community.author!
    const joined = new Date(membership.joinedAt ?? "").toLocaleDateString()

    return (
        <Stack
            direction="row"
            alignItems="center"
            justifyContent="space-between"
            sx={{
                py: 1.5,
                px: 2,
                "&:hover": { bgcolor: "action.hover" },
            }}
        >
            <Stack>
                <Button
                    component={RouterLink}
                    to={`/communities/${community.communityId}`}
                    sx={{ textTransform: "none", px: 0 }}
                >
                    <Typography fontWeight={600}>{community.name}</Typography>
                </Button>
                <Typography variant="body2" color="text.secondary">
                    by {author.fullName} • joined on {joined}
                </Typography>
            </Stack>

            <Tooltip title="Leave community">
                <IconButton color="error" onClick={() => onLeaveRequest(membership)}>
                    <ExitIcon />
                </IconButton>
            </Tooltip>
        </Stack>
    )
}
