import { useMutation, useQueryClient } from "@tanstack/react-query"
import { PostCommentsControllerService } from "@/api/generated"
import type { PostCommentRequest } from "@/api/generated"

export const useReplyToComment = (postId: number) => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: ({ parentId, body }: { parentId: number; body: PostCommentRequest }) =>
            PostCommentsControllerService.replyToComment({ parentId, requestBody: body }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ["post-comments", postId] }),
    })
}
