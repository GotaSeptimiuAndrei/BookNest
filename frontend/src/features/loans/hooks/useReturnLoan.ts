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
        onSuccess: () => {
            enqueueSnackbar("Book returned!", { variant: "success" })
            qc.invalidateQueries({ queryKey: ["loans", "current"] })
        },
        onError: (e: any) => enqueueSnackbar(e.message ?? "Return failed", { variant: "error" }),
    })
}
