import { useQuery } from "@tanstack/react-query"
import { BookControllerService } from "@/api"
import type { APIResponsePageBookResponse, PageBookResponse } from "@/api/generated"

export const useSearchBooks = (query: string, category: string, page: number, size = 5) =>
    useQuery<APIResponsePageBookResponse, Error, PageBookResponse>({
        queryKey: ["search-books", query, category, page],
        queryFn: () => {
            const isAll = category === "All"
            const isEmpty = query.trim() === ""

            if (isAll && isEmpty) {
                return BookControllerService.getAllBooksPaginated({ page, size })
            }

            return BookControllerService.searchBooksPaginated({
                query,
                category,
                page,
                size,
            })
        },
        select: (resp) => resp.results ?? { content: [], totalPages: 0, number: 0, totalElements: 0 },
        placeholderData: (prev) => prev,
    })
