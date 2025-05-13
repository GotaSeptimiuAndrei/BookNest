import { Avatar, Stack, Typography } from "@mui/material"
import CountryFlag from "react-country-flag"
import type { AuthorResponse } from "@/api/generated"
import { countryToAlpha2 } from "country-to-iso"

export default function AuthorProfileInfo({ author }: { author: AuthorResponse }) {
    return (
        <Stack spacing={1}>
            <Avatar src={author.photo} alt={author.fullName} sx={{ width: 140, height: 140 }} />
            <Typography variant="h4">{author.fullName}</Typography>

            <Typography variant="body2">
                {author.city},{" "}
                {author.country && (
                    <>
                        <CountryFlag
                            countryCode={countryToAlpha2(author.country)}
                            svg
                            style={{ width: "1.5em", height: "1.5em", marginRight: 4 }}
                        />
                        {author.country}
                    </>
                )}
            </Typography>

            <Typography variant="body1" sx={{ mt: 2 }}>
                {author.bio}
            </Typography>
        </Stack>
    )
}
