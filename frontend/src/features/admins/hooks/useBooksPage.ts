import { useQuery } from "@tanstack/react-query"
import { BookControllerService } from "@/api"
import type { APIResponsePageBookResponse, PageBookResponse } from "@/api/generated"

export const useBooksPage = (page: number, size = 5) =>
    useQuery<APIResponsePageBookResponse, Error, PageBookResponse>({
        queryKey: ["books", page],
        queryFn: () => BookControllerService.getAllBooksPaginated({ page, size }),
        select: (resp) =>
            resp.results ?? {
                content: [],
                totalPages: 0,
                number: 0,
                totalElements: 0,
            },
        placeholderData: (prev) => prev,
    })
