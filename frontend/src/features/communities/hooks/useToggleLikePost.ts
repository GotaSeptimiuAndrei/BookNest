import { useMutation, useQueryClient } from "@tanstack/react-query"
import { PostControllerService } from "@/api/generated"

interface Params {
    postId: number
    userId: number
    liked: boolean
    communityId: number
}

export const useToggleLikePost = () => {
    const qc = useQueryClient()

    return useMutation<void, Error, Params>({
        mutationFn: async ({ postId, userId, liked }) => {
            if (liked) {
                await PostControllerService.unlikePost({ id: postId, userId })
            } else {
                await PostControllerService.likePost({ id: postId, userId })
            }
        },
        onSuccess: (_data, { communityId }) => qc.invalidateQueries({ queryKey: ["community-posts", communityId] }),
    })
}
