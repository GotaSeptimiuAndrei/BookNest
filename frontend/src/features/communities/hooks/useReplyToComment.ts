import { useMutation, useQueryClient } from "@tanstack/react-query"
import { PostCommentsControllerService } from "@/api/generated"

export const useReplyToComment = (postId: number) => {
    const qc = useQueryClient()
    const token = localStorage.getItem("token") ?? ""

    return useMutation({
        mutationFn: ({ parentId, text }: { parentId: number; text: string }) =>
            PostCommentsControllerService.replyToComment({
                authorization: `Bearer ${token}`,
                parentId,
                requestBody: { postId, text },
            }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ["post-comments", postId] }),
    })
}
