import { useState } from "react"
import { Box, Button, Chip, Grid, Rating, Typography } from "@mui/material"
import { useParams } from "react-router-dom"

import { useAuth } from "@/context/AuthContext"
import { useBook } from "../hooks/useBook"
import BookInfo from "../components/BookInfo"
import ReviewForm from "@/features/reviews/components/ReviewForm"
import ReviewCard from "@/features/reviews/components/ReviewCard"
import type { ReviewResponse } from "@/api/generated"
import { useAllReviewsForAvg } from "@/features/reviews/hooks/useAllReviewsForAvg"
import { useReviewsPaginated } from "@/features/reviews/hooks/useReviewsPaginated"
import Paginator from "@/utils/Paginator"
import { useLoanInfo } from "@/features/loans/hooks/useLoanInfo"
import { useLoanBook } from "@/features/loans/hooks/useLoanBook"
import { useHasReviewed } from "@/features/reviews/hooks/useHasReviewd"

export default function BookDetailPage() {
    const { id } = useParams()
    const bookId = Number(id)

    const { data: book, isLoading } = useBook(bookId)
    const { user } = useAuth()

    const isBasicUser = user?.roles.includes("USER") ?? false

    const { data: allReviews } = useAllReviewsForAvg(bookId)
    const avg =
        allReviews && allReviews.length ? allReviews.reduce((s, r) => s + (r.rating ?? 0), 0) / allReviews.length : 0

    const [revPage, setRevPage] = useState(0)
    const { data: revPageData } = useReviewsPaginated(bookId, revPage)

    if (isLoading || !book) return null

    const available = (book.copiesAvailable ?? 0) > 0
    return (
        <Grid container spacing={4} sx={{ p: 4 }}>
            <Grid item xs={12} md={isBasicUser ? 7 : 12}>
                <BookInfo book={book} />

                <Box sx={{ mt: 3 }}>
                    <Typography variant="h6" mb={0.5}>
                        Average rating
                    </Typography>
                    <Rating value={avg} precision={0.5} readOnly />
                    <Typography variant="caption" color="text.secondary" ml={1}>
                        ({allReviews?.length ?? 0})
                    </Typography>
                </Box>
            </Grid>

            {isBasicUser && (
                <RightColumn
                    bookId={bookId}
                    available={available}
                    copies={book.copies ?? 0}
                    availableCopies={book.copiesAvailable ?? 0}
                />
            )}

            <Grid item xs={12}>
                <Typography variant="h5" mb={2}>
                    Reviews
                </Typography>

                {revPageData?.content?.map((r: ReviewResponse) => <ReviewCard key={r.reviewId} review={r} />)}

                <Paginator page={revPage} totalPages={revPageData?.totalPages ?? 0} onChange={setRevPage} />
            </Grid>
        </Grid>
    )
}

interface RightProps {
    bookId: number
    available: boolean
    copies: number
    availableCopies: number
}

function RightColumn({ bookId, available, copies, availableCopies }: RightProps) {
    const [submitted, setSubmitted] = useState(false)

    const { count, isLoaned, max } = useLoanInfo(bookId)
    const loanMutation = useLoanBook(bookId)
    const hasReachedMax = count >= max
    const canLoanMore = availableCopies > 0 && !isLoaned && !hasReachedMax
    const { data: alreadyReviewed = false, isLoading: loadingReview } = useHasReviewed(bookId)

    return (
        <Grid item xs={12} md={5}>
            <Typography variant="h6" mb={1}>
                You have {count}/{max} books loaned
            </Typography>

            <Chip
                label={available ? "Available" : "Unavailable"}
                color={available ? "success" : "error"}
                sx={{ mb: 2 }}
            />

            <Typography variant="body2" mb={1}>
                Copies: <strong>{copies}</strong> | Available: <strong>{availableCopies}</strong>
            </Typography>

            {canLoanMore ? (
                <Button
                    variant="contained"
                    color="success"
                    sx={{ mt: 2 }}
                    onClick={() => loanMutation.mutate()}
                    disabled={loanMutation.isPending}
                >
                    Loan
                </Button>
            ) : (
                <Typography fontWeight={600} sx={{ mt: 2 }} color={isLoaned ? "success.main" : "warning.main"}>
                    {isLoaned
                        ? "Book loaned, enjoy!"
                        : hasReachedMax
                          ? "Can't loan any more books!"
                          : "Currently unavailable"}
                </Typography>
            )}

            <Typography variant="h6" mt={4} mb={1}>
                Leave a review?
            </Typography>

            {loadingReview ? null : alreadyReviewed ? (
                <Typography color="text.secondary">You already reviewed this book.</Typography>
            ) : submitted ? (
                <Typography color="success.main" fontWeight={600}>
                    Thank you for your review!
                </Typography>
            ) : (
                <ReviewForm bookId={bookId} onSuccess={() => setSubmitted(true)} />
            )}
        </Grid>
    )
}
