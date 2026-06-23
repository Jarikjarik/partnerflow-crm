import { http } from "./http";
import type { AnalyticsSummaryResponse } from "../types";

export const analyticsApi = {
    async getSummary(): Promise<AnalyticsSummaryResponse> {
        const response = await http.get<AnalyticsSummaryResponse>("/api/analytics/summary");
        return response.data;
    },
};