import { AxiosError } from "axios"
import { enqueueSnackbar } from "notistack"

export function toastAxiosError(err: AxiosError) {
    const message = (err.response?.data as any)?.message ?? err.response?.statusText ?? "Something went wrong"
    enqueueSnackbar(message, { variant: "error" })
}
