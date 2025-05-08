import { Box, Stack, Typography } from "@mui/material"
import type { BookResponse } from "@/api/generated"

interface Props {
    book: BookResponse
}

export default function BookInfo({ book }: Props) {
    return (
        <Stack spacing={1}>
            <Typography variant="h4">{book.title}</Typography>
            <Typography variant="subtitle1" color="text.secondary">
                {book.author}
            </Typography>
            <Typography variant="subtitle2" fontStyle="italic" fontWeight={600}>
                {book.category}
            </Typography>
            <Box component="img" src={book.image} alt={book.title} sx={{ mt: 2, width: "100%", maxWidth: 240 }} />
            <Typography variant="body1" sx={{ mt: 2 }}>
                {book.description}
            </Typography>
        </Stack>
    )
}
