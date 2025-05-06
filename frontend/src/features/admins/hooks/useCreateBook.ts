import { useMutation, useQueryClient } from "@tanstack/react-query"
import { enqueueSnackbar } from "notistack"
import { BookFormData } from "../components/BookForm"
import axios from "@/lib/axios"

export const useCreateBook = () => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: async (data: BookFormData) => {
            const form = new FormData()
            Object.entries(data).forEach(([k, v]) => form.append(k, v as any))

            await axios.post("/api/books", form, {
                headers: { "Content-Type": "multipart/form-data" },
            })
        },
        onSuccess: () => {
            enqueueSnackbar("Book created", { variant: "success" })
            qc.invalidateQueries({ queryKey: ["books"] })
        },
        onError: (e: any) => enqueueSnackbar(e.message ?? "Create failed", { variant: "error" }),
    })
}
