import { Suspense } from "react"
import { Navigate, useRoutes } from "react-router-dom"
import { lazyImport } from "./lazy"
import { CircularProgress } from "@mui/material"
import RequireAuth from "./RequireAuth"
import RequireCommunityAccess from "./RequireCommunityAccess"

const RegisterPage = lazyImport(() => import("@/features/auth/pages/RegisterPage"))
const EmailVerificationPage = lazyImport(() => import("@/features/auth/pages/EmailVerificationPage"))
const LoginPage = lazyImport(() => import("@/features/auth/pages/LoginPage"))
const AdminBooksPage = lazyImport(() => import("@/features/admins/pages/AdminBooksPage"))
const CreateBookPage = lazyImport(() => import("@/features/admins/pages/CreateBookPage"))
const SearchBooksPage = lazyImport(() => import("@/features/books/pages/SearchBooksPage"))
const SearchAuthorsPage = lazyImport(() => import("@/features/authors/pages/SearchAuthorsPage"))
const BookDetailPage = lazyImport(() => import("@/features/books/pages/BookDetailPage"))
const ShelfPage = lazyImport(() => import("@/features/loans/pages/ShelfPage"))
const AuthorProfilePage = lazyImport(() => import("@/features/authors/pages/AuthorProfilePage"))
const CreateCommunityPage = lazyImport(() => import("@/features/authors/pages/CreateCommunityPage"))
const CommunityPage = lazyImport(() => import("@/features/communities/pages/AuthorCommunityPage"))
const EditCommunityPage = lazyImport(() => import("@/features/authors/pages/EditCommunityPage"))
const MyCommunitiesPage = lazyImport(() => import("@/features/communities/pages/UserCommunitiesPage"))
const AdminCommunitiesPage = lazyImport(() => import("@/features/admins/pages/AdminCommunitiesPage"))

export default function AppRoutes() {
    const element = useRoutes([
        /* public */
        { path: "/register", element: <RegisterPage /> },
        { path: "/verify-email", element: <EmailVerificationPage /> },
        { path: "/login", element: <LoginPage /> },
        { path: "/books", element: <SearchBooksPage /> },
        { path: "/authors", element: <SearchAuthorsPage /> },
        { path: "/books/:id", element: <BookDetailPage /> },
        { path: "/authors/:fullName", element: <AuthorProfilePage /> },

        {
            element: <RequireAuth roles={["ADMIN"]} />,
            children: [
                { path: "/admin/books", element: <AdminBooksPage /> },
                { path: "/admin/books/new", element: <CreateBookPage /> },
                { path: "/admin/communities", element: <AdminCommunitiesPage /> },
            ],
        },

        {
            element: <RequireAuth roles={["AUTHOR"]} />,
            children: [
                { path: "/author/community/create", element: <CreateCommunityPage /> },
                { path: "/author/community/edit", element: <EditCommunityPage /> },
            ],
        },

        {
            element: <RequireAuth roles={["USER"]} />,
            children: [
                { path: "/shelf", element: <ShelfPage /> },
                { path: "/user-communities", element: <MyCommunitiesPage /> },
            ],
        },

        {
            element: <RequireAuth />,
            children: [
                {
                    element: <RequireCommunityAccess />,
                    children: [{ path: "/communities/:id", element: <CommunityPage /> }],
                },
            ],
        },

        /* fallback */
        { path: "*", element: <Navigate to="/" /> },
    ])

    return <Suspense fallback={<CircularProgress color="secondary" />}>{element}</Suspense>
}
