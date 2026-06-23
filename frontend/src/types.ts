export type UserRole = "ADMIN" | "MANAGER" | "PARTNER";

export interface AuthResponse {
    accessToken: string;
    tokenType: string;
    userId: number;
    email: string;
    fullName: string;
    role: UserRole;
}

export interface CurrentUserResponse {
    id: number;
    email: string;
    fullName: string;
    role: UserRole;
}

export interface AnalyticsSummaryResponse {
    totalClients: number;
    totalDeals: number;
    wonDeals: number;
    lostDeals: number;
    totalDealAmount: number;
    wonDealAmount: number;
    calculatedCommissionAmount: number;
}