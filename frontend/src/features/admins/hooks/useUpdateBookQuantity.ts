import { useMutation, useQueryClient } from "@tanstack/react-query"
import { enqueueSnackbar } from "notistack"
import { BookControllerService, type BookQuantityUpdateRequest, type BookResponse } from "@/api/generated"

/** Shape of a single page returned by useBooksPage */
interface BooksPage {
    content: BookResponse[]
    totalPages: number
    // add other props (pageNumber, size…) if your backend sends them
}

export interface UpdateQtyPayload {
    id: number
    delta: 1 | -1
}

export const useUpdateBookQuantity = () => {
    const qc = useQueryClient()

    return useMutation({
        // ─────────────────────────────────────────────
        mutationFn: ({ id, delta }: UpdateQtyPayload) =>
            BookControllerService.updateBookQuantity({
                id,
                requestBody: { delta } as BookQuantityUpdateRequest,
            }),

        // ─────────── optimistic update ───────────────
        onMutate: async ({ id, delta }) => {
            await qc.cancelQueries({ queryKey: ["books"] }) // pause in‑flight GETs

            // snapshot every cached books page
            const previousPages = qc.getQueriesData<BooksPage>({
                queryKey: ["books"],
            })

            // update every page that might contain the book
            previousPages.forEach(([key, page]) => {
                if (!page) return
                qc.setQueryData<BooksPage>(key, {
                    ...page,
                    content: page.content.map((b) =>
                        b.bookId === id
                            ? {
                                  ...b,
                                  copies: (b.copies ?? 0) + delta,
                                  copiesAvailable: (b.copiesAvailable ?? 0) + delta,
                              }
                            : b
                    ),
                })
            })

            return { previousPages }
        },

        onError: (_err, _vars, ctx) => {
            ctx?.previousPages.forEach(([key, data]) => qc.setQueryData(key, data))
            enqueueSnackbar("Quantity update failed", { variant: "error" })
        },

        onSuccess: () => enqueueSnackbar("Quantity updated", { variant: "success" }),

        onSettled: () => qc.invalidateQueries({ queryKey: ["books"] }),
    })
}
