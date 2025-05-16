import { useQuery } from "@tanstack/react-query"
import { PostCommentsControllerService } from "@/api/generated"
import type { PostCommentResponse } from "@/api/generated"

export const usePostComments = (postId: number) =>
    useQuery<PostCommentResponse[]>({
        queryKey: ["post-comments", postId],
        enabled: !!postId,
        queryFn: () => PostCommentsControllerService.getCommentsForPost({ postId }).then((r) => r.results ?? []),
        staleTime: 60_000,
    })
