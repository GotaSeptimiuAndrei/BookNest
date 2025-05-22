// features/admins/pages/AdminCommunitiesPage.tsx
import { Box, CircularProgress, Container, Grid, Typography } from "@mui/material"
import { useAllCommunities } from "../hooks/useAllCommunities"
import CommunityCard from "../components/CommunityCard"

export default function AdminCommunitiesPage() {
    const { data: communities, isLoading, error } = useAllCommunities()

    return (
        <Container sx={{ py: 4 }}>
            <Typography variant="h4" gutterBottom>
                All Communities
            </Typography>

            {isLoading && <CircularProgress />}
            {error && <Typography color="error">Failed to load.</Typography>}

            <Grid container spacing={2}>
                {communities?.map((c) => (
                    <Grid item xs={12} sm={6} md={4} lg={3} key={c.communityId}>
                        <CommunityCard community={c} />
                    </Grid>
                ))}
            </Grid>

            {!isLoading && !communities?.length && (
                <Box mt={4}>
                    <Typography>No communities yet.</Typography>
                </Box>
            )}
        </Container>
    )
}
