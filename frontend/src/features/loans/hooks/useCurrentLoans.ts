import { useQuery } from "@tanstack/react-query"
import { BookLoanControllerService } from "@/api"
import type { CurrentLoansResponse } from "@/api/generated"

export const useCurrentLoans = () => {
    const token = localStorage.getItem("token") ?? ""

    return useQuery<CurrentLoansResponse[]>({
        queryKey: ["loans", "current"],
        queryFn: () =>
            BookLoanControllerService.getCurrentLoansByUser({
                authorization: `Bearer ${token}`,
            }).then((r) => r.results ?? []),
    })
}
