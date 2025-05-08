import { useQuery } from "@tanstack/react-query"
import { ReviewControllerService } from "@/api"
import type { APIResponsePageReviewResponse, PageReviewResponse } from "@/api/generated"

export const useReviewsPaginated = (bookId: number, page: number, size = 5) =>
    useQuery<APIResponsePageReviewResponse, Error, PageReviewResponse>({
        queryKey: ["reviews", bookId, page],
        queryFn: () =>
            ReviewControllerService.getReviewsForBookPaginated({
                bookId,
                page,
                size,
            }),
        select: (r) => r.results ?? { content: [], totalPages: 0, number: 0, totalElements: 0 },
        placeholderData: (prev) => prev,
    })
