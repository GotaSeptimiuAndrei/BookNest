import { enqueueSnackbar } from "notistack"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom"
import axios from "@/lib/axios"
import type { CommunityFormData } from "../components/CommunityForm"

import type { APIResponseCommunity, Community } from "@/api/generated"

export const useCreateCommunity = () => {
    const qc = useQueryClient()
    const navigate = useNavigate()

    return useMutation<Community, Error, CommunityFormData>({
        mutationFn: async (data) => {
            const form = new FormData()
            Object.entries(data).forEach(([k, v]) => form.append(k, v as any))

            const res = await axios.post<APIResponseCommunity>("/api/communities", form, {
                headers: { "Content-Type": "multipart/form-data" },
            })

            return res.data.results! as Community
        },

        onSuccess: (community) => {
            const authorId = community.author!.authorId!
            qc.setQueryData(["author-has-community", authorId], true)
            qc.setQueryData(["author-community", authorId], community)

            enqueueSnackbar("Community created!", { variant: "success" })
            navigate(`/communities/${community.communityId}`)
        },

        onError: (e: any) =>
            enqueueSnackbar(e?.response?.data?.message ?? "Create failed", {
                variant: "error",
            }),
    })
}
