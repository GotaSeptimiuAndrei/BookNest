import { useQuery } from "@tanstack/react-query"
import { ReviewControllerService } from "@/api"
import type { ReviewResponse } from "@/api/generated"

export const useAllReviewsForAvg = (bookId: number) =>
    useQuery<ReviewResponse[]>({
        queryKey: ["reviews", "all", bookId],
        queryFn: () => ReviewControllerService.getReviewsForBook({ bookId }).then((r) => r.results ?? []),
    })
