# SkillSwap — Campus Peer Learning & Skill Barter Platform

A modern peer-to-peer knowledge exchange platform built with pure **HTML5, CSS3, and Vanilla JavaScript** on the frontend, and an enterprise **Java Object-Oriented Architecture (OOP)** backend with **MySQL Database** integration.

---

## 🌟 Key Features

### 1. User Management & LinkedIn-Style Profiles
* **Secure Authentication**: SHA-256 password hashing with cryptographically secure salts.
* **LinkedIn-Style Profile**: Cover banner, avatar halo, headline, bio, university, and education timeline.
* **GitHub Integration**: Import real public repositories with stars, descriptions, and language badges.
* **Skill Passport**: Public shareable credential showcasing verified badges, tier, sessions taught, and student ratings.

### 2. Smart Matching Algorithms (OOP Strategy Pattern)
* **Direct Swap Matchmaker**: Identifies reciprocal 2-way swaps where User A teaches what User B wants, and User B teaches what User A wants.
* **3-Way Chain Matching (A ➔ B ➔ C ➔ A)**: Solves barter deadlocks using directed graph cycle detection when direct reciprocal matches are impossible.
* **Match Score**: Evaluates compatibility based on skill level alignment, mentor rating, and campus streak activity.

### 3. Credit Economy & Escrow System
* **Welcome Bonus**: 100 free credits on registration.
* **Escrow Guarantee**: Credits are safely held in escrow when booking a session and released only when marked completed.
* **Simulated College Payment Gateway**: 1-click test checkout with UPI, Card, and NetBanking options.
* **Transparent Credit Ledger**: Audit log tracking every credit change with timestamps and reference IDs.
* **Pay It Forward Pool**: Community donation fund where learners can donate spare credits or claim assistance grants.

### 4. Verification & Tier Formula
* **Interactive Skill Tests (MCQ)**: Instant grading across Java, Python, React, DSA, SQL, and System Design. Scoring ≥ 70% awards verified badges and a 20-credit bonus!
* **Certificate Verification Workflow**: Students submit proofs; campus admins review and approve/reject credentials.
* **Formula-Based Tiers**:
  * **Beginner**: Standard (10 credits/session)
  * **Intermediate**: ≥ 1 verified skill/cert, rating ≥ 4.0, completed ≥ 3 sessions (25 credits/session)
  * **Pro**: ≥ 3 verified skills/certs, rating ≥ 4.5, completed ≥ 8 sessions (50 credits/session)

### 5. Sessions & Built-In Video Room
* **6 Polymorphic Session Types**:
  1. `Direct Swap`: 1-hour reciprocal exchange
  2. `Doubt Sprint`: 15-minute rapid unblocking (1-2 credits) with code error snippet & urgency tags
  3. `Mock Interview`: 60-minute technical/behavioral practice with rubric
  4. `Code Review`: 45-minute GitHub PR & architectural review
  5. `Group Class`: Masterclasses with multi-learner seat capacity
  6. `Build Together`: Collaborative project sprints linked to GitHub
* **Live Video Room**: Integrated encrypted WebRTC video exchange studio with live timer.
* **Ratings & Reviews**: 1-5 star reviews recalculate teacher ratings and tiers in real time.

### 6. Campus Community & Social
* **Direct Messaging**: Chat system with active peer threads.
* **Hackathon Team Finder**: Browse and create teams with specific role requirements (e.g. ML engineer, Frontend dev).
* **Campus Leaderboard**: Ranks top mentors by credits earned, streaks, and badges.
* **Knowledge Base**: Community Q&A with upvoting and verified answers.
* **Learning Roadmaps**: Structured career roadmaps (Full-Stack in 3 Months, ML Engineer) with 1-click mentor discovery.
* **Admin Verification Panel**: Review pending certificates, toggle user suspensions, and inspect MySQL telemetry.

---

## 🏗️ Architecture & OOP Design Patterns

```
com.skillswap/
├── Main.java                        # Server entry point (HttpServer on port 8080)
├── model/                           # Domain Entities
│   ├── User.java                    # Encapsulated state & tier recalculation logic
│   ├── Education.java / Project.java / Certificate.java
│   ├── Skill.java
│   ├── Session.java                 # Abstract Base Class
│   │   ├── DirectSwapSession.java   # Polymorphic session implementations
│   │   ├── DoubtSprintSession.java
│   │   ├── MockInterviewSession.java
│   │   ├── CodeReviewSession.java
│   │   ├── GroupClassSession.java
│   │   └── BuildTogetherSession.java
│   ├── CreditTransaction.java
│   ├── Review.java / Message.java / Notification.java
│   ├── HackathonTeam.java / QuizQuestion.java / Roadmap.java
│   └── PayItForwardPool.java
├── service/                         # Business Logic Layer
│   ├── AuthService.java             # SHA-256 + salt security
│   ├── UserService.java             # Profiles, GitHub repo import
│   ├── SkillService.java            # Quizzes, verification, teacher ranking
│   ├── MatchingService.java         # Direct & Chain matching orchestrator
│   ├── CreditService.java           # Escrow management & ledger logs
│   ├── SessionService.java          # Lifecycle state machine & video links
│   ├── CommunityService.java        # Messaging, Hackathons, Leaderboard
│   └── AdminService.java            # Approvals, user bans, analytics
│   └── matching/                    # Strategy Pattern
│       ├── MatchingStrategy.java    # Strategy Interface
│       ├── DirectSwapStrategy.java  # 2-way reciprocal matching
│       ├── ChainMatchingStrategy.java # 3-way circular graph cycle detection
│       └── MatchResult.java
├── repository/                      # Data Access Layer
│   ├── DataRepository.java          # Interface
│   ├── DatabaseManager.java         # JDBC Connection Pool & schema runner
│   └── HybridDataRepository.java    # MySQL write-through + JSON snapshot persistence
├── controller/                      # Web & API Handlers
│   ├── ApiController.java           # RESTful JSON endpoint router
│   └── StaticFileHandler.java       # High-performance static asset server
└── util/
    ├── SimpleJson.java              # Pure Java zero-dependency JSON parser & serializer
    ├── SecurityUtils.java           # SHA-256 hashing & token generator
    └── HttpUtils.java               # CORS & HTTP request helpers
```

---

## 🚀 How to Run

### Step 1: Compile the Java Backend
Run the compiler script:
```powershell
powershell -ExecutionPolicy Bypass -File compile.ps1
```

### Step 2: Start the Server
Run the startup script:
```cmd
run.bat
```
or in PowerShell:
```powershell
powershell -ExecutionPolicy Bypass -File run.ps1
```

### Step 3: Open in Browser
Visit:
```
http://localhost:8080
```

---

## 🗄️ MySQL Database Setup (Optional & Configurable)

1. Open `config.properties`:
```properties
db.enabled=true
db.host=localhost
db.port=3306
db.name=skillswap
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```
2. The server will automatically connect using the included `lib/mysql-connector-j-9.2.0.jar` driver and execute `database/schema.sql`.
3. *Note*: If MySQL is currently offline or password is not yet configured, the system seamlessly operates in **Hybrid Persistence Mode** using `data/skillswap_datastore.json` without any crashes, and allows you to test/reconnect MySQL anytime from the Admin panel!

---

## ⚡ Demo Accounts for Instant Evaluation

Use the **⚡ Quick Demo Accounts** button on the top-right of the web app or sign in manually:

| Name | Role / Specialty | Email | Password | Tier |
|---|---|---|---|---|
| **Priya Sharma** | React 19 / Frontend Architect | `priya@skillswap.edu` | `priya123` | Pro (240 CR) |
| **Rahul Verma** | PyTorch / Machine Learning | `rahul@skillswap.edu` | `rahul123` | Intermediate (180 CR) |
| **Ananya Patel** | Java / DSA / Spring Boot | `ananya@skillswap.edu` | `ananya123` | Intermediate (150 CR) |
| **Campus Admin** | Dean of Studies (Admin Panel) | `admin@skillswap.edu` | `admin123` | Pro Admin |

*(Priya, Rahul, and Ananya form a verified 3-Way Chain Swap Cycle: Priya teaches React to Rahul, Rahul teaches ML to Ananya, and Ananya teaches Java to Priya!)*

