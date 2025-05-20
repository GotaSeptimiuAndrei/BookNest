import { ListItem, ListItemButton, ListItemText } from "@mui/material"
import { Link as RouterLink } from "react-router-dom"
import { useMarkNotification } from "../hooks/useNotifications"
import type { NotificationResponse } from "@/api/generated"

interface Props {
    notif: NotificationResponse
    onCloseMenu: () => void
}

export default function NotificationItem({ notif, onCloseMenu }: Props) {
    const mark = useMarkNotification()

    const handleClick = () => {
        mark.mutate(notif.notificationId!)
        onCloseMenu()
    }

    return (
        <ListItem disablePadding>
            <ListItemButton
                component={RouterLink}
                to={`/communities/${notif.communityId}`}
                onClick={handleClick}
                sx={notif.read ? undefined : { bgcolor: "action.selected" }}
            >
                <ListItemText primary={notif.message} secondary={new Date(notif.createdAt ?? "").toLocaleString()} />
            </ListItemButton>
        </ListItem>
    )
}
