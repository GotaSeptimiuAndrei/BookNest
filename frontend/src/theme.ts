import { createTheme, alpha } from "@mui/material/styles"

const palette = {
    primary: { main: "#543A14" },
    secondary: { main: "#F0BB78" },
    background: {
        default: "#FFF0DC",
        paper: "#FFFFFF",
    },
    text: {
        primary: "#131010",
        secondary: alpha("#131010", 0.7),
    },
}

export const theme = createTheme({
    palette,
    shape: { borderRadius: 12 },
    typography: {
        fontFamily: `'Inter', sans-serif`,
        button: { textTransform: "none" },
    },
})
