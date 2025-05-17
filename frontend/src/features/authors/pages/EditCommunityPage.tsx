import { Box, CircularProgress, Container, Typography } from "@mui/material"
import { Navigate } from "react-router-dom"
import { useAuth } from "@/context/AuthContext"
import { useAuthorCommunity } from "@/features/authors/hooks/useAuthorCommunity"
import { useUpdateCommunity } from "@/features/authors/hooks/useUpdateCommunity"
import CommunityForm from "../components/CommunityForm"

export default function EditCommunityPage() {
    const { user } = useAuth()
    const { data: community, isLoading, error } = useAuthorCommunity(user?.id)
    const update = useUpdateCommunity()

    if (!user || !user.roles.includes("AUTHOR")) return <Navigate to="/" replace />

    if (isLoading)
        return (
            <Box sx={{ pt: 4, textAlign: "center" }}>
                <CircularProgress />
            </Box>
        )

    if (!community || error)
        return (
            <Container sx={{ py: 4 }}>
                <Typography color="error">Could not load community information.</Typography>
            </Container>
        )

    return (
        <Container maxWidth="sm" sx={{ py: 4 }}>
            <CommunityForm
                authorId={user.id}
                initial={community}
                loading={update.isPending}
                onSubmit={(d) => update.mutateAsync(d)}
            />
        </Container>
    )
}
