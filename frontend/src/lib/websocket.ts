// src/lib/websocket.ts
import { Client, IMessage, Frame } from "@stomp/stompjs"
import SockJS from "sockjs-client"

const WS_URL = import.meta.env.VITE_BACKEND_URL + "/ws"

export class WebSocketService {
    private client: Client

    constructor() {
        this.client = new Client({
            // SockJS fallback if native WebSocket fails
            webSocketFactory: () => new SockJS(WS_URL),
            // auto-reconnect after 5s
            reconnectDelay: 5000,
            // debug logging (optional)
            debug: (msg) => console.debug("[STOMP]", msg),
        })
    }

    connect(onMessage: (msg: IMessage) => void) {
        const token = localStorage.getItem("token")
        if (token) {
            this.client.connectHeaders = {
                Authorization: `Bearer ${token}`,
            }
        }

        this.client.onConnect = (frame: Frame) => {
            console.log("WebSocket connected:", frame)
            const userId = JSON.parse(atob(token!.split(".")[1])).userId
            this.client.subscribe(`/topic/notifications/${userId}`, onMessage)
        }

        this.client.onStompError = (frame) => {
            console.error("Broker error", frame)
        }

        this.client.activate()
    }

    disconnect() {
        this.client.deactivate()
    }
}

export const websocketService = new WebSocketService()
