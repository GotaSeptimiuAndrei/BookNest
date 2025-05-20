import { Box, Divider, List, ListItem, ListItemText, Menu, MenuItem, Typography } from "@mui/material"
import { useMarkAllNotifications, useNotificationListener, useNotifications } from "../hooks/useNotifications"
import NotificationItem from "./NotificationItem"

interface Props {
    anchorEl: HTMLElement | null
    open: boolean
    onClose: () => void
}

export default function NotificationDropdown({ anchorEl, open, onClose }: Props) {
    const { data: notifs = [] } = useNotifications()
    useNotificationListener()

    const unread = notifs.length
    const markAll = useMarkAllNotifications()

    return (
        <Menu anchorEl={anchorEl} open={open} onClose={onClose} PaperProps={{ sx: { width: 360, maxHeight: 400 } }}>
            <Box sx={{ px: 2, py: 1 }}>
                <Typography variant="subtitle1">Notifications ({unread})</Typography>
            </Box>
            <Divider />

            {unread ? (
                <>
                    <List dense>
                        {notifs.map((n) => (
                            <NotificationItem key={n.notificationId} notif={n} onCloseMenu={onClose} />
                        ))}
                    </List>
                    <Divider />
                    <MenuItem onClick={() => markAll.mutate(undefined, { onSuccess: onClose })}>
                        Mark all as read
                    </MenuItem>
                </>
            ) : (
                <ListItem>
                    <ListItemText primary="No unread notifications" />
                </ListItem>
            )}
        </Menu>
    )
}
