import React, { createContext, useState, useEffect } from "react"

interface AuthContextProps {
    token: string | null
    isLoggedIn: boolean
    login: (token: string) => void
    logout: () => void
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextProps>({
    token: null,
    isLoggedIn: false,
    login: () => {},
    logout: () => {},
})

interface AuthProviderProps {
    children: React.ReactNode
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [token, setToken] = useState<string | null>(() => {
        return localStorage.getItem("jwtToken")
    })

    const isLoggedIn = !!token

    useEffect(() => {
        if (token) {
            localStorage.setItem("jwtToken", token)
        } else {
            localStorage.removeItem("jwtToken")
        }
    }, [token])

    const login = (newToken: string) => {
        setToken(newToken)
    }

    const logout = () => {
        setToken(null)
    }

    return <AuthContext.Provider value={{ token, isLoggedIn, login, logout }}>{children}</AuthContext.Provider>
}
