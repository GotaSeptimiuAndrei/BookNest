import { useMutation, useQueryClient } from "@tanstack/react-query"
import { BookControllerService } from "@/api"
import { enqueueSnackbar } from "notistack"

export const useDeleteBook = () => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: (id: number) => BookControllerService.deleteBook({ id }),
        onSuccess: () => {
            enqueueSnackbar("Book deleted", { variant: "success" })
            qc.invalidateQueries({ queryKey: ["books"] })
        },
        onError: (e: any) => enqueueSnackbar(e.message ?? "Delete failed", { variant: "error" }),
    })
}
