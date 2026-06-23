import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export function AppLayout() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    function handleLogout() {
        logout();
        navigate("/login");
    }

    return (
        <div className="app-shell">
            <aside className="sidebar">
                <div className="brand">
                    <div className="brand-logo">PF</div>
                    <div>
                        <strong>PartnerFlow</strong>
                        <span>CRM</span>
                    </div>
                </div>

                <nav className="nav">
                    <NavLink to="/" end>
                        Dashboard
                    </NavLink>

                    <NavLink to="/clients">
                        Clients
                    </NavLink>

                    <NavLink to="/deals">
                        Deals
                    </NavLink>
                </nav>

                <div className="sidebar-user">
                    <div>{user?.fullName}</div>
                    <span>{user?.role}</span>
                    <button onClick={handleLogout}>Logout</button>
                </div>
            </aside>

            <main className="main">
                <Outlet />
            </main>
        </div>
    );
}