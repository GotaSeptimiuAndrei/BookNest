import { useQuery } from "@tanstack/react-query"
import { CommunityControllerService, type Community } from "@/api/generated"

export const useAllCommunities = () =>
    useQuery<Community[]>({
        queryKey: ["all-communities"],
        queryFn: () => CommunityControllerService.getAllCommunities().then((r) => r.results ?? []),
        staleTime: 60_000,
    })
