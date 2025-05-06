import RemoveIcon from "@mui/icons-material/Remove"
import AddIcon from "@mui/icons-material/Add"
import { Card, CardContent, CardMedia, Typography, Stack, IconButton, Button, Box } from "@mui/material"
import type { BookResponse } from "@/api/generated"

interface Props {
    book: BookResponse
    editable?: boolean
    onDelete?: () => void
    onIncrease?: () => void
    onDecrease?: () => void
    disableDecrease?: boolean
}

export default function BookCard({ book, editable, onDelete, onIncrease, onDecrease, disableDecrease }: Props) {
    return (
        <Card sx={{ display: "flex", mb: 2 }}>
            <CardMedia component="img" image={book.image} alt={book.title} sx={{ width: 120 }} />

            <CardContent sx={{ flex: 1 }}>
                <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                    <Typography variant="h6">{book.title}</Typography>

                    <Typography variant="subtitle2" sx={{ fontWeight: "bold", fontStyle: "italic" }}>
                        {book.category}
                    </Typography>
                </Box>

                <Typography variant="subtitle2" color="text.secondary">
                    {book.author}
                </Typography>

                {editable && (
                    <Typography variant="body2" sx={{ mt: 0.5 }}>
                        Copies:&nbsp;
                        <strong>{book.copies ?? 0}</strong>&nbsp;|&nbsp;Available:&nbsp;
                        <strong>{book.copiesAvailable ?? 0}</strong>
                    </Typography>
                )}

                <Typography variant="body2" sx={{ mt: 1 }}>
                    {book.description}
                </Typography>

                {editable && (
                    <Stack direction="row" spacing={1} sx={{ mt: 2 }}>
                        <IconButton size="small" onClick={onIncrease}>
                            <AddIcon fontSize="inherit" />
                        </IconButton>

                        <IconButton size="small" onClick={onDecrease} disabled={disableDecrease}>
                            <RemoveIcon fontSize="inherit" />
                        </IconButton>

                        <Button size="small" color="error" variant="contained" onClick={onDelete}>
                            Delete
                        </Button>
                    </Stack>
                )}
            </CardContent>
        </Card>
    )
}
