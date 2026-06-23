import { http } from "./http";
import type { ClientResponse } from "../types";

export const clientApi = {
    async findAll(): Promise<ClientResponse[]> {
        const response = await http.get<ClientResponse[]>("/api/clients");
        return response.data;
    },
};