import { useRef, useState } from "react"
import { IconButton, Badge } from "@mui/material"
import NotificationsIcon from "@mui/icons-material/Notifications"
import NotificationDropdown from "./NotificationDropdown"
import { useNotificationListener, useNotifications } from "../hooks/useNotifications"

export default function NotificationBell() {
    useNotificationListener()

    const { data: notifs = [] } = useNotifications()
    const unread = notifs.length

    const anchorRef = useRef<HTMLButtonElement | null>(null)
    const [open, setOpen] = useState(false)

    return (
        <>
            <IconButton ref={anchorRef} onClick={() => setOpen((prev) => !prev)} aria-label="notifications">
                <Badge badgeContent={unread} color="error" overlap="circular" invisible={unread === 0}>
                    <NotificationsIcon />
                </Badge>
            </IconButton>
            <NotificationDropdown anchorEl={anchorRef.current} open={open} onClose={() => setOpen(false)} />
        </>
    )
}
