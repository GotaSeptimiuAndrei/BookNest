import { Box, InputAdornment, Tab, Tabs, TextField } from "@mui/material"
import SearchIcon from "@mui/icons-material/Search"

interface Props {
    query: string
    onQuery: (v: string) => void
    category: string
    onCategory: (c: string) => void
}

const categories = ["All", "Science-Fiction", "Non-Fiction", "Psychology"]

export default function BooksSearchBar({ query, onQuery, category, onCategory }: Props) {
    return (
        <Box>
            <TextField
                fullWidth
                placeholder="Search books…"
                value={query}
                onChange={(e) => onQuery(e.target.value)}
                InputProps={{
                    startAdornment: (
                        <InputAdornment position="start">
                            <SearchIcon />
                        </InputAdornment>
                    ),
                }}
                sx={{ mb: 2 }}
            />

            <Tabs value={category} onChange={(_, v) => onCategory(v)} variant="scrollable" scrollButtons="auto">
                {categories.map((c) => (
                    <Tab key={c} label={c} value={c} />
                ))}
            </Tabs>
        </Box>
    )
}
