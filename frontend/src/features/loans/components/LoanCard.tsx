import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Grid,
    Stack,
    Typography,
} from "@mui/material"
import BookCard from "@/components/BookCard"
import type { CurrentLoansResponse } from "@/api/generated"
import { useRenewLoan } from "../hooks/useRenewLoan"
import { useReturnLoan } from "../hooks/useReturnLoan"
import { useState } from "react"

interface Props {
    loan: CurrentLoansResponse
}

export default function LoanCard({ loan }: Props) {
    const { book, daysLeft = 0 } = loan

    const renew = useRenewLoan()
    const ret = useReturnLoan()

    const [open, setOpen] = useState(false)
    const [action, setAction] = useState<"renew" | "return" | null>(null)

    const handleConfirm = () => {
        if (!book) return

        if (action === "renew") renew.mutate(book.bookId)
        if (action === "return") ret.mutate(book.bookId)

        setOpen(false)
    }

    const overdue = daysLeft < 0
    const text = overdue ? `Overdue by ${-daysLeft} day(s)` : `Due in ${daysLeft} day(s)`

    return (
        <Grid container spacing={2} sx={{ mb: 3 }}>
            {/* Left: book info */}
            <Grid item xs={12} md={9}>
                {book && <BookCard book={book} />}
            </Grid>

            {/* Right: manage panel */}
            <Grid item xs={12} md={3}>
                <Stack spacing={1}>
                    <Typography variant="subtitle1" fontWeight={600}>
                        Manage Loan
                    </Typography>

                    <Typography variant="body2" color={overdue ? "error.main" : "success.main"}>
                        {text}
                    </Typography>

                    <Button
                        variant="outlined"
                        onClick={() => {
                            setAction("renew")
                            setOpen(true)
                        }}
                        disabled={overdue || renew.isPending}
                    >
                        Renew loan
                    </Button>

                    <Button
                        variant="contained"
                        color="error"
                        onClick={() => {
                            setAction("return")
                            setOpen(true)
                        }}
                        disabled={ret.isPending}
                    >
                        Return book
                    </Button>
                </Stack>
            </Grid>

            {/*Confirmation Dialog*/}
            <Dialog open={open} onClose={() => setOpen(false)}>
                <DialogTitle>Are you sure?</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {action === "renew" ? "Renew this loan for 3 more days?" : "Return this book?"}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>Cancel</Button>
                    <Button
                        color="success"
                        onClick={handleConfirm}
                        autoFocus
                        disabled={renew.isPending || ret.isPending}
                    >
                        Yes
                    </Button>
                </DialogActions>
            </Dialog>
        </Grid>
    )
}
