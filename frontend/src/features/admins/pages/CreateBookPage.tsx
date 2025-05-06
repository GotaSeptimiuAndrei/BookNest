import { Container, Typography } from "@mui/material"
import { useNavigate } from "react-router-dom"
import BookForm, { BookFormData } from "../components/BookForm"
import { useCreateBook } from "../hooks/useCreateBook"

export default function CreateBookPage() {
    const create = useCreateBook()
    const navigate = useNavigate()

    const handleSubmit = async (data: BookFormData) => {
        console.log(data)
        await create.mutateAsync(data)
        navigate("/admin/books")
    }

    return (
        <Container sx={{ py: 4, maxWidth: 600 }}>
            <Typography variant="h4" mb={3}>
                Add new book
            </Typography>
            <BookForm onSubmit={handleSubmit} loading={create.isPending} />
        </Container>
    )
}
