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

export interface DealResponse {
    id: number;
    clientId: number;
    clientFullName: string;
    partnerId: number | null;
    partnerEmail: string | null;
    assignedManagerId: number | null;
    assignedManagerEmail: string | null;
    statusId: number;
    statusCode: string;
    statusName: string;
    title: string;
    propertyName: string | null;
    budget: number | null;
    amount: number | null;
    createdById: number;
    createdByEmail: string;
    createdAt: string;
    updatedAt: string;
    closedAt: string | null;
}

export interface ClientResponse {
    id: number;
    fullName: string;
    phone: string;
    email: string | null;
    source: string | null;
    partnerId: number | null;
    partnerEmail: string | null;
    assignedManagerId: number | null;
    assignedManagerEmail: string | null;
    createdById: number;
    createdByEmail: string;
    archived: boolean;
    createdAt: string;
    updatedAt: string;
}

export interface PartnerCommissionResponse {
    id: number;
    partnerId: number;
    partnerEmail: string;
    dealId: number;
    dealTitle: string;
    amount: number;
    percent: number;
    status: string;
    calculatedAt: string;
    approvedAt: string | null;
    paidAt: string | null;
}