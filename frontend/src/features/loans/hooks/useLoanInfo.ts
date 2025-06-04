import { useQuery, useQueryClient } from "@tanstack/react-query"
import { BookLoanControllerService } from "@/api"
import { useAuth } from "@/context/AuthContext"

const MAX_LOANS = 5

export const useLoanInfo = (bookId: number) => {
    const { user } = useAuth()
    const token = localStorage.getItem("token") ?? ""
    const enabled = !!user?.roles.includes("USER")

    const qc = useQueryClient()

    const { data: count } = useQuery<number>({
        queryKey: ["loans", "count"],
        enabled,
        queryFn: () =>
            BookLoanControllerService.getCurrentLoansCount({
                authorization: `Bearer ${token}`,
            }).then((r) => r.results ?? 0),
    })

    const { data: isLoaned } = useQuery<boolean>({
        queryKey: ["loans", "status", bookId],
        enabled,
        queryFn: () =>
            BookLoanControllerService.isBookLoanedByUser({
                authorization: `Bearer ${token}`,
                bookId,
            }).then((r) => r.results ?? false),
    })

    const invalidate = () => {
        qc.invalidateQueries({ queryKey: ["loans"] })
    }

    return { count: count ?? 0, isLoaned: isLoaned ?? false, invalidate, max: MAX_LOANS }
}
