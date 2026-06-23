import { http } from "./http";
import type { AuthResponse, CurrentUserResponse } from "../types";

export interface LoginRequest {
    email: string;
    password: string;
}

export const authApi = {
    async login(request: LoginRequest): Promise<AuthResponse> {
        const response = await http.post<AuthResponse>("/api/auth/login", request);
        return response.data;
    },

    async me(): Promise<CurrentUserResponse> {
        const response = await http.get<CurrentUserResponse>("/api/auth/me");
        return response.data;
    },
};