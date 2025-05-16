import { useMutation, useQueryClient } from "@tanstack/react-query"
import axios from "@/lib/axios"

export const useDeleteComment = (postId: number) => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: (commentId: number) => axios.delete(`/api/comments/${commentId}`),
        onSuccess: () => qc.invalidateQueries({ queryKey: ["post-comments", postId] }),
    })
}
