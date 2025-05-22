import { AppBar, Box, Button, CircularProgress, IconButton, Stack, Toolbar, Typography } from "@mui/material"
import { Link as RouterLink, useNavigate } from "react-router-dom"
import { useAuth } from "@/context/AuthContext"
import { useAuthorCommunity } from "@/features/authors/hooks/useAuthorCommunity"
import { ReactNode } from "react"
import { useAuthorHasCommunity } from "@/features/authors/hooks/useAuthorHasCommunity"
import NotificationBell from "@/features/notifications/components/NotificationBell"

const NavButton = ({ to, children }: { to: string; children: ReactNode }) => (
    <Button component={RouterLink} to={to}>
        {children}
    </Button>
)

export default function Navbar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()

    const { data: hasCommunity, isLoading: checkingHasCommunity } = useAuthorHasCommunity(user?.id)

    const { data: community, isLoading: loadingCommunity } = useAuthorCommunity(user?.id, Boolean(hasCommunity))

    const RightSide = () =>
        user ? (
            <Stack direction="row" spacing={2} alignItems="center">
                {user.roles.includes("USER") && <NotificationBell />}

                <Typography variant="subtitle1">{user.displayName}</Typography>

                <IconButton
                    onClick={() => {
                        logout()
                        navigate("/login")
                    }}
                >
                    <Box component="img" src="/logout-icon.svg" alt="logout" sx={{ width: 24 }} />
                </IconButton>
            </Stack>
        ) : (
            <Stack direction="row" spacing={2}>
                <Button component={RouterLink} to="/login" variant="outlined">
                    Log in
                </Button>
                <Button component={RouterLink} to="/register" variant="contained">
                    Sign up
                </Button>
            </Stack>
        )

    const LeftLinks = () => (
        <Stack direction="row" spacing={3} alignItems="center">
            <Typography
                variant="h5"
                fontWeight={700}
                component={RouterLink}
                to="/"
                sx={{ textDecoration: "none", color: "inherit" }}
            >
                BookNest
            </Typography>

            <NavButton to="/">Home</NavButton>
            <NavButton to="/books">Search Books</NavButton>
            <NavButton to="/authors">Search Authors</NavButton>

            {user?.roles.includes("USER") && (
                <>
                    <NavButton to="/shelf">Shelf</NavButton>
                    <NavButton to="/user-communities">My Communities</NavButton>
                </>
            )}

            {user?.roles.includes("ADMIN") && (
                <>
                    <NavButton to="/admin/books">Manage Library</NavButton>
                    <NavButton to="/admin/communities">Communities</NavButton>
                </>
            )}
            {user?.roles.includes("AUTHOR") &&
                (checkingHasCommunity ? (
                    <CircularProgress />
                ) : hasCommunity ? (
                    loadingCommunity ? (
                        <CircularProgress />
                    ) : (
                        <NavButton to={`/communities/${community!.communityId}`}>{community!.name}</NavButton>
                    )
                ) : (
                    <NavButton to="/author/community/create">Create Community</NavButton>
                ))}
        </Stack>
    )

    return (
        <AppBar position="static" color="transparent" elevation={0}>
            <Toolbar sx={{ justifyContent: "space-between" }}>
                <LeftLinks />
                <RightSide />
            </Toolbar>
        </AppBar>
    )
}
