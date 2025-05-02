import { useMutation } from "@tanstack/react-query"
import { AuthControllerService } from "@/api"
import type { UserSignupRequest } from "@/api/generated"
import { enqueueSnackbar } from "notistack"

export const useRegisterUser = () =>
    useMutation({
        mutationFn: (data: UserSignupRequest) => AuthControllerService.registerUser({ requestBody: data }),
        onSuccess: () => enqueueSnackbar("User registered! Check your email.", { variant: "success" }),
        onError: (e: any) => enqueueSnackbar(e.message ?? "Registration failed", { variant: "error" }),
    })
