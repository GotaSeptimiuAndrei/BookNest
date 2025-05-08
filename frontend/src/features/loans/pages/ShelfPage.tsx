import { Container, Typography } from "@mui/material"
import { useCurrentLoans } from "../hooks/useCurrentLoans"
import LoanCard from "../components/LoanCard"

export default function ShelfPage() {
    const { data, isLoading } = useCurrentLoans()

    return (
        <Container sx={{ py: 4 }}>
            <Typography variant="h4" mb={3}>
                My Shelf
            </Typography>

            {isLoading && <Typography>Loading…</Typography>}

            {data?.length === 0 && <Typography>You have no current loans.</Typography>}

            {data?.map((loan) => <LoanCard key={loan.book?.bookId} loan={loan} />)}
        </Container>
    )
}
