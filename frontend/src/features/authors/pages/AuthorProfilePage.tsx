// src/features/authors/pages/AuthorProfilePage.tsx
import { Box, Button, Container, Typography } from "@mui/material"
import { useParams } from "react-router-dom"
import { useAuthor } from "@/features/authors/hooks/useAuthor"
import AuthorProfileInfo from "../components/AuthorProfileInfo"
import { useAuth } from "@/context/AuthContext"
import { useUserMemberships } from "@/features/communities/hooks/useUserMemberships"
import { useJoinCommunity } from "@/features/communities/hooks/useJoinCommunity"
import { useAuthorCommunity } from "../hooks/useAuthorCommunity"

export default function AuthorProfilePage() {
    const { fullName } = useParams<{ fullName: string }>()
    const { data: author, isLoading: authorLoading } = useAuthor(fullName)
    const { user } = useAuth()

    console.log(author)
    const { data: community, isLoading: communityLoading } = useAuthorCommunity(author?.authorId)

    const { data: memberships = [] } = useUserMemberships(user?.id)

    const hasJoined = Boolean(community && memberships.some((m) => m.community?.communityId === community.communityId))

    const join = useJoinCommunity()

    if (authorLoading || !author) return null

    const isBasicUser = user?.roles.includes("USER")

    return (
        <Container sx={{ py: 4 }}>
            <AuthorProfileInfo author={author} />

            {isBasicUser && !communityLoading && (
                <Box sx={{ mt: 4 }}>
                    {community ? (
                        !hasJoined ? (
                            <Button
                                variant="contained"
                                color="success"
                                size="large"
                                disabled={join.isPending}
                                onClick={() =>
                                    join.mutate({
                                        userId: user!.id,
                                        communityId: community.communityId!,
                                    })
                                }
                            >
                                Join Community
                            </Button>
                        ) : (
                            <Typography variant="h6" color="text.secondary">
                                Already a member
                            </Typography>
                        )
                    ) : (
                        <Typography variant="h6" color="text.secondary">
                            This author has no community
                        </Typography>
                    )}
                </Box>
            )}
        </Container>
    )
}
