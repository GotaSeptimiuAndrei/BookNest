import { useMutation, useQueryClient } from "@tanstack/react-query"
import axios from "@/lib/axios"
import { enqueueSnackbar } from "notistack"
import { useNavigate } from "react-router-dom"
import type { APIResponseCommunity, Community } from "@/api/generated"
import { CommunityFormData } from "../components/CommunityForm"

export const useUpdateCommunity = () => {
    const qc = useQueryClient()
    const navigate = useNavigate()

    return useMutation<Community, Error, CommunityFormData>({
        mutationFn: async (data) => {
            const form = new FormData()
            Object.entries(data).forEach(([k, v]) => {
                if (v !== undefined && v !== null) form.append(k, v as any)
            })

            const res = await axios.put<APIResponseCommunity>("/api/communities", form, {
                headers: { "Content-Type": "multipart/form-data" },
            })

            return res.data.results!
        },

        onSuccess: (updated) => {
            const authorId = updated.author!.authorId!
            const communityId = updated.communityId!

            qc.setQueryData(["author-community", authorId], updated)
            qc.setQueryData(["community", communityId], updated)
            enqueueSnackbar("Community updated!", { variant: "success" })
            navigate(`/communities/${updated.communityId}`)
        },

        onError: (e: any) =>
            enqueueSnackbar(e?.response?.data?.message ?? "Update failed", {
                variant: "error",
            }),
    })
}
