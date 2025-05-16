import { useQuery } from "@tanstack/react-query"
import { CommunityControllerService } from "@/api/generated"
import type { Community } from "@/api/generated"

export const useCommunityById = (communityId?: number, enabled = true) =>
    useQuery<Community | null>({
        queryKey: ["community", communityId],
        enabled: !!communityId && enabled,
        queryFn: async () => {
            if (!communityId) return null
            const res = await CommunityControllerService.getCommunityById({
                id: communityId,
            })
            return res.results ?? null
        },
        staleTime: 5 * 60 * 1000,
    })
