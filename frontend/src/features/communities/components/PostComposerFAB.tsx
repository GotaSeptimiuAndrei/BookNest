// features/communities/components/PostComposerFAB.tsx
import AddIcon from "@mui/icons-material/Add"
import { Box, Fab } from "@mui/material"
import { useState } from "react"
import PostComposerDialog from "./PostComposerDialog"

interface Props {
    communityId: number
    authorId: number
}

export default function PostComposerFAB({ communityId, authorId }: Props) {
    const [open, setOpen] = useState(false)

    return (
        <>
            <Box
                sx={{
                    position: "fixed",
                    bottom: 32,
                    right: 32,
                    zIndex: 1100,
                }}
            >
                <Fab color="primary" onClick={() => setOpen(true)}>
                    <AddIcon />
                </Fab>
            </Box>

            <PostComposerDialog
                open={open}
                onClose={() => setOpen(false)}
                communityId={communityId}
                authorId={authorId}
            />
        </>
    )
}
