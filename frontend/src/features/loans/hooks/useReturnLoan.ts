import { useMutation, useQueryClient } from "@tanstack/react-query"
import { BookLoanControllerService } from "@/api"
import { enqueueSnackbar } from "notistack"

export const useReturnLoan = () => {
    const qc = useQueryClient()
    const token = localStorage.getItem("token") ?? ""

    return useMutation({
        mutationFn: (bookId: number) =>
            BookLoanControllerService.returnBook({
                authorization: `Bearer ${token}`,
                bookId,
            }),

        onSuccess: (_void, bookId) => {
            enqueueSnackbar("Book returned!", { variant: "success" })

            qc.invalidateQueries({ queryKey: ["loans"] })
            qc.invalidateQueries({ queryKey: ["book", bookId] })
        },

        onError: (e: any) => enqueueSnackbar(e.message ?? "Return failed", { variant: "error" }),
    })
}
