import { TextField, Stack } from "@mui/material"
import { useRef } from "react"

interface Props {
    value: string
    onChange: (val: string) => void
    disabled?: boolean
}

export default function OtpInput({ value, onChange, disabled }: Props) {
    const inputs = useRef<Array<HTMLInputElement | null>>([])

    const handleChange = (i: number, v: string) => {
        if (!/^[0-9]?$/u.test(v)) return // only digits
        const newVal = value.split("")
        newVal[i] = v
        onChange(newVal.join("").slice(0, 6))

        if (v && inputs.current[i + 1]) inputs.current[i + 1]!.focus()
    }

    const handleKey = (i: number, e: React.KeyboardEvent<HTMLInputElement | HTMLDivElement>) => {
        if (e.key === "Backspace" && !value[i] && inputs.current[i - 1]) {
            inputs.current[i - 1]!.focus()
        }
    }

    return (
        <Stack direction="row" spacing={1} justifyContent="center">
            {Array.from({ length: 6 }).map((_, i) => (
                <TextField
                    key={i}
                    inputRef={(el) => (inputs.current[i] = el)}
                    value={value[i] ?? ""}
                    onChange={(e) => handleChange(i, e.target.value)}
                    onKeyDown={(e) => handleKey(i, e)}
                    inputProps={{ maxLength: 1, style: { textAlign: "center", width: 40 } }}
                    disabled={disabled}
                />
            ))}
        </Stack>
    )
}
