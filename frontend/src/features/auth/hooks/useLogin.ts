import { useMutation } from "@tanstack/react-query"
import { AuthControllerService } from "@/api"
import type { LoginRequest } from "@/api/generated"
import { enqueueSnackbar } from "notistack"
import { useAuth } from "@/context/AuthContext"

export const useLogin = () => {
    const { login } = useAuth()

    return useMutation({
        mutationFn: (data: LoginRequest) => AuthControllerService.loginUser({ requestBody: data }),
        onSuccess: (token) => {
            login(token)
            enqueueSnackbar("Logged in!", { variant: "success" })
        },
        onError: (e: any) => enqueueSnackbar(e.message ?? "Invalid credentials", { variant: "error" }),
    })
}
