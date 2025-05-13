import { CircularProgress, Container, Typography } from "@mui/material"
import { useState } from "react"
import AuthorsSearchBar from "../components/AuthorsSearchBar"
import AuthorCard from "../components/AuthorCard"
import type { AuthorResponse } from "@/api/generated"
import Paginator from "@/utils/Paginator"
import { useDebounce } from "@/hooks/useDebounce"
import { useSearchAuthors } from "../hooks/useSearchAuthors"

export default function SearchAuthorsPage() {
    const [query, setQuery] = useState("")
    const [page, setPage] = useState(0)

    const debounced = useDebounce(query, 400)
    const { data, isLoading } = useSearchAuthors(debounced, page)

    return (
        <Container sx={{ py: 4 }}>
            <Typography variant="h4" mb={3}>
                Browse Authors
            </Typography>

            <AuthorsSearchBar
                value={query}
                onChange={(v) => {
                    setQuery(v)
                    setPage(0)
                }}
            />

            {isLoading && <CircularProgress />}

            {data?.content?.map((a: AuthorResponse) => <AuthorCard key={a.fullName} {...a} />)}

            <Paginator page={page} totalPages={data?.totalPages ?? 0} onChange={setPage} />
        </Container>
    )
}
