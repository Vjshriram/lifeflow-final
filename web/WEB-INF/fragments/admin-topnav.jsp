<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String active = (String) request.getAttribute("activePage");
    if (active == null) active = "dashboard";
    String rootPath = request.getContextPath();
%>
<!-- Glassmorphic Top Navigation -->
<nav class="admin-topnav-wrapper">
    <div class="container">
        <div class="admin-topnav glass-morphism d-flex align-items-center justify-content-between px-4 py-2">
            <!-- Brand -->
            <a href="<%= rootPath %>/index.jsp" class="brand-link text-decoration-none d-flex align-items-center gap-2">
                <i class="fa-solid fa-heart-pulse text-danger fs-4 pulse-glow"></i>
                <span class="text-white fs-5 fw-bold mb-0">Life<span class="text-danger">Flow</span></span>
                <span class="badge bg-danger bg-opacity-10 text-danger border border-danger border-opacity-25 ms-2" style="font-size: 0.6rem; letter-spacing: 1px;">ADMIN HQ</span>
            </a>

            <!-- Nav Links -->
            <div class="d-none d-lg-flex align-items-center gap-2 nav-links-container">
                <a href="<%= rootPath %>/dashboard/admin/home.jsp" class="nav-link-premium <%= "dashboard".equals(active) ? "active" : "" %>">
                    <div class="nav-icon-wrapper"><i class="fa-solid fa-border-all"></i></div>
                    <span>Dashboard</span>
                </a>
                <a href="<%= rootPath %>/dashboard/admin/analytics.jsp" class="nav-link-premium <%= "analytics".equals(active) ? "active" : "" %>">
                    <div class="nav-icon-wrapper"><i class="fa-solid fa-chart-line"></i></div>
                    <span>Intel</span>
                </a>
                <a href="<%= rootPath %>/dashboard/admin/campaigns.jsp" class="nav-link-premium <%= "campaigns".equals(active) ? "active" : "" %>">
                    <div class="nav-icon-wrapper"><i class="fa-solid fa-bullhorn"></i></div>
                    <span>Campaigns</span>
                </a>
                <a href="<%= rootPath %>/dashboard/admin/adminPendingApprovals.jsp" class="nav-link-premium <%= "approvals".equals(active) ? "active" : "" %>">
                    <div class="nav-icon-wrapper"><i class="fa-solid fa-user-check"></i></div>
                    <span>Approvals</span>
                </a>
                <a href="<%= rootPath %>/dashboard/admin/emergencyBroadcast.jsp" class="nav-link-premium <%= "emergency".equals(active) ? "active" : "" %>">
                    <div class="nav-icon-wrapper"><i class="fa-solid fa-tower-broadcast"></i></div>
                    <span>Emergency</span>
                </a>
            </div>
 
            <!-- Profile / Actions -->
            <div class="d-flex align-items-center gap-3">
                <div class="v-divider"></div>
                <a href="<%= rootPath %>/LogoutServlet" class="btn-logout-premium">
                    <i class="fa-solid fa-power-off"></i>
                    <span>Sign Out</span>
                </a>
            </div>
        </div>
    </div>
</nav>
 
<style>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;700&display=swap');

.admin-topnav-wrapper {
    position: fixed;
    top: 1.2rem;
    left: 0;
    right: 0;
    z-index: 1000;
    padding: 0 1rem;
    font-family: 'Outfit', sans-serif;
}

.admin-topnav {
    max-width: 1200px;
    margin: 0 auto;
    border-radius: 100px;
    background: rgba(10, 10, 15, 0.75);
    backdrop-filter: blur(20px) saturate(180%);
    -webkit-backdrop-filter: blur(20px) saturate(180%);
    border: 1px solid rgba(255, 255, 255, 0.12);
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.4), 
                inset 0 0 20px rgba(255, 255, 255, 0.02);
    padding: 0.6rem 1.5rem !important;
}

.brand-link { transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
.brand-link:hover { transform: translateY(-1px) scale(1.02); }

.pulse-glow {
    filter: drop-shadow(0 0 5px rgba(225, 29, 72, 0.6));
    animation: pulse-red 2s infinite ease-in-out;
}

@keyframes pulse-red {
    0%, 100% { opacity: 1; transform: scale(1); }
    50% { opacity: 0.8; transform: scale(1.1); filter: drop-shadow(0 0 8px rgba(225, 29, 72, 0.9)); }
}

.nav-link-premium {
    color: rgba(255, 255, 255, 0.6);
    font-size: 0.85rem;
    font-weight: 500;
    text-decoration: none !important;
    padding: 0.5rem 1.2rem;
    border-radius: 100px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    align-items: center;
    gap: 8px;
    position: relative;
    border: 1px solid transparent;
}

.nav-icon-wrapper {
    font-size: 0.95rem;
    transition: transform 0.3s ease;
}

.nav-link-premium:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.06);
    transform: translateY(-1px);
}

.nav-link-premium:hover .nav-icon-wrapper {
    transform: scale(1.2);
}

.nav-link-premium.active {
    background: linear-gradient(135deg, rgba(225, 29, 72, 0.15) 0%, rgba(225, 29, 72, 0.05) 100%);
    color: #ff4d6d;
    font-weight: 700;
    border: 1px solid rgba(225, 29, 72, 0.3);
    box-shadow: 0 4px 15px rgba(225, 29, 72, 0.1);
}

.nav-link-premium.active .nav-icon-wrapper {
    filter: drop-shadow(0 0 5px rgba(225, 29, 72, 0.4));
}

.v-divider {
    width: 1px;
    height: 20px;
    background: linear-gradient(to bottom, transparent, rgba(255,255,255,0.2), transparent);
}

.btn-logout-premium {
    background: rgba(225, 29, 72, 0.1);
    color: #ff4d6d;
    border: 1px solid rgba(225, 29, 72, 0.3);
    border-radius: 100px;
    padding: 0.5rem 1.2rem;
    font-size: 0.8rem;
    font-weight: 700;
    text-decoration: none !important;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.3s ease;
}

.btn-logout-premium:hover {
    background: #e11d48;
    color: #fff;
    box-shadow: 0 0 20px rgba(225, 29, 72, 0.4);
    transform: scale(1.05);
}

.badge-admin {
    background: rgba(225, 29, 72, 0.1);
    color: #ff4d6d;
    border: 1px solid rgba(225, 29, 72, 0.2);
    font-size: 0.6rem !important;
    padding: 0.2rem 0.6rem !important;
    border-radius: 4px;
    letter-spacing: 1px;
    font-weight: 800;
    box-shadow: inset 0 0 10px rgba(225, 29, 72, 0.05);
}

/* Ticker Upgrades */
.ticker-wrapper { 
    background: rgba(10, 10, 15, 0.4); 
    border-bottom: 1px solid rgba(255, 255, 255, 0.05); 
    backdrop-filter: blur(10px);
    overflow: hidden; 
    padding: 0.8rem 0; 
    margin-bottom: 3rem; 
}
.ticker-item { 
    color: rgba(255, 255, 255, 0.6); 
    font-family: 'Outfit', sans-serif;
    font-size: 0.75rem;
}
.ticker-item i { color: #e11d48; }

.admin-view { padding-top: 8rem; min-height: 100vh; background: #050507; }
</style>
</style>

<script>
// Prevent flash of invisible content
document.documentElement.classList.add('js-enabled');

document.addEventListener('DOMContentLoaded', function() {
    const observerOptions = { threshold: 0.1 };
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('scroll-active');
            }
        });
    }, observerOptions);

    document.querySelectorAll('.fade-in-up').forEach(el => observer.observe(el));
});
</script>

<!-- Scroll Reveal & Interaction Engine -->
<script>
document.addEventListener('DOMContentLoaded', function() {
    // 1. Scroll Reveal Logic
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('scroll-active');
                // Optional: stop observing once revealed
                // observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    document.querySelectorAll('.fade-in-up').forEach(el => {
        observer.observe(el);
    });

    // 2. Active Link Guard (fallback)
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-link-premium').forEach(link => {
        if (link.getAttribute('href') && currentPath.includes(link.getAttribute('href'))) {
            // link.classList.add('active');
        }
    });
});
</script>
