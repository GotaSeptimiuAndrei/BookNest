import axios, { AxiosError } from "axios"
import { toastAxiosError } from "./axiosErrorToToast"

axios.defaults.baseURL = import.meta.env.VITE_BACKEND_URL
axios.defaults.headers.common["Content-Type"] = "application/json"

// attach JWT on every request
axios.interceptors.request.use((cfg) => {
    const token = localStorage.getItem("token")
    if (token) cfg.headers!["Authorization"] = `Bearer ${token}`
    return cfg
})

// global error handler -> toast
axios.interceptors.response.use(
    (res) => res,
    (err: AxiosError) => {
        if (err.response?.status === 401) {
            localStorage.removeItem("token")
            window.location.replace("/login")
        }
        toastAxiosError(err)
        return Promise.reject(err)
    }
)

export default axios
