import "./App.css"
import { ThemeProvider } from "@emotion/react"
import { theme } from "./theme"
import { CssBaseline } from "@mui/material"

function App() {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <div>Booknest starts here…</div>
        </ThemeProvider>
    )
}

export default App
