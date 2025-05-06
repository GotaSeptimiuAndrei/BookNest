import { Suspense } from "react"
import { Navigate, useRoutes } from "react-router-dom"
import { lazyImport } from "./lazy"
import { CircularProgress } from "@mui/material"
import RequireAuth from "./RequireAuth"

const RegisterPage = lazyImport(() => import("@/features/auth/pages/RegisterPage"))
const EmailVerificationPage = lazyImport(() => import("@/features/auth/pages/EmailVerificationPage"))
const LoginPage = lazyImport(() => import("@/features/auth/pages/LoginPage"))
const AdminBooksPage = lazyImport(() => import("@/features/admins/pages/AdminBooksPage"))
const CreateBookPage = lazyImport(() => import("@/features/admins/pages/CreateBookPage"))

//const BooksPage = lazyImport(() => import("@/features/books/pages/BooksPage"))
//const AdminDashboard = lazyImport(() => import("@/features/admin/pages/Dashboard"))

export default function AppRoutes() {
    const element = useRoutes([
        /* public */
        { path: "/register", element: <RegisterPage /> },
        { path: "/verify-email", element: <EmailVerificationPage /> },
        { path: "/login", element: <LoginPage /> },

        {
            element: <RequireAuth roles={["ADMIN"]} />,
            children: [
                { path: "/admin/books", element: <AdminBooksPage /> },
                { path: "/admin/books/new", element: <CreateBookPage /> },
            ],
        },

        /* fallback */
        { path: "*", element: <Navigate to="/" /> },
    ])

    return <Suspense fallback={<CircularProgress color="secondary" />}>{element}</Suspense>
}
