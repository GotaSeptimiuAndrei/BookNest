import { useMutation, useQueryClient } from "@tanstack/react-query"
import { BookLoanControllerService } from "@/api"
import { enqueueSnackbar } from "notistack"

export const useLoanBook = (bookId: number) => {
    const token = localStorage.getItem("token") ?? ""
    const qc = useQueryClient()

    return useMutation({
        mutationFn: () =>
            BookLoanControllerService.loanBook({
                authorization: `Bearer ${token}`,
                bookId,
            }),
        onSuccess: () => {
            enqueueSnackbar("Book loaned!", { variant: "success" })
            qc.invalidateQueries({ queryKey: ["loans"] }) // count + status
            qc.invalidateQueries({ queryKey: ["book", bookId] }) // refresh book
        },
        onError: (e: any) => enqueueSnackbar(e.message ?? "Loan failed", { variant: "error" }),
    })
}
