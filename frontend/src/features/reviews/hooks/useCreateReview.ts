// features/books/hooks/useCreateReview.ts
import { ReviewControllerService } from "@/api" // ← change import
import type { ReviewRequest } from "@/api/generated"
import { useAuth } from "@/context/AuthContext"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { enqueueSnackbar } from "notistack"

export const useCreateReview = () => {
    const qc = useQueryClient()
    const { user } = useAuth()
    const token = localStorage.getItem("token") ?? ""

    return useMutation({
        mutationFn: (payload: ReviewRequest) =>
            ReviewControllerService.createReview({
                authorization: `Bearer ${token}`,
                requestBody: payload,
            }),
        onSuccess: () => {
            enqueueSnackbar("Review submitted", { variant: "success" })
            qc.invalidateQueries({ queryKey: ["reviews"] })
        },
        onError: (e: any) => enqueueSnackbar(e.message ?? "Review failed", { variant: "error" }),
        meta: { requiresAuth: !!user },
    })
}
