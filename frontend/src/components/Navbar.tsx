import { AppBar, Box, Button, IconButton, Stack, Toolbar, Typography } from "@mui/material"
import { Link as RouterLink, useNavigate } from "react-router-dom"
import { useAuth } from "@/context/AuthContext"

const NavButton = ({ to, children }: { to: string; children: string }) => (
    <Button component={RouterLink} to={to}>
        {children}
    </Button>
)

export default function Navbar() {
    const { user, logout } = useAuth()
    const navigate = useNavigate()

    const RightSide = () =>
        user ? (
            <Stack direction="row" spacing={2} alignItems="center">
                {user.roles.includes("USER") && (
                    <IconButton>
                        <Box component="img" src="/notification-bell.svg" alt="notifications" sx={{ width: 24 }} />
                    </IconButton>
                )}

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
                    <NavButton to="/communities">My Communities</NavButton>
                </>
            )}

            {user?.roles.includes("ADMIN") && <NavButton to="/admin/books">Manage Library</NavButton>}

            {user?.roles.includes("AUTHOR") && <NavButton to="/author/community">Create Community</NavButton>}
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
