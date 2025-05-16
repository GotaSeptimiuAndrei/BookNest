import { useQuery } from "@tanstack/react-query"
import { CommunityMembershipControllerService } from "@/api/generated"

export const useCommunityMemberCount = (communityId?: number) =>
    useQuery<number>({
        queryKey: ["community-member-count", communityId],
        enabled: !!communityId,
        queryFn: () =>
            CommunityMembershipControllerService.getNrOfMembersOfCommunity({
                communityId: communityId!,
            }).then((r) => r.results!),
        staleTime: 2 * 60 * 1000,
    })
