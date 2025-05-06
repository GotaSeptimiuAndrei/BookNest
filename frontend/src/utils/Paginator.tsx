import { Pagination, Stack } from "@mui/material"

interface Props {
    page: number
    totalPages: number
    onChange: (page: number) => void
}

export default function Paginator({ page, totalPages, onChange }: Props) {
    if (totalPages <= 1) return null

    return (
        <Stack alignItems="center" mt={4}>
            <Pagination page={page + 1} count={totalPages} onChange={(_, p) => onChange(p - 1)} color="primary" />
        </Stack>
    )
}
