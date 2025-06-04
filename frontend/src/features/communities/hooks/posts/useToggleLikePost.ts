import { useMutation, useQueryClient } from "@tanstack/react-query"
import { PostControllerService } from "@/api/generated"
import type { PostResponse } from "@/api/generated"

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

        onMutate: ({ postId, communityId, liked }) => {
            qc.setQueryData(["community-posts", communityId], (old: any) => {
                if (!old) return old
                return {
                    ...old,
                    pages: old.pages.map((page: any) => ({
                        ...page,
                        content: page.content.map((p: PostResponse) =>
                            p.postId === postId
                                ? {
                                      ...p,
                                      likedByMe: !liked,
                                      likeCount: (p.likeCount ?? 0) + (liked ? -1 : 1),
                                  }
                                : p
                        ),
                    })),
                }
            })
        },

        onSuccess: (_d, { communityId }) => qc.invalidateQueries({ queryKey: ["community-posts", communityId] }),
    })
}
