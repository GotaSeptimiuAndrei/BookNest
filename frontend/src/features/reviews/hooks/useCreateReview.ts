import { ReviewControllerService } from "@/api" // ← change import
import type { ReviewRequest } from "@/api/generated"
import { useAuth } from "@/context/AuthContext"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { enqueueSnackbar } from "notistack"

export const useCreateReview = (bookId: number) => {
    const qc = useQueryClient()
    const { user } = useAuth()
    const token = localStorage.getItem("token") ?? ""

    return useMutation({
        mutationFn: (payload: ReviewRequest) =>
            ReviewControllerService.createReview({ authorization: `Bearer ${token}`, requestBody: payload }),

        onSuccess: () => {
            qc.setQueryData(["has-reviewed", bookId, user?.id], true)
            qc.invalidateQueries({ queryKey: ["reviews", bookId] })
            enqueueSnackbar("Review submitted", { variant: "success" })
        },

        onError: (e: any) =>
            enqueueSnackbar(
                e?.response?.status === 409 ? "You have already reviewed this book." : (e.message ?? "Review failed"),
                { variant: "error" }
            ),

        meta: { requiresAuth: !!user },
    })
}
