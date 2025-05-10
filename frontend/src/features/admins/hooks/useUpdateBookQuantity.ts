import { useMutation, useQueryClient } from "@tanstack/react-query"
import { enqueueSnackbar } from "notistack"
import { BookControllerService, type BookQuantityUpdateRequest, type BookResponse } from "@/api/generated"

interface BooksPage {
    content: BookResponse[]
    totalPages: number
}

export interface UpdateQtyPayload {
    id: number
    delta: 1 | -1
}

export const useUpdateBookQuantity = () => {
    const qc = useQueryClient()

    return useMutation({
        mutationFn: ({ id, delta }: UpdateQtyPayload) =>
            BookControllerService.updateBookQuantity({
                id,
                requestBody: { delta } as BookQuantityUpdateRequest,
            }),

        onMutate: async ({ id, delta }) => {
            await qc.cancelQueries({ queryKey: ["books"] })

            const previous = qc.getQueriesData<BooksPage>({ queryKey: ["books"] })

            previous.forEach(([key, page]) => {
                if (!page?.content) return

                qc.setQueryData<BooksPage>(key, {
                    ...page,
                    content: page.content.map((b) =>
                        b.bookId === id
                            ? {
                                  ...b,
                                  copies: Math.max(0, (b.copies ?? 0) + delta),
                                  copiesAvailable: Math.max(0, (b.copiesAvailable ?? 0) + delta),
                              }
                            : b
                    ),
                })
            })

            return { previous }
        },

        onError: (_err, _vars, ctx) => {
            ctx?.previous.forEach(([key, data]) => qc.setQueryData(key, data))
            enqueueSnackbar("Quantity update failed", { variant: "error" })
        },

        onSuccess: () => enqueueSnackbar("Quantity updated", { variant: "success" }),

        onSettled: () => qc.invalidateQueries({ queryKey: ["books"] }),
    })
}
