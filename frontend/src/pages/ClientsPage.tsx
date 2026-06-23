import { useEffect, useState } from "react";
import { clientApi } from "../api/clientApi";
import type { ClientResponse } from "../types";
import { useAuth } from "../auth/AuthContext";

function formatDate(value: string) {
    return new Intl.DateTimeFormat("en-US", {
        year: "numeric",
        month: "short",
        day: "2-digit",
    }).format(new Date(value));
}

export function ClientsPage() {
    const { user } = useAuth();
    const [clients, setClients] = useState<ClientResponse[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        clientApi
            .findAll()
            .then(setClients)
            .catch(() => setError("Failed to load clients"))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Clients</h1>
                    <p>
                        {user?.role === "PARTNER"
                            ? "Your referred clients."
                            : "All active clients in the CRM."}
                    </p>
                </div>
            </div>

            {error && <div className="error-box">{error}</div>}

            {loading && <div className="panel">Loading clients...</div>}

            {!loading && !error && (
                <div className="table-card">
                    <table>
                        <thead>
                        <tr>
                            <th>Client</th>
                            <th>Phone</th>
                            <th>Email</th>
                            <th>Source</th>
                            <th>Partner</th>
                            <th>Created</th>
                        </tr>
                        </thead>

                        <tbody>
                        {clients.map((client) => (
                            <tr key={client.id}>
                                <td>
                                    <strong>{client.fullName}</strong>
                                    <span>ID: {client.id}</span>
                                </td>
                                <td>{client.phone}</td>
                                <td>{client.email ?? "—"}</td>
                                <td>{client.source ?? "—"}</td>
                                <td>{client.partnerEmail ?? "—"}</td>
                                <td>{formatDate(client.createdAt)}</td>
                            </tr>
                        ))}

                        {clients.length === 0 && (
                            <tr>
                                <td colSpan={6} className="empty-cell">
                                    No clients found.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}