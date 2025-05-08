import { useState } from "react"
import {
    Container,
    Typography,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogContentText,
    DialogActions,
    Button,
    Fab,
    CircularProgress,
} from "@mui/material"
import AddIcon from "@mui/icons-material/Add"
import { useNavigate } from "react-router-dom"
import { useBooksPage } from "@/features/admins/hooks/useBooksPage"
import { useDeleteBook } from "../hooks/useDeleteBook"
import BookCard from "@/components/BookCard"
import Paginator from "@/utils/Paginator"
import type { BookResponse } from "@/api/generated"
import { useUpdateBookQuantity } from "../hooks/useUpdateBookQuantity"

export default function AdminBooksPage() {
    const navigate = useNavigate()
    const [page, setPage] = useState(0)
    const [selected, setSelected] = useState<BookResponse | null>(null)

    const { data, isLoading } = useBooksPage(page)
    const deleteBook = useDeleteBook()
    const qtyMutation = useUpdateBookQuantity()

    const confirmDelete = async () => {
        if (selected) await deleteBook.mutateAsync(selected.bookId)
        setSelected(null)
    }

    return (
        <Container sx={{ py: 4 }}>
            <Typography variant="h4" mb={3}>
                Manage Library
            </Typography>

            {isLoading && <CircularProgress />}

            {data?.content?.map((b) => {
                const inc = () => qtyMutation.mutate({ id: b.bookId, delta: 1 })
                const dec = () => qtyMutation.mutate({ id: b.bookId, delta: -1 })

                // disable if no stock OR mutation running
                const disableDec = b.copies === 0 || qtyMutation.isPending

                return (
                    <BookCard
                        key={b.bookId}
                        book={b}
                        editable
                        onDelete={() => setSelected(b)}
                        onIncrease={inc}
                        onDecrease={dec}
                        disableDecrease={disableDec}
                    />
                )
            })}

            <Paginator page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />

            <Fab
                color="primary"
                aria-label="add"
                sx={{ position: "fixed", bottom: 24, right: 24 }}
                onClick={() => navigate("/admin/books/new")}
            >
                <AddIcon />
            </Fab>

            <Dialog open={!!selected} onClose={() => setSelected(null)}>
                <DialogTitle>Delete book</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete <strong>{selected?.title}</strong>?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setSelected(null)}>Cancel</Button>
                    <Button color="error" onClick={confirmDelete} disabled={deleteBook.isPending}>
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    )
}
