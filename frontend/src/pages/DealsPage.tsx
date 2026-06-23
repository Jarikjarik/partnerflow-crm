import { useEffect, useState } from "react";
import { dealApi } from "../api/dealApi";
import type { DealResponse } from "../types";
import { useAuth } from "../auth/AuthContext";

function formatMoney(value: number | null) {
    if (value === null) {
        return "—";
    }

    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        maximumFractionDigits: 0,
    }).format(value);
}

function formatDate(value: string | null) {
    if (!value) {
        return "—";
    }

    return new Intl.DateTimeFormat("en-US", {
        year: "numeric",
        month: "short",
        day: "2-digit",
    }).format(new Date(value));
}

function getStatusClass(statusCode: string) {
    const normalized = statusCode.toLowerCase();

    if (normalized === "won") {
        return "status-badge status-won";
    }

    if (normalized === "lost") {
        return "status-badge status-lost";
    }

    return "status-badge";
}

export function DealsPage() {
    const { user } = useAuth();
    const [deals, setDeals] = useState<DealResponse[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        dealApi
            .findAll()
            .then(setDeals)
            .catch(() => setError("Failed to load deals"))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Deals</h1>
                    <p>
                        {user?.role === "PARTNER"
                            ? "Deals linked to your referred clients."
                            : "All deals across the CRM pipeline."}
                    </p>
                </div>
            </div>

            {error && <div className="error-box">{error}</div>}

            {loading && <div className="panel">Loading deals...</div>}

            {!loading && !error && (
                <div className="table-card">
                    <table>
                        <thead>
                        <tr>
                            <th>Deal</th>
                            <th>Client</th>
                            <th>Status</th>
                            <th>Amount</th>
                            <th>Partner</th>
                            <th>Closed</th>
                        </tr>
                        </thead>

                        <tbody>
                        {deals.map((deal) => (
                            <tr key={deal.id}>
                                <td>
                                    <strong>{deal.title}</strong>
                                    <span>{deal.propertyName ?? `ID: ${deal.id}`}</span>
                                </td>
                                <td>{deal.clientFullName}</td>
                                <td>
                    <span className={getStatusClass(deal.statusCode)}>
                      {deal.statusCode}
                    </span>
                                </td>
                                <td>{formatMoney(deal.amount)}</td>
                                <td>{deal.partnerEmail ?? "—"}</td>
                                <td>{formatDate(deal.closedAt)}</td>
                            </tr>
                        ))}

                        {deals.length === 0 && (
                            <tr>
                                <td colSpan={6} className="empty-cell">
                                    No deals found.
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