import { OpenAPI } from "@/api/generated"

OpenAPI.BASE = import.meta.env.VITE_BACKEND_URL
OpenAPI.TOKEN = async () => localStorage.getItem("token") ?? ""
OpenAPI.HEADERS = { "Content-Type": "application/json" }

export * from "@/api/generated"
