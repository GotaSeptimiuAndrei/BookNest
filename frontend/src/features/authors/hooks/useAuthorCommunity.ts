// src/features/authors/hooks/useAuthorCommunity.ts
import { useQuery } from "@tanstack/react-query"
import { CommunityControllerService } from "@/api/generated"
import type { Community } from "@/api/generated"

export const useAuthorCommunity = (authorId?: number, enabled: boolean = true) =>
    useQuery<Community | null>({
        queryKey: ["author-community", authorId],
        enabled: !!authorId && enabled,
        queryFn: async () => {
            if (!authorId) return null
            const res = await CommunityControllerService.getCommunityByAuthor({
                authorId,
            })
            return res.results ?? null
        },
    })
