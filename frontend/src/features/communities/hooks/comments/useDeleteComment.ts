import { PostCommentsControllerService } from "@/api/generated/services/PostCommentsControllerService"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { enqueueSnackbar } from "notistack"

export const useDeleteComment = (postId: number) => {
    const qc = useQueryClient()
    const token = localStorage.getItem("token") ?? ""

    return useMutation({
        mutationFn: (commentId: number) =>
            PostCommentsControllerService.deleteComment({
                authorization: `Bearer ${token}`,
                commentId,
            }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ["post-comments", postId] }),
        onError: (e: any) =>
            enqueueSnackbar(e.message ?? "Failed to delete comment", {
                variant: "error",
            }),
    })
}
