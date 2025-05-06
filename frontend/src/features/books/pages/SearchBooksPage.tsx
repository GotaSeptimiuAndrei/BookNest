import { useState } from "react"
import { CircularProgress, Container, Typography } from "@mui/material"
import BookCard from "@/components/BookCard"
import Paginator from "@/utils/Paginator"
import { useSearchBooks } from "../hooks/useSearchBooks"
import type { BookResponse } from "@/api/generated"
import BooksSearchBar from "../components/BooksSearchBar"
import { useDebounce } from "@/hooks/useDebounce"

export default function SearchBooksPage() {
    const [query, setQuery] = useState("")
    const [category, setCategory] = useState("All")
    const [page, setPage] = useState(0)

    const debounced = useDebounce(query, 400)

    const { data, isLoading } = useSearchBooks(debounced, category, page)

    return (
        <Container sx={{ py: 4 }}>
            <Typography variant="h4" mb={3}>
                Browse Books
            </Typography>

            <BooksSearchBar
                query={query}
                onQuery={(v) => {
                    setQuery(v)
                    setPage(0)
                }}
                category={category}
                onCategory={(c) => {
                    setCategory(c)
                    setPage(0)
                }}
            />

            {isLoading && <CircularProgress />}

            {data?.content?.map((b: BookResponse) => <BookCard key={b.bookId} book={b} />)}

            <Paginator page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />
        </Container>
    )
}
