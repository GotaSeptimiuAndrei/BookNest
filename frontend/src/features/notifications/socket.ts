import { Client } from "@stomp/stompjs"
import SockJS from "sockjs-client"

let client: Client | null = null

export const getSocket = () => {
    if (client) return client

    client = new Client({
        webSocketFactory: () => new SockJS(import.meta.env.VITE_BACKEND_URL + "/ws"),

        reconnectDelay: 5_000,
        connectHeaders: {
            Authorization: localStorage.getItem("token") ?? "",
        },
        debug: console.log,
    })

    client.activate()
    return client
}
