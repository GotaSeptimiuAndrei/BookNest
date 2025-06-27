import { useQuery } from "@tanstack/react-query"
import { BookLoanControllerService } from "@/api"
import { useAuth } from "@/context/AuthContext"

export const useCurrentLoans = () => {
    const { user } = useAuth()
    const token = localStorage.getItem("token") ?? ""

    return useQuery({
        queryKey: ["loans", "current", user?.id],
        enabled: !!user,
        queryFn: () =>
            BookLoanControllerService.getCurrentLoansByUser({
                authorization: `Bearer ${token}`,
            }).then((r) => r.results ?? []),
    })
}
