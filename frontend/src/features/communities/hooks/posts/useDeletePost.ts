import { useMutation, useQueryClient } from "@tanstack/react-query"
import { PostControllerService } from "@/api/generated"
import { enqueueSnackbar } from "notistack"

export const useDeletePost = (communityId: number) => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: (postId: number) => PostControllerService.deletePost({ id: postId }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ["community-posts", communityId] }),
        onError: (e: any) =>
            enqueueSnackbar(e.message ?? "Failed to delete post", {
                variant: "error",
            }),
    })
}
