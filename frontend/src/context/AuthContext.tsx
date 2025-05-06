import React, { createContext, useContext, useState, useEffect, PropsWithChildren } from "react"
import { jwtDecode } from "jwt-decode"

interface RawJwtPayload {
    sub: string // email
    roles: string[] // ["ROLE_USER", "ROLE_ADMIN", "ROLE_AUTHOR"]
    userId?: number
    username?: string
    authorId?: number
    fullName?: string
    exp: number
}

export type Role = "USER" | "AUTHOR" | "ADMIN"

export interface AppUser {
    email: string
    roles: Role[]
    id: number // userId | authorId
    displayName: string // username | fullName
    exp: number
}

interface AuthCtx {
    user: AppUser | null
    login: (token: string) => void
    logout: () => void
}

const AuthContext = createContext<AuthCtx>({} as AuthCtx)
// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext)

// eslint-disable-next-line @typescript-eslint/no-empty-object-type
export const AuthProvider: React.FC<PropsWithChildren<{}>> = ({ children }) => {
    const [user, setUser] = useState<AppUser | null>(null)

    const parseToken = (token: string): AppUser => {
        const raw = jwtDecode<RawJwtPayload>(token)

        return {
            email: raw.sub,
            roles: raw.roles.map((r) => r.replace("ROLE_", "") as Role),
            id: raw.userId ?? raw.authorId!,
            displayName: raw.username ?? raw.fullName!,
            exp: raw.exp,
        }
    }

    const login = (token: string) => {
        localStorage.setItem("token", token)
        setUser(parseToken(token))
    }

    const logout = () => {
        localStorage.removeItem("token")
        setUser(null)
    }

    useEffect(() => {
        const stored = localStorage.getItem("token")
        if (stored) setUser(parseToken(stored))
    }, [])

    /* ---- auto-logout at token expiry ---- */
    useEffect(() => {
        if (!user) return

        const msLeft = user.exp * 1000 - Date.now()
        if (msLeft <= 0) {
            logout()
            return
        }

        const to = setTimeout(logout, msLeft)
        return () => clearTimeout(to)
    }, [user])

    return <AuthContext.Provider value={{ user, login, logout }}>{children}</AuthContext.Provider>
}
