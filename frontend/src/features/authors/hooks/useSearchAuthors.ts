import { useQuery } from "@tanstack/react-query"
import { AuthorControllerService } from "@/api"
import type { APIResponsePageAuthorResponse, PageAuthorResponse } from "@/api/generated"

export const useSearchAuthors = (query: string, page: number, size = 10) =>
    useQuery<APIResponsePageAuthorResponse, Error, PageAuthorResponse>({
        queryKey: ["authors", query, page],
        queryFn: () =>
            query.trim() === ""
                ? AuthorControllerService.getAllAuthorsPaginated({ page, size })
                : AuthorControllerService.searchAuthors({ query, page, size }),
        select: (resp) => resp.results ?? { content: [], totalPages: 0, number: 0, totalElements: 0 },
        placeholderData: (prev) => prev,
    })
