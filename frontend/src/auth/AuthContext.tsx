/* eslint-disable react-refresh/only-export-components */

import { createContext, useContext, useEffect, useState } from "react";
import type { ReactNode } from "react";
import { authApi } from "../api/authApi";
import type { CurrentUserResponse } from "../types";

interface AuthContextValue {
    user: CurrentUserResponse | null;
    initialized: boolean;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
    const [user, setUser] = useState<CurrentUserResponse | null>(null);
    const [initialized, setInitialized] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("accessToken");

        if (!token) {
            setInitialized(true);
            return;
        }

        authApi
            .me()
            .then(setUser)
            .catch(() => {
                localStorage.removeItem("accessToken");
                setUser(null);
            })
            .finally(() => setInitialized(true));
    }, []);

    async function login(email: string, password: string) {
        const response = await authApi.login({ email, password });

        localStorage.setItem("accessToken", response.accessToken);

        setUser({
            id: response.userId,
            email: response.email,
            fullName: response.fullName,
            role: response.role,
        });
    }

    function logout() {
        localStorage.removeItem("accessToken");
        setUser(null);
    }

    return (
        <AuthContext.Provider value={{ user, initialized, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth must be used inside AuthProvider");
    }

    return context;
}