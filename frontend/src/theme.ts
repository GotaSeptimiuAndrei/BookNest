import { createTheme, alpha, responsiveFontSizes } from "@mui/material/styles"

declare module "@mui/material/styles" {
    interface Palette {
        neutral: Palette["primary"]
        status: { danger: string; success: string; warning: string }
    }
    interface PaletteOptions {
        neutral?: PaletteOptions["primary"]
        status?: { danger: string; success: string; warning: string }
    }
}

const palette = {
    mode: "light",
    primary: { main: "#543A14" },
    secondary: { main: "#F0BB78" },
    neutral: { main: "#A8A29E", contrastText: "#fff" },
    status: {
        danger: "#D9544D",
        success: "#ADFFC3",
        warning: "#e38130",
    },
    background: {
        default: "#FFF0DC",
        paper: "#FFFFFF",
    },
    text: {
        primary: "#131010",
        secondary: alpha("#131010", 0.7),
    },
} as const

const themeBase = createTheme({
    palette,
    shape: { borderRadius: 12 },
    spacing: 8,
    breakpoints: {
        values: { xs: 0, sm: 600, md: 900, lg: 1200, xl: 1536 },
    },
    typography: {
        fontFamily: "'Inter', sans-serif",
        button: { textTransform: "none" },
        h1: { fontWeight: 700, fontSize: "clamp(2.2rem, 5vw, 3rem)" },
        subtitle2: { color: alpha("#131010", 0.7) },
    },
    components: {
        MuiCssBaseline: {
            styleOverrides: {
                "html, body": { height: "100%", scrollBehavior: "smooth" },
                a: { textDecoration: "none", color: palette.primary.main },
            },
        },
        MuiButton: {
            defaultProps: { variant: "contained", disableElevation: true },
            styleOverrides: {
                root: {
                    borderRadius: 12,
                    paddingInline: 8 * 3, // theme.spacing(3)
                },
            },
        },
        MuiTextField: {
            defaultProps: { variant: "outlined", size: "small" },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    boxShadow: "0 4px 20px rgba(0,0,0,0.06)",
                    transition: "transform .2s",
                    "&:hover": { transform: "translateY(-2px)" },
                },
            },
        },
    },
})

export const theme = responsiveFontSizes(themeBase)
