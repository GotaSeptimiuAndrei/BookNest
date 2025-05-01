import { useEffect, useState } from "react"
import { websocketService } from "@/lib/websocket"
import type { Notification } from "@/api/generated"

export function useNotifications() {
    const [notifications, setNotifications] = useState<Notification[]>([])

    useEffect(() => {
        const handler = (msg: any) => {
            const notification: Notification = JSON.parse(msg.body)
            setNotifications((cur) => [notification, ...cur])
        }

        websocketService.connect(handler)
        return () => {
            websocketService.disconnect()
        }
    }, [])

    return notifications
}
