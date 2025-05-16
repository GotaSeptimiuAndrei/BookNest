import { useMutation, useQueryClient } from "@tanstack/react-query"
import { PostControllerService } from "@/api/generated"

export const useDeletePost = (communityId: number) => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: (postId: number) => PostControllerService.deletePost({ id: postId }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ["community-posts", communityId] }),
    })
}
