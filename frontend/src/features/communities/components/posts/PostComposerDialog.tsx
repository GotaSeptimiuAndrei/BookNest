import { Dialog, DialogContent, DialogTitle, IconButton } from "@mui/material"
import CloseIcon from "@mui/icons-material/Close"
import PostComposer from "./PostComposer"

interface Props {
    open: boolean
    onClose: () => void
    communityId: number
    authorId: number
}

export default function PostComposerDialog({ open, onClose, communityId, authorId }: Props) {
    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>
                New Post
                <IconButton aria-label="close" onClick={onClose} sx={{ position: "absolute", right: 8, top: 8 }}>
                    <CloseIcon />
                </IconButton>
            </DialogTitle>
            <DialogContent>
                <PostComposer communityId={communityId} authorId={authorId} onSuccess={onClose} />
            </DialogContent>
        </Dialog>
    )
}
