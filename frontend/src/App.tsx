import { BrowserRouter, Route, Routes } from "react-router-dom"
import "./App.css"
import { HomePage } from "./pages/HomePage/HomePage"
import RegisterPage from "./pages/RegisterPage/RegisterPage"
import LoginPage from "./pages/LoginPage/LoginPage"
import VerifyEmailPage from "./pages/VerifyEmailPage/VerifyEmailPage"

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/verify-email" element={<VerifyEmailPage />} />
            </Routes>
        </BrowserRouter>
    )
}

export default App
