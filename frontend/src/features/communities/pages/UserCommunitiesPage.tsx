import {
    Box,
    Button,
    CircularProgress,
    Container,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Stack,
    Typography,
} from "@mui/material"
import { Navigate } from "react-router-dom"
import { useState } from "react"
import { useAuth } from "@/context/AuthContext"
import { useUserMemberships } from "../hooks/community/useUserMemberships"
import { useLeaveCommunity } from "../hooks/community/useLeaveCommunity"
import MembershipRow from "../components/MembershipRow"
import type { CommunityMembership } from "@/api/generated"

export default function UserCommunitiesPage() {
    const { user } = useAuth()

    const { data: memberships, isLoading, error } = useUserMemberships(user?.id)
    const leaveMut = useLeaveCommunity(user?.id)
    const [selected, setSelected] = useState<CommunityMembership | null>(null)

    if (!user) return <Navigate to="/login" replace />

    const confirmLeave = async () => {
        if (!selected) return
        await leaveMut.mutateAsync({
            userId: user.id,
            communityId: selected.community!.communityId!,
        })
        setSelected(null)
    }

    return (
        <Container maxWidth="sm" sx={{ py: 4 }}>
            <Typography variant="h4" gutterBottom>
                My Communities
            </Typography>

            {isLoading && (
                <Box sx={{ textAlign: "center", mt: 4 }}>
                    <CircularProgress />
                </Box>
            )}

            {error && <Typography color="error">Failed to load memberships.</Typography>}

            {!!memberships?.length && (
                <Stack divider={<Box sx={{ borderBottom: 1, borderColor: "divider" }} />}>
                    {memberships.map((m) => (
                        <MembershipRow key={m.membershipId} membership={m} onLeaveRequest={setSelected} />
                    ))}
                </Stack>
            )}

            {!isLoading && !error && !memberships?.length && (
                <Typography>You haven’t joined any communities yet.</Typography>
            )}

            <Dialog open={Boolean(selected)} onClose={() => setSelected(null)}>
                <DialogTitle>Leave community</DialogTitle>

                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to leave <strong>{selected?.community?.name}</strong>?
                    </DialogContentText>
                </DialogContent>

                <DialogActions>
                    <Button onClick={() => setSelected(null)}>Cancel</Button>
                    <Button color="error" onClick={confirmLeave} disabled={leaveMut.isPending}>
                        Leave
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    )
}
