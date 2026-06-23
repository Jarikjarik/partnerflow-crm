import { http } from "./http";
import type { DealResponse } from "../types";

export const dealApi = {
    async findAll(): Promise<DealResponse[]> {
        const response = await http.get<DealResponse[]>("/api/deals");
        return response.data;
    },
};