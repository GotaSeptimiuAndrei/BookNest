import { Navigate, Outlet, useParams } from "react-router-dom"
import { useAuth } from "@/context/AuthContext"
import { useCommunityById } from "@/features/communities/hooks/community/useCommunityById"
import { useUserMemberships } from "@/features/communities/hooks/community/useUserMemberships"
import { CircularProgress } from "@mui/material"

const RequireCommunityAccess = () => {
    const { id } = useParams<{ id: string }>()
    const communityId = Number(id)

    const { user } = useAuth()

    const { data: community, isLoading: cLoading } = useCommunityById(communityId)

    const { data: memberships = [], isLoading: mLoading } = useUserMemberships(user?.id)

    if (user?.roles.includes("ADMIN")) return <Outlet />

    if (cLoading || mLoading) return <CircularProgress />

    const isAuthor = community?.author?.authorId === user?.id
    const isMember = memberships.some((m) => m.community?.communityId === communityId)

    return isAuthor || isMember ? <Outlet /> : <Navigate to="/403" replace />
}

export default RequireCommunityAccess
