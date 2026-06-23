import { useState } from "react";
import type { FormEvent } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function LoginPage() {
    const { user, login } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("admin@partnerflow.local");
    const [password, setPassword] = useState("admin123");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    if (user) {
        return <Navigate to="/" replace />;
    }

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        setError(null);
        setLoading(true);

        try {
            await login(email, password);
            navigate("/");
        } catch {
            setError("Invalid email or password");
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="login-page">
            <form className="login-card" onSubmit={handleSubmit}>
                <div className="login-title">
                    <div className="brand-logo">PF</div>
                    <div>
                        <h1>PartnerFlow CRM</h1>
                        <p>Sign in to continue</p>
                    </div>
                </div>

                <label>
                    Email
                    <input
                        value={email}
                        onChange={(event) => setEmail(event.target.value)}
                        type="email"
                        autoComplete="email"
                    />
                </label>

                <label>
                    Password
                    <input
                        value={password}
                        onChange={(event) => setPassword(event.target.value)}
                        type="password"
                        autoComplete="current-password"
                    />
                </label>

                {error && <div className="error-box">{error}</div>}

                <button type="submit" disabled={loading}>
                    {loading ? "Signing in..." : "Sign in"}
                </button>

                <div className="demo-accounts">
                    <strong>Demo accounts</strong>
                    <span>ADMIN: admin@partnerflow.local / admin123</span>
                    <span>PARTNER: partner@example.com / password123</span>
                </div>
            </form>
        </div>
    );
}