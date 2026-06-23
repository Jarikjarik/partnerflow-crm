import { http } from "./http";
import type { PartnerCommissionResponse } from "../types";

export const billingApi = {
    async findCommissions(): Promise<PartnerCommissionResponse[]> {
        const response = await http.get<PartnerCommissionResponse[]>("/api/billing/commissions");
        return response.data;
    },
};