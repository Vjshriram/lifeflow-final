<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hall of Fame | LifeFlow Leaderboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <!-- Premium Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700;800&family=Outfit:wght@400;600;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/assets/css/theme.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    
    <style>
        :root {
            --gold: #fbbf24;
            --silver: #cbd5e1;
            --bronze: #d97706;
            --podium-bg: rgba(225, 29, 72, 0.05);
        }

        body {
            font-family: 'Outfit', sans-serif;
            background-color: var(--bg-dark);
        }

        @keyframes rotate { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }

        .trophy-glow {
            width: 100px;
            height: 100px;
            background: linear-gradient(135deg, #fbbf24, #d97706);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 3rem;
            color: white;
            margin: 0 auto 2rem;
            box-shadow: 0 0 40px rgba(251, 191, 36, 0.4);
            position: relative;
        }
        
        .trophy-glow::after {
            content: '';
            position: absolute;
            inset: -10px;
            border: 3px dashed rgba(251, 191, 36, 0.5);
            border-radius: 50%;
            animation: rotate 10s linear infinite;
        }

        /* 🏆 Podium Section */
        .podium-section {
            padding: 8rem 0 4rem;
            background: radial-gradient(circle at center, rgba(225, 29, 72, 0.15) 0%, transparent 100%);
            overflow: hidden;
        }

        .podium-container {
            display: flex;
            align-items: flex-end;
            justify-content: center;
            gap: 2rem;
            margin-top: 4rem;
            min-height: 400px;
        }

        .podium-item {
            text-align: center;
            position: relative;
            transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
            width: 250px;
            opacity: 0;
            transform: translateY(30px);
        }
        
        .podium-item.show {
            opacity: 1;
            transform: translateY(0);
        }

        .podium-avatar {
            width: 120px;
            height: 120px;
            border-radius: 30px;
            margin: 0 auto 1.5rem;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 3rem;
            font-weight: 800;
            position: relative;
            background: var(--surface-dark);
            border: 4px solid var(--border-glass);
            box-shadow: 0 10px 30px rgba(0,0,0,0.5);
        }

        .rank-badge {
            position: absolute;
            bottom: -15px;
            left: 50%;
            transform: translateX(-50%);
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 800;
            font-size: 1.2rem;
            box-shadow: 0 4px 10px rgba(0,0,0,0.3);
        }

        /* 1st Place Specifics */
        .podium-1 { order: 2; z-index: 3; }
        .podium-1 .podium-avatar { 
            width: 160px; height: 160px; font-size: 4rem; 
            border-color: var(--gold);
            box-shadow: 0 0 50px rgba(251, 191, 36, 0.2);
        }
        .podium-1 .rank-badge { background: var(--gold); color: #451a03; }
        .podium-1 .pillar { height: 180px; background: linear-gradient(180deg, rgba(251, 191, 36, 0.1), transparent); }

        /* 2nd Place Specifics */
        .podium-2 { order: 1; z-index: 2; }
        .podium-2 .podium-avatar { border-color: var(--silver); }
        .podium-2 .rank-badge { background: var(--silver); color: #334155; }
        .podium-2 .pillar { height: 130px; background: linear-gradient(180deg, rgba(203, 213, 225, 0.1), transparent); }

        /* 3rd Place Specifics */
        .podium-3 { order: 3; z-index: 1; }
        .podium-3 .podium-avatar { border-color: var(--bronze); }
        .podium-3 .rank-badge { background: var(--bronze); color: white; }
        .podium-3 .pillar { height: 90px; background: linear-gradient(180deg, rgba(217, 119, 6, 0.1), transparent); }

        .pillar {
            border-radius: 20px 20px 0 0;
            width: 100%;
            border: 1px solid rgba(255, 255, 255, 0.05);
            border-bottom: none;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-direction: column;
            padding-top: 1rem;
        }

        /* 🏆 Table List Section */
        .leaderboard-list-section {
            max-width: 900px;
            margin: 0 auto 10rem;
        }

        .list-card {
            background: var(--surface-dark);
            border: var(--border-glass);
            border-radius: var(--radius-xl);
            padding: 1rem;
            box-shadow: var(--shadow-premium);
        }

        .list-row {
            padding: 1.25rem 1.5rem;
            display: flex;
            align-items: center;
            gap: 1.5rem;
            border-radius: var(--radius-lg);
            margin-bottom: 0.5rem;
            transition: all 0.3s ease;
            cursor: default;
        }

        .list-row:hover {
            background: rgba(255, 255, 255, 0.03);
            transform: scale(1.01);
        }

        .list-rank {
            width: 40px;
            font-weight: 800;
            font-size: 1.1rem;
            color: var(--text-secondary);
        }

        .list-avatar {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            background: rgba(225, 29, 72, 0.1);
            color: var(--primary-crimson);
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 700;
            font-size: 1.2rem;
            border: 1px solid rgba(225, 29, 72, 0.2);
        }

        .list-info { flex: 1; }
        
        .badge-pill {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            background: rgba(255, 255, 255, 0.05);
            padding: 0.2rem 0.8rem;
            border-radius: var(--radius-pill);
            font-size: 0.75rem;
            font-weight: 600;
            margin-top: 0.3rem;
            border: 1px solid rgba(255, 255, 255, 0.1);
        }

        .badge-pill.life-saver { color: var(--gold); border-color: rgba(251, 191, 36, 0.3); background: rgba(251, 191, 36, 0.05); }
        .badge-pill.top-donor { color: var(--silver); border-color: rgba(203, 213, 225, 0.3); }

        .list-count {
            text-align: right;
            font-weight: 800;
            color: var(--primary-crimson);
            font-size: 1.2rem;
        }

        @media (max-width: 768px) {
            .podium-container { flex-direction: column; align-items: center; min-height: auto; gap: 4rem; }
            .podium-item { order: unset !important; }
            .pillar { display: none; }
        }
    </style>
</head>
<body>

<%@ include file="/WEB-INF/fragments/header.jspf" %>

<section class="podium-section">
    <div class="container text-center">
        <div class="trophy-glow mx-auto mb-4">
            <i class="fa-solid fa-trophy"></i>
        </div>
        <h1 class="display-2 fw-900 text-white mb-2" style="font-family: 'Poppins', sans-serif; letter-spacing: -2px;">LifeFlow Hall of Fame</h1>
        <p class="text-secondary fs-5 mb-5 fw-600">Celebrating our most dedicated lifesavers.</p>
        
        <div class="podium-container" id="podiumContent">
            <!-- Dynamic Podium Content -->
             <div class="text-secondary">Loading the elite...</div>
        </div>
    </div>
</section>

<section class="leaderboard-list-section container">
    <div class="list-card">
        <div id="listContent">
            <!-- Dynamic List Content -->
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>

    // Use the secure server-side bridge instead of client-side Firebase
    async function loadLeaderboard() {
        console.log("🌐 Leaderboard: Initializing fetch to /api/leaderboard...");
        try {
            const response = await fetch('<%=request.getContextPath()%>/api/leaderboard');
            console.log("📡 Leaderboard: Server responded with status:", response.status);
            const data = await response.json();
            
            if (data.success) {
                renderLeaderboard(data.leaderboard);
            } else {
                console.error("Leaderboard Error:", data.message);
            }
        } catch (e) {
            console.error("Failed to fetch leaderboard:", e);
        }
    }

    // Initial load
    loadLeaderboard();
</script>

</body>
</html>

