import "./App.css"
import { ThemeProvider } from "@emotion/react"
import { theme } from "./theme"
import { CssBaseline } from "@mui/material"
import { QueryClientProvider } from "@tanstack/react-query"
import { queryClient } from "./lib/queryClient"
import { SnackbarProvider } from "notistack"
import { AuthProvider } from "./context/AuthContext"
import { BrowserRouter } from "react-router-dom"
import AppRoutes from "./routes/AppRoutes"
import Navbar from "./components/Navbar"

function App() {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />

            <QueryClientProvider client={queryClient}>
                <SnackbarProvider
                    maxSnack={3}
                    autoHideDuration={4000}
                    anchorOrigin={{ vertical: "top", horizontal: "center" }}
                >
                    <AuthProvider>
                        <BrowserRouter>
                            <Navbar />
                            <AppRoutes />
                        </BrowserRouter>
                    </AuthProvider>
                </SnackbarProvider>
            </QueryClientProvider>
        </ThemeProvider>
    )
}

export default App
