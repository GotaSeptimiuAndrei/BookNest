import { useQuery } from "@tanstack/react-query"
import { CommunityMembershipControllerService } from "@/api/generated"
import type { CommunityMembership } from "@/api/generated"

export const useUserMemberships = (userId?: number) => {
    return useQuery<CommunityMembership[]>({
        queryKey: ["user-memberships", userId],
        enabled: !!userId,
        queryFn: async () => {
            if (!userId) return []
            const res = await CommunityMembershipControllerService.getAllMembershipsForUser({
                userId,
            })
            return res.results ?? []
        },
    })
}
