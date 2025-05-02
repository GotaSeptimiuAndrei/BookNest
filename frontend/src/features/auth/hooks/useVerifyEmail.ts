import { useMutation } from "@tanstack/react-query"
import { AuthControllerService } from "@/api"
import type { EmailVerificationRequest } from "@/api/generated"
import { enqueueSnackbar } from "notistack"

export const useVerifyEmail = () =>
    useMutation({
        mutationFn: (data: EmailVerificationRequest) => AuthControllerService.verifyEmail({ requestBody: data }),
        onSuccess: () => enqueueSnackbar("Email verified! You can now log in.", { variant: "success" }),
        onError: (e: any) => enqueueSnackbar(e.message ?? "Verification failed", { variant: "error" }),
    })
