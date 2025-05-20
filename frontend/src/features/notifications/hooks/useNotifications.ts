import { useEffect } from "react"
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { Client, StompSubscription } from "@stomp/stompjs"
import { NotificationControllerService, type NotificationResponse } from "@/api"
import { getSocket } from "../socket"
import { useAuth } from "@/context/AuthContext"

// Helper to build the auth header
const authHeader = (): string => {
    const token = localStorage.getItem("token")
    return token ? `Bearer ${token}` : ""
}

// Fetch unread notifications once
export const useNotifications = () => {
    const { user } = useAuth()

    return useQuery<NotificationResponse[]>({
        queryKey: ["notifications", user?.id],
        enabled: !!user?.id,
        queryFn: () =>
            NotificationControllerService.getUnread({
                authorization: authHeader(),
            }).then((r) => r.results ?? []),
        staleTime: 60_000,
    })
}

// Listen for real-time pushes and inject into cache
export const useNotificationListener = () => {
    const { user } = useAuth()
    const qc = useQueryClient()

    useEffect(() => {
        if (!user) return

        const sock: Client = getSocket()
        let sub: StompSubscription | null = null

        const subscribe = () => {
            sub = sock.subscribe(`/queue/notifications-${user.id}`, (msg) => {
                const incoming: NotificationResponse = JSON.parse(msg.body)
                qc.setQueryData<NotificationResponse[]>(["notifications", user.id], (prev = []) => [incoming, ...prev])
            })
        }

        // If already connected
        if (sock.connected) subscribe()
        sock.onConnect = subscribe

        return () => {
            sub?.unsubscribe()
        }
    }, [user, qc])
}

// Mark a single notification as read
export const useMarkNotification = () => {
    const qc = useQueryClient()
    const { user } = useAuth()

    return useMutation<void, Error, number>({
        mutationFn: (id) =>
            NotificationControllerService.markOne({
                authorization: authHeader(),
                id,
            }),
        onSuccess: (_, id) => {
            qc.setQueryData<NotificationResponse[]>(["notifications", user?.id], (prev = []) =>
                prev.filter((n) => n.notificationId !== id)
            )
        },
    })
}

// Mark all notifications as read
export const useMarkAllNotifications = () => {
    const qc = useQueryClient()
    const { user } = useAuth()

    return useMutation<void, Error, void>({
        mutationFn: () =>
            NotificationControllerService.markAll({
                authorization: authHeader(),
            }),
        onSuccess: () => {
            qc.setQueryData<NotificationResponse[]>(["notifications", user?.id], [])
        },
    })
}
