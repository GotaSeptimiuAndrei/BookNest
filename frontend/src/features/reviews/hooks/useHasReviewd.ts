// src/features/reviews/hooks/useHasReviewed.ts
import { useQuery } from "@tanstack/react-query"
import { ReviewControllerService } from "@/api"
import { useAuth } from "@/context/AuthContext"

export const useHasReviewed = (bookId: number) => {
    const { user } = useAuth()
    const token = localStorage.getItem("token") ?? ""
    return useQuery<boolean>({
        queryKey: ["has-reviewed", bookId, user?.id],
        enabled: !!user && !!bookId,
        queryFn: () =>
            ReviewControllerService.hasReviewed({ bookId, authorization: `Bearer ${token}` }).then(
                (r) => r.results ?? false
            ),
        staleTime: 60_000,
    })
}
