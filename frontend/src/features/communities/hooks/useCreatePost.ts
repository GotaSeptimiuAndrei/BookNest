import { useMutation, useQueryClient } from "@tanstack/react-query"
import axios from "@/lib/axios"
import { enqueueSnackbar } from "notistack"
import type { PostRequest, PostResponse, APIResponsePostResponse } from "@/api/generated"

export const useCreatePost = (communityId: number) => {
    const qc = useQueryClient()

    return useMutation<PostResponse, Error, PostRequest>({
        mutationFn: async ({ image, ...rest }) => {
            const form = new FormData()

            form.append("communityId", String(rest.communityId))
            form.append("authorId", String(rest.authorId))
            form.append("text", rest.text)

            if (image) form.append("image", image, (image as File).name)

            const res = await axios.post<APIResponsePostResponse>("/api/posts", form, {
                headers: { "Content-Type": "multipart/form-data" },
            })

            return res.data.results!
        },

        onSuccess: () => {
            enqueueSnackbar("Post published!", { variant: "success" })
            qc.invalidateQueries({ queryKey: ["community-posts", communityId] })
        },

        onError: (e: any) =>
            enqueueSnackbar(e?.response?.data?.message ?? "Post failed", {
                variant: "error",
            }),
    })
}
