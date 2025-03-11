import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import { ThemeProvider } from "@mui/material/styles"

import "./index.css"
import App from "./App.tsx"
import theme from "./theme.ts"
import { AuthProvider } from "./context/AuthContext.tsx"

createRoot(document.getElementById("root")!).render(
    <StrictMode>
        <ThemeProvider theme={theme}>
            <AuthProvider>
                <App />
            </AuthProvider>
        </ThemeProvider>
    </StrictMode>
)
