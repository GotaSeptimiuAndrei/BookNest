import { Navigate, Outlet, useLocation } from "react-router-dom"
import { useAuth, Role } from "@/context/AuthContext"
import CircularProgress from "@mui/material/CircularProgress"

interface Props {
    roles?: Role[]
}

export default function RequireAuth({ roles }: Props) {
    const { user } = useAuth()
    const location = useLocation()
    const tokenInStorage = !!localStorage.getItem("token")

    // while bootstrap is happening, show nothing instead of redirecting
    if (!user && tokenInStorage) {
        return <CircularProgress color="secondary" />
    }

    // not logged in → send to login, preserve where they wanted to go
    if (!user) {
        return <Navigate to="/login" state={{ from: location }} replace />
    }

    // missing required role → send to a “403 forbidden” page
    if (roles && !roles.some((r) => user.roles.includes(r))) {
        return <Navigate to="/403" replace />
    }

    // all good → render the child routes
    return <Outlet />
}
