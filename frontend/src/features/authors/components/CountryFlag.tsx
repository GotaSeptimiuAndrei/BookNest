import * as Flags from "country-flag-icons/react/3x2"
import { Box, BoxProps } from "@mui/material"

type Props = { code: string } & BoxProps

export default function CountryFlag({ code, ...box }: Props) {
    const FlagSVG = (Flags as any)[code.toUpperCase()]
    if (!FlagSVG) return null

    return <Box component={FlagSVG} {...box} />
}
