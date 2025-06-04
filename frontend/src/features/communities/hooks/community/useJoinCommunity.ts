import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom"
import { enqueueSnackbar } from "notistack"
import { CommunityMembership, CommunityMembershipControllerService } from "@/api/generated"

interface Params {
    userId: number
    communityId: number
}

export const useJoinCommunity = () => {
    const qc = useQueryClient()
    const navigate = useNavigate()

    return useMutation({
        mutationFn: async ({ userId, communityId }: Params) => {
            await CommunityMembershipControllerService.joinCommunity({
                requestBody: { userId, communityId },
            })
            return communityId
        },

        onSuccess: (communityId, { userId }) => {
            enqueueSnackbar("Joined the community!", { variant: "success" })
            qc.setQueryData<CommunityMembership[]>(["user-memberships", userId], (prev) =>
                prev
                    ? [
                          ...prev,
                          {
                              membershipId: 0,
                              user: { userId } as any,
                              community: { communityId } as any,
                              joinedAt: new Date().toISOString(),
                          },
                      ]
                    : [
                          {
                              membershipId: 0,
                              user: { userId } as any,
                              community: { communityId } as any,
                              joinedAt: new Date().toISOString(),
                          },
                      ]
            )
            qc.invalidateQueries({ queryKey: ["user-memberships", userId] })

            navigate(`/communities/${communityId}`)
        },

        onError: (e: any) =>
            enqueueSnackbar(e?.response?.data?.message ?? "Join failed", {
                variant: "error",
            }),
    })
}
