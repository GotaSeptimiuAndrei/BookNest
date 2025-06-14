import { useMutation, useQueryClient } from "@tanstack/react-query"
import { CommunityMembershipControllerService, type CommunityMembershipDTO } from "@/api/generated"
import { enqueueSnackbar } from "notistack"

export const useLeaveCommunity = (userId?: number) => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: (payload: CommunityMembershipDTO) =>
            CommunityMembershipControllerService.leaveCommunity({
                requestBody: payload,
            }),

        onSuccess: () => {
            enqueueSnackbar("Left community", { variant: "success" })
            if (userId) qc.invalidateQueries({ queryKey: ["user-memberships", userId] })
        },

        onError: (e: any) =>
            enqueueSnackbar(e?.response?.data?.message ?? "Leave failed", {
                variant: "error",
            }),
    })
}
