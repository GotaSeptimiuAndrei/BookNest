import { Box, Button, Container, Stack, Typography, ThemeProvider } from "@mui/material"
import { Link as RouterLink } from "react-router-dom"
import { landingTheme } from "@/theme" // adjust the import path to your project

export default function LandingPage() {
    return (
        <ThemeProvider theme={landingTheme}>
            <Box>
                {/* hero section */}
                <Box sx={{ position: "relative", height: { xs: 300, md: 500 } }}>
                    <Box
                        component="img"
                        src="/books-banner.jpg"
                        alt="library"
                        sx={{ width: "100%", height: "100%", objectFit: "cover" }}
                    />
                    <Box
                        sx={{
                            position: "absolute",
                            top: 0,
                            left: 0,
                            width: "100%",
                            height: "100%",
                            bgcolor: "rgba(0,0,0,0.35)",
                            display: "flex",
                            flexDirection: "column",
                            alignItems: "center",
                            justifyContent: "center",
                            color: "#fff",
                            textAlign: "center",
                            p: 2,
                        }}
                    >
                        <Typography variant="h2" component="h1" gutterBottom color="secondary">
                            Discover Your Next Favorite Book
                        </Typography>
                        <Typography variant="h6" sx={{ mb: 4 }} color="secondary">
                            Join communities and connect with your favorite authors
                        </Typography>
                        <Stack direction="row" spacing={2}>
                            <Button component={RouterLink} to="/register" variant="outlined" color="secondary">
                                Get Started
                            </Button>
                            <Button component={RouterLink} to="/books" variant="outlined" color="secondary">
                                Browse Books
                            </Button>
                        </Stack>
                    </Box>
                </Box>

                {/* features section */}
                <Container sx={{ py: 8 }}>
                    <Typography variant="h4" textAlign="center" fontWeight={700} mb={4}>
                        Why BookNest?
                    </Typography>
                    <Stack direction={{ xs: "column", md: "row" }} spacing={4}>
                        <Stack spacing={2} sx={{ flex: 1, textAlign: "center" }}>
                            <Box component="img" src="/book-collection.png" sx={{ width: "100%", borderRadius: 2 }} />
                            <Typography variant="h6" fontWeight={600}>
                                Endless Collection
                            </Typography>
                            <Typography variant="body2">Explore thousands of books from all genres.</Typography>
                        </Stack>
                        <Stack spacing={2} sx={{ flex: 1, textAlign: "center" }}>
                            <Box
                                component="img"
                                src="/author-communities.png"
                                sx={{ width: "100%", borderRadius: 2 }}
                            />
                            <Typography variant="h6" fontWeight={600}>
                                Author Communities
                            </Typography>
                            <Typography variant="body2">Follow authors and engage with passionate readers.</Typography>
                        </Stack>
                        <Stack spacing={2} sx={{ flex: 1, textAlign: "center" }}>
                            <Box
                                component="img"
                                src="/notification-img.png"
                                sx={{ width: "100%", mx: "auto", borderRadius: 2 }}
                            />
                            <Typography variant="h6" fontWeight={600}>
                                Stay Updated
                            </Typography>
                            <Typography variant="body2">Get notifications about new releases and posts.</Typography>
                        </Stack>
                    </Stack>
                </Container>

                {/* community call-to-action */}
                <Box sx={{ bgcolor: "accent.main", color: "#fff", py: 8 }}>
                    <Container>
                        <Stack direction={{ xs: "column", md: "row" }} spacing={4} alignItems="center">
                            <Box
                                component="img"
                                src="/world-readers.png"
                                sx={{ width: { xs: "100%", md: "50%" }, borderRadius: 2 }}
                            />
                            <Box>
                                <Typography variant="h4" fontWeight={700} gutterBottom>
                                    Connect with Writers Worldwide
                                </Typography>
                                <Typography variant="body1" mb={3}>
                                    Share your thoughts, join live discussions and participate in events hosted by
                                    authors you love.
                                </Typography>
                                <Button component={RouterLink} to="/register">
                                    Join the Community
                                </Button>
                            </Box>
                        </Stack>
                    </Container>
                </Box>

                {/* join section */}
                <Box sx={{ position: "relative", mt: 8, height: { xs: 250, md: 350 } }}>
                    <Box
                        component="img"
                        src="/placeholder-cover.jpg"
                        alt="bookshelf"
                        sx={{ width: "100%", height: "100%", objectFit: "cover" }}
                    />
                    <Box
                        sx={{
                            position: "absolute",
                            top: 0,
                            left: 0,
                            width: "100%",
                            height: "100%",
                            bgcolor: "rgba(0,0,0,0.5)",
                            display: "flex",
                            flexDirection: "column",
                            alignItems: "center",
                            justifyContent: "center",
                            color: "#fff",
                            textAlign: "center",
                            p: 2,
                        }}
                    >
                        <Typography variant="h4" fontWeight={700} gutterBottom>
                            Start your reading adventure today
                        </Typography>
                        <Button component={RouterLink} to="/register">
                            Sign Up
                        </Button>
                    </Box>
                </Box>
            </Box>
        </ThemeProvider>
    )
}
