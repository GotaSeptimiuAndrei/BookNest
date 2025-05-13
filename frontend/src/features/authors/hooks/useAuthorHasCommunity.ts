import { useQuery } from "@tanstack/react-query"
import { CommunityControllerService } from "@/api/generated"

export const useAuthorHasCommunity = (authorId?: number) =>
    useQuery<boolean>({
        queryKey: ["author-has-community", authorId],
        enabled: !!authorId,
        queryFn: async () => {
            const res = await CommunityControllerService.authorHasCommunity({
                authorId: authorId!,
            })
            return res.results!
        },
    })
