import { useInfiniteQuery } from "@tanstack/react-query"
import { PostControllerService } from "@/api/generated"
import type { PostResponse } from "@/api/generated"

interface Page {
    content: PostResponse[]
    number: number
    last: boolean
}

export const useCommunityPosts = (communityId: number, sort: "newest" | "oldest" | "likes" = "newest") =>
    useInfiniteQuery<Page, Error>({
        queryKey: ["community-posts", communityId, sort],
        initialPageParam: 0,

        queryFn: ({ pageParam }) => {
            const token = localStorage.getItem("token")
            return PostControllerService.getPostsByCommunityPaginated({
                communityId,
                authorization: token ? `Bearer ${token}` : undefined,
                page: pageParam as number,
                size: 5,
                sort,
            } as any).then((r) => r.results! as Page)
        },

        getNextPageParam: (last) => (last.last ? undefined : last.number + 1),
        staleTime: 60_000,
    })
