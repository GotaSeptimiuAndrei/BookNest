import { PostCommentsControllerService } from "@/api/generated/services/PostCommentsControllerService"
import { useMutation, useQueryClient } from "@tanstack/react-query"

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
    })
}
