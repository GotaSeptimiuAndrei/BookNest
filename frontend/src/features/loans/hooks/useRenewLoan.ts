import { useMutation, useQueryClient } from "@tanstack/react-query"
import { BookLoanControllerService } from "@/api"
import { enqueueSnackbar } from "notistack"

export const useRenewLoan = () => {
    const qc = useQueryClient()
    const token = localStorage.getItem("token") ?? ""

    return useMutation({
        mutationFn: (bookId: number) =>
            BookLoanControllerService.renewLoan({
                authorization: `Bearer ${token}`,
                bookId,
            }),
        onSuccess: () => {
            enqueueSnackbar("Loan renewed!", { variant: "success" })
            qc.invalidateQueries({ queryKey: ["loans", "current"] })
        },
        onError: (e: any) => enqueueSnackbar(e.message ?? "Renew failed", { variant: "error" }),
    })
}
