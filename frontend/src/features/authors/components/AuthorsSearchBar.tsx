import { InputAdornment, TextField } from "@mui/material"
import SearchIcon from "@mui/icons-material/Search"

interface Props {
    value: string
    onChange: (v: string) => void
}

export default function AuthorsSearchBar({ value, onChange }: Props) {
    return (
        <TextField
            fullWidth
            placeholder="Search authors…"
            value={value}
            onChange={(e) => onChange(e.target.value)}
            InputProps={{
                startAdornment: (
                    <InputAdornment position="start">
                        <SearchIcon />
                    </InputAdornment>
                ),
            }}
            sx={{ mb: 2 }}
        />
    )
}
