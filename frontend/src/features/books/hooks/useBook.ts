import { useQuery } from "@tanstack/react-query"
import { BookControllerService } from "@/api"
import type { BookResponse } from "@/api/generated"

export const useBook = (id: number) =>
    useQuery<BookResponse>({
        queryKey: ["book", id],
        queryFn: () => BookControllerService.getBookById({ id }).then((r) => r.results!),
        enabled: !isNaN(id),
    })
