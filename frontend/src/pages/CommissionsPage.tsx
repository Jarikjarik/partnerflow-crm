import { useEffect, useState } from "react";
import { billingApi } from "../api/billingApi";
import type { PartnerCommissionResponse } from "../types";
import { useAuth } from "../auth/AuthContext";

function formatMoney(value: number) {
    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        maximumFractionDigits: 0,
    }).format(value);
}

function formatPercent(value: number) {
    return `${value}%`;
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

function getCommissionStatusClass(status: string) {
    const normalized = status.toLowerCase();

    if (normalized === "paid") {
        return "status-badge status-won";
    }

    if (normalized === "cancelled") {
        return "status-badge status-lost";
    }

    return "status-badge";
}

export function CommissionsPage() {
    const { user } = useAuth();
    const [commissions, setCommissions] = useState<PartnerCommissionResponse[]>([]);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        billingApi
            .findCommissions()
            .then(setCommissions)
            .catch(() => setError("Failed to load commissions"))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Commissions</h1>
                    <p>
                        {user?.role === "PARTNER"
                            ? "Your calculated partner commissions."
                            : "Calculated commissions across all partners."}
                    </p>
                </div>
            </div>

            {error && <div className="error-box">{error}</div>}

            {loading && <div className="panel">Loading commissions...</div>}

            {!loading && !error && (
                <div className="table-card">
                    <table>
                        <thead>
                        <tr>
                            <th>Deal</th>
                            <th>Partner</th>
                            <th>Amount</th>
                            <th>Percent</th>
                            <th>Status</th>
                            <th>Calculated</th>
                        </tr>
                        </thead>

                        <tbody>
                        {commissions.map((commission) => (
                            <tr key={commission.id}>
                                <td>
                                    <strong>{commission.dealTitle}</strong>
                                    <span>Deal ID: {commission.dealId}</span>
                                </td>
                                <td>{commission.partnerEmail}</td>
                                <td>{formatMoney(commission.amount)}</td>
                                <td>{formatPercent(commission.percent)}</td>
                                <td>
                    <span className={getCommissionStatusClass(commission.status)}>
                      {commission.status}
                    </span>
                                </td>
                                <td>{formatDate(commission.calculatedAt)}</td>
                            </tr>
                        ))}

                        {commissions.length === 0 && (
                            <tr>
                                <td colSpan={6} className="empty-cell">
                                    No commissions found.
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