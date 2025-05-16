import { useMutation, useQueryClient } from "@tanstack/react-query"
import { PostCommentsControllerService } from "@/api/generated"
import type { PostCommentRequest } from "@/api/generated"

export const useCreateComment = (postId: number) => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: (body: PostCommentRequest) => PostCommentsControllerService.createComment({ requestBody: body }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ["post-comments", postId] }),
    })
}
