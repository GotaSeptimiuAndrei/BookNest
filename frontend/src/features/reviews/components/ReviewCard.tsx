import { Card, CardContent, Rating, Typography } from "@mui/material"
import type { ReviewResponse } from "@/api/generated"
import dayjs from "dayjs"

interface Props {
    review: ReviewResponse
}

export default function ReviewCard({ review }: Props) {
    return (
        <Card sx={{ mb: 1 }}>
            <CardContent>
                <Typography fontWeight={600}>{review.username}</Typography>

                <Rating value={review.rating ?? 0} precision={0.5} readOnly size="small" />

                <Typography variant="body2" sx={{ mt: 0.5 }}>
                    {review.reviewDescription}
                </Typography>

                <Typography variant="caption" color="text.secondary">
                    {dayjs(review.date).format("DD MMM YYYY")}
                </Typography>
            </CardContent>
        </Card>
    )
}
