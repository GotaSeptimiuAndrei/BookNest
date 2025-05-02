import { Suspense } from "react"
import { Navigate, useRoutes } from "react-router-dom"
import { lazyImport } from "./lazy"
import { CircularProgress } from "@mui/material"

const RegisterPage = lazyImport(() => import("@/features/auth/pages/RegisterPage"))
const EmailVerificationPage = lazyImport(() => import("@/features/auth/pages/EmailVerificationPage"))
const LoginPage = lazyImport(() => import("@/features/auth/pages/LoginPage"))

//const BooksPage = lazyImport(() => import("@/features/books/pages/BooksPage"))
//const AdminDashboard = lazyImport(() => import("@/features/admin/pages/Dashboard"))

export default function AppRoutes() {
    const element = useRoutes([
        /* public */
        //{ path: "/", element: <BooksPage /> },
        { path: "/register", element: <RegisterPage /> },
        { path: "/verify-email", element: <EmailVerificationPage /> },
        { path: "/login", element: <LoginPage /> },

        /* fallback */
        { path: "*", element: <Navigate to="/" /> },
    ])

    return <Suspense fallback={<CircularProgress color="secondary" />}>{element}</Suspense>
}
