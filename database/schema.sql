-- SkillSwap Database Schema (MySQL)
CREATE DATABASE IF NOT EXISTS skillswap CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE skillswap;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(191) NOT NULL UNIQUE,
    password_hash VARCHAR(256) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    avatar_url VARCHAR(500),
    headline VARCHAR(255),
    bio TEXT,
    college VARCHAR(255),
    linkedin_url VARCHAR(500),
    github_username VARCHAR(100),
    tier VARCHAR(20) DEFAULT 'Beginner', -- Beginner, Intermediate, Pro
    tier_price INT DEFAULT 10,
    credits INT DEFAULT 100,
    escrow_credits INT DEFAULT 0,
    streak_days INT DEFAULT 1,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role VARCHAR(20) DEFAULT 'USER', -- USER, ADMIN
    is_banned BOOLEAN DEFAULT FALSE,
    available_formats VARCHAR(255) DEFAULT 'One-to-One, Group Session',
    available_time_slots VARCHAR(255) DEFAULT 'Evenings & Weekends',
    mentor_intro TEXT,
    expertise_level VARCHAR(50) DEFAULT 'Intermediate',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Education Table
CREATE TABLE IF NOT EXISTS education (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    school VARCHAR(255) NOT NULL,
    degree VARCHAR(150),
    field_of_study VARCHAR(150),
    start_year INT,
    end_year INT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Projects Table
CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    github_repo_url VARCHAR(500),
    live_url VARCHAR(500),
    tags VARCHAR(255),
    stars INT DEFAULT 0,
    is_imported BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4. Certificates & Proofs Table
CREATE TABLE IF NOT EXISTS certificates (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    issuing_org VARCHAR(255),
    issue_date VARCHAR(50),
    credential_url VARCHAR(500),
    proof_description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. User Skills Table
CREATE TABLE IF NOT EXISTS user_skills (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    skill_name VARCHAR(100) NOT NULL,
    skill_type VARCHAR(20) NOT NULL, -- TEACH or LEARN
    proficiency_level VARCHAR(20) NOT NULL, -- Beginner, Intermediate, Advanced, Expert
    is_verified BOOLEAN DEFAULT FALSE,
    quiz_score INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Sessions Table
CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(64) PRIMARY KEY,
    session_type VARCHAR(30) NOT NULL, -- DIRECT_SWAP, DOUBT_SPRINT, MOCK_INTERVIEW, CODE_REVIEW, GROUP_CLASS, BUILD_TOGETHER
    teacher_id VARCHAR(64) NOT NULL,
    learner_id VARCHAR(64) NOT NULL,
    skill_topic VARCHAR(150) NOT NULL,
    description TEXT,
    scheduled_time VARCHAR(100),
    duration_minutes INT DEFAULT 60,
    credit_amount INT DEFAULT 10,
    status VARCHAR(20) DEFAULT 'REQUESTED', -- REQUESTED, ACCEPTED, REJECTED, CANCELLED, COMPLETED
    meeting_link VARCHAR(500),
    github_pr_url VARCHAR(500),
    total_seats INT DEFAULT 1,
    booked_seats INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. Credit Ledger Table
CREATE TABLE IF NOT EXISTS credit_ledger (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    transaction_type VARCHAR(40) NOT NULL, -- SIGNUP_BONUS, PURCHASE, ESCROW_HOLD, ESCROW_RELEASE, ESCROW_REFUND, DONATION, POOL_GRANT
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    description VARCHAR(255),
    reference_id VARCHAR(64),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8. Reviews Table
CREATE TABLE IF NOT EXISTS reviews (
    id VARCHAR(64) PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    teacher_id VARCHAR(64) NOT NULL,
    learner_id VARCHAR(64) NOT NULL,
    rating INT NOT NULL, -- 1 to 5
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 9. Direct Messages Table
CREATE TABLE IF NOT EXISTS messages (
    id VARCHAR(64) PRIMARY KEY,
    sender_id VARCHAR(64) NOT NULL,
    receiver_id VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    action_url VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 11. Hackathon Teams Table
CREATE TABLE IF NOT EXISTS hackathon_teams (
    id VARCHAR(64) PRIMARY KEY,
    organizer_id VARCHAR(64) NOT NULL,
    title VARCHAR(200) NOT NULL,
    hackathon_name VARCHAR(200) NOT NULL,
    description TEXT,
    looking_for_skills VARCHAR(255),
    max_members INT DEFAULT 4,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 12. Hackathon Team Members Table
CREATE TABLE IF NOT EXISTS hackathon_members (
    id VARCHAR(64) PRIMARY KEY,
    team_id VARCHAR(64) NOT NULL,
    user_id VARCHAR(64) NOT NULL,
    role_name VARCHAR(100),
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES hackathon_teams(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 13. Knowledge Base Posts Table
CREATE TABLE IF NOT EXISTS knowledge_posts (
    id VARCHAR(64) PRIMARY KEY,
    author_id VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    tags VARCHAR(255),
    content TEXT NOT NULL,
    upvotes INT DEFAULT 0,
    answers_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 14. Pay It Forward Pool Grants & Donations Table
CREATE TABLE IF NOT EXISTS pay_it_forward (
    id VARCHAR(64) PRIMARY KEY,
    type VARCHAR(20) NOT NULL, -- DONATE or CLAIM
    user_id VARCHAR(64) NOT NULL,
    amount INT NOT NULL,
    note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

