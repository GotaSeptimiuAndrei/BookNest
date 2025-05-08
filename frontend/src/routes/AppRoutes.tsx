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
const SearchBooksPage = lazyImport(() => import("@/features/books/pages/SearchBooksPage"))
const SearchAuthorsPage = lazyImport(() => import("@/features/authors/pages/SearchAuthorsPage"))
const BookDetailPage = lazyImport(() => import("@/features/books/pages/BookDetailPage"))
const ShelfPage = lazyImport(() => import("@/features/loans/pages/ShelfPage"))

export default function AppRoutes() {
    const element = useRoutes([
        /* public */
        { path: "/register", element: <RegisterPage /> },
        { path: "/verify-email", element: <EmailVerificationPage /> },
        { path: "/login", element: <LoginPage /> },
        { path: "/books", element: <SearchBooksPage /> },
        { path: "/authors", element: <SearchAuthorsPage /> },
        { path: "/books/:id", element: <BookDetailPage /> },

        {
            element: <RequireAuth roles={["ADMIN"]} />,
            children: [
                { path: "/admin/books", element: <AdminBooksPage /> },
                { path: "/admin/books/new", element: <CreateBookPage /> },
            ],
        },

        {
            element: <RequireAuth roles={["USER"]} />,
            children: [{ path: "/shelf", element: <ShelfPage /> }],
        },

        /* fallback */
        { path: "*", element: <Navigate to="/" /> },
    ])

    return <Suspense fallback={<CircularProgress color="secondary" />}>{element}</Suspense>
}
