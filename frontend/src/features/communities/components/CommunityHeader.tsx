// features/communities/components/CommunityHeader.tsx
import { Box, CircularProgress, FormControlLabel, Radio, RadioGroup, Stack } from "@mui/material"
import { useParams } from "react-router-dom"
import { useCommunityById } from "@/features/communities/hooks/useCommunityById"
import { useCommunityMemberCount } from "@/features/communities/hooks/useCommunityMemberCount"
import { useAuthor } from "@/features/authors/hooks/useAuthor"
import { useAuth } from "@/context/AuthContext"
import CoverBanner from "./CoverBanner"
import CommunityInfoBar from "./CommunityInfoBar"

interface HeaderProps {
    onSortChange: (s: "newest" | "oldest" | "likes") => void
    sort: "newest" | "oldest" | "likes"
}

export default function CommunityHeader({ onSortChange, sort }: HeaderProps) {
    const { id } = useParams<{ id: string }>()
    const communityId = Number(id)

    /* main community */
    const { data: community, isLoading: loadingCommunity } = useCommunityById(communityId)

    /* member‑count */
    const { data: memberCount = 0 } = useCommunityMemberCount(communityId)

    /* author avatar */
    const { data: author } = useAuthor(community?.author?.fullName)

    const { user } = useAuth()
    const canEdit = user?.roles.includes("AUTHOR") && community?.author?.authorId === user?.id

    if (loadingCommunity) return <CircularProgress color="secondary" />

    return (
        <Box pb={8}>
            <CoverBanner coverUrl={community?.photo} authorAvatarUrl={author?.photo} />

            <CommunityInfoBar
                name={community?.name ?? ""}
                description={community?.description}
                memberCount={memberCount}
                canEdit={Boolean(canEdit)}
            />

            <Stack direction="row" spacing={3} sx={{ mt: 3 }}>
                <RadioGroup
                    row
                    value={sort}
                    onChange={(e) => onSortChange(e.target.value as "newest" | "oldest" | "likes")}
                >
                    <FormControlLabel value="newest" control={<Radio />} label="Newest" />
                    <FormControlLabel value="oldest" control={<Radio />} label="Oldest" />
                    <FormControlLabel value="likes" control={<Radio />} label="Most likes" />
                </RadioGroup>
            </Stack>
        </Box>
    )
}
