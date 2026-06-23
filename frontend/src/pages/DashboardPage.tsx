import { useEffect, useState } from "react";
import { analyticsApi } from "../api/analyticsApi";
import type { AnalyticsSummaryResponse } from "../types";
import { useAuth } from "../auth/AuthContext";

function formatMoney(value: number) {
    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        maximumFractionDigits: 0,
    }).format(value);
}

export function DashboardPage() {
    const { user } = useAuth();
    const [summary, setSummary] = useState<AnalyticsSummaryResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        analyticsApi
            .getSummary()
            .then(setSummary)
            .catch(() => setError("Failed to load analytics summary"));
    }, []);

    return (
        <div className="page">
            <div className="page-header">
                <div>
                    <h1>Dashboard</h1>
                    <p>
                        Welcome back, <strong>{user?.fullName}</strong>. Current role:{" "}
                        <strong>{user?.role}</strong>
                    </p>
                </div>
            </div>

            {error && <div className="error-box">{error}</div>}

            {!summary && !error && <div className="panel">Loading analytics...</div>}

            {summary && (
                <div className="cards-grid">
                    <div className="metric-card">
                        <span>Total clients</span>
                        <strong>{summary.totalClients}</strong>
                    </div>

                    <div className="metric-card">
                        <span>Total deals</span>
                        <strong>{summary.totalDeals}</strong>
                    </div>

                    <div className="metric-card">
                        <span>Won deals</span>
                        <strong>{summary.wonDeals}</strong>
                    </div>

                    <div className="metric-card">
                        <span>Lost deals</span>
                        <strong>{summary.lostDeals}</strong>
                    </div>

                    <div className="metric-card wide">
                        <span>Total deal amount</span>
                        <strong>{formatMoney(summary.totalDealAmount)}</strong>
                    </div>

                    <div className="metric-card wide">
                        <span>Won deal amount</span>
                        <strong>{formatMoney(summary.wonDealAmount)}</strong>
                    </div>

                    <div className="metric-card wide accent">
                        <span>Calculated commissions</span>
                        <strong>{formatMoney(summary.calculatedCommissionAmount)}</strong>
                    </div>
                </div>
            )}
        </div>
    );
}