import { useMutation, useQueryClient } from "@tanstack/react-query"
import { PostCommentsControllerService } from "@/api/generated"
import { enqueueSnackbar } from "notistack"

export const useCreateComment = (postId: number) => {
    const qc = useQueryClient()
    const token = localStorage.getItem("token") ?? ""

    return useMutation({
        mutationFn: (text: string) =>
            PostCommentsControllerService.createComment({
                authorization: `Bearer ${token}`,
                requestBody: { postId, text },
            }),
        onSuccess: () => qc.invalidateQueries({ queryKey: ["post-comments", postId] }),
        onError: (e: any) =>
            enqueueSnackbar(e.message ?? "Failed to post comment", {
                variant: "error",
            }),
    })
}
