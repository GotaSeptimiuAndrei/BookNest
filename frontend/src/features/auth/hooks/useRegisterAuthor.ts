import { useMutation } from "@tanstack/react-query"
import { enqueueSnackbar } from "notistack"
import axios from "@/lib/axios"
import { AuthorForm } from "../components/AuthorRegisterForm"

export const useRegisterAuthor = () =>
    useMutation({
        mutationFn: async (data: AuthorForm) => {
            const form = new FormData()
            Object.entries(data).forEach(([k, v]) => form.append(k, v as any))

            await axios.post("/api/auth/signup-author", form, {
                headers: { "Content-Type": "multipart/form-data" },
            })
        },
        onSuccess: () => enqueueSnackbar("Author registered! Check your email.", { variant: "success" }),
        onError: (e: any) => enqueueSnackbar(e.message ?? "Registration failed", { variant: "error" }),
    })
