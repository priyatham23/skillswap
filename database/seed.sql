-- =========================================================================
-- SkillSwap Seed Data (MySQL)
-- Pre-seeded with 5 campus mentors, reciprocal direct swaps, 3-way cycle,
-- sample sessions, credit ledger history, hackathons, and knowledge base.
-- =========================================================================

USE skillswap;

-- 1. Insert Users
INSERT INTO users (id, name, email, password_hash, salt, avatar_url, headline, bio, college, linkedin_url, github_username, tier, tier_price, credits, escrow_credits, streak_days, role, is_banned)
VALUES
('admin-1', 'Prof. Admin (Dean of Studies)', 'admin@skillswap.edu', 'P/e9G8w6hI6D6Rk5...', 'salt1', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=250&q=80', 'Campus Dean & Platform Verification Admin', 'Overseeing campus peer mentorship and validating verified skill credentials.', 'Stanford / IIT Campus Network', 'https://linkedin.com/in/dean-admin', '', 'Pro', 50, 500, 0, 30, 'ADMIN', FALSE),

('user-1', 'Priya Sharma', 'priya@skillswap.edu', 'hash1...', 'salt1', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=250&q=80', 'Frontend Architect | React 19 & Tailwind Specialist', 'Senior CS student with 2 internships. Love building responsive UI systems and helping juniors master modern frontend.', 'Birla Institute of Technology (BITS)', 'https://linkedin.com/in/priya-sharma-demo', 'priya-frontend', 'Pro', 50, 240, 10, 14, 'USER', FALSE),

('user-2', 'Rahul Verma', 'rahul@skillswap.edu', 'hash2...', 'salt2', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=250&q=80', 'AI/ML Researcher | PyTorch, NLP & Computer Vision', 'Published 1 paper on transformer attention optimizations. Passionate about machine learning foundations.', 'IIT Bombay', 'https://linkedin.com/in/rahul-verma-ml', 'rahul-ml', 'Intermediate', 25, 180, 0, 7, 'USER', FALSE),

('user-3', 'Ananya Patel', 'ananya@skillswap.edu', 'hash3...', 'salt3', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=250&q=80', 'LeetCode Knight (2100+) | Java & Spring Boot Enthusiast', 'Cracked multiple backend interviews. Love breaking down complex graph and dynamic programming problems.', 'Delhi Technological University (DTU)', 'https://linkedin.com/in/ananya-patel-dsa', 'ananya-code', 'Intermediate', 25, 150, 0, 9, 'USER', FALSE),

('user-4', 'Rohan Mehta', 'rohan@skillswap.edu', 'hash4...', 'salt4', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=250&q=80', 'Cloud Systems Specialist | AWS & High-Scale Architecture', 'Ex-Amazon Intern. Mentoring students on scalable distributed systems, caching, and database partitioning.', 'IIIT Hyderabad', 'https://linkedin.com/in/rohan-mehta-cloud', 'rohan-cloud', 'Pro', 50, 320, 0, 21, 'USER', FALSE),

('user-5', 'Sneha Reddy', 'sneha@skillswap.edu', 'hash5...', 'salt5', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=250&q=80', 'Backend & Microservices Engineer | Go & Kubernetes', 'Passionate about high-throughput concurrency in Go. Seeking deeper knowledge in high-level system design tradeoffs.', 'NIT Trichy', 'https://linkedin.com/in/sneha-reddy-go', 'sneha-go', 'Pro', 50, 290, 0, 18, 'USER', FALSE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 2. Insert User Skills (Pre-configured 3-Way Cycle: Priya -> Rahul -> Ananya -> Priya, plus Direct Swap: Rohan <-> Sneha)
INSERT INTO user_skills (id, user_id, skill_name, skill_type, proficiency_level, is_verified, quiz_score) VALUES
('sk-1', 'user-1', 'React', 'TEACH', 'Expert', TRUE, 95),
('sk-2', 'user-1', 'TypeScript', 'TEACH', 'Advanced', TRUE, 90),
('sk-3', 'user-1', 'Machine Learning', 'LEARN', 'Beginner', FALSE, 0),
('sk-3b', 'user-1', 'DSA', 'LEARN', 'Intermediate', FALSE, 0),

('sk-4', 'user-2', 'Machine Learning', 'TEACH', 'Advanced', TRUE, 88),
('sk-5', 'user-2', 'Python', 'TEACH', 'Expert', TRUE, 94),
('sk-6', 'user-2', 'Java', 'LEARN', 'Beginner', FALSE, 0),

('sk-7', 'user-3', 'Java', 'TEACH', 'Expert', TRUE, 92),
('sk-8', 'user-3', 'DSA', 'TEACH', 'Expert', TRUE, 96),
('sk-9', 'user-3', 'React', 'LEARN', 'Beginner', FALSE, 0),

('sk-10', 'user-4', 'System Design', 'TEACH', 'Expert', TRUE, 98),
('sk-11', 'user-4', 'Golang', 'LEARN', 'Intermediate', FALSE, 0),

('sk-12', 'user-5', 'Golang', 'TEACH', 'Expert', TRUE, 94),
('sk-13', 'user-5', 'System Design', 'LEARN', 'Intermediate', FALSE, 0)
ON DUPLICATE KEY UPDATE proficiency_level=VALUES(proficiency_level);

-- 3. Insert Education
INSERT INTO education (id, user_id, school, degree, field_of_study, start_year, end_year) VALUES
('edu-1', 'user-1', 'BITS Pilani', 'B.Tech', 'Computer Science', 2022, 2026),
('edu-2', 'user-2', 'IIT Bombay', 'B.Tech', 'Electrical & Computer Science', 2023, 2027),
('edu-3', 'user-3', 'Delhi Technological University', 'B.Tech', 'Information Technology', 2022, 2026),
('edu-4', 'user-4', 'IIIT Hyderabad', 'B.Tech & M.S.', 'Computer Science', 2021, 2025),
('edu-5', 'user-5', 'NIT Trichy', 'B.Tech', 'Electronics & CS', 2022, 2026)
ON DUPLICATE KEY UPDATE school=VALUES(school);

-- 4. Insert Projects
INSERT INTO projects (id, user_id, title, description, github_repo_url, live_url, tags, stars, is_imported) VALUES
('prj-1', 'user-1', 'Kanban Flow Studio', 'Collaborative drag-and-drop workspace with optimistic state updates', 'https://github.com/priya/kanban-flow', 'https://kanban-demo.io', 'React, Tailwind, WebSockets', 124, FALSE),
('prj-2', 'user-1', 'CSS Neumorphic UI Design Kit', 'Popular component library with 2k+ community stars', 'https://github.com/priya/neumorphic-ui', '', 'CSS3, Vanilla JS', 210, TRUE),
('prj-3', 'user-2', 'Edge Vision Face Detector', 'Real-time edge inference model optimized for micro-controllers', 'https://github.com/rahul/edge-vision', '', 'PyTorch, OpenCV', 85, TRUE),
('prj-4', 'user-3', 'Distributed Job Scheduler', 'Fault-tolerant Redis queue built with Java Virtual Threads', 'https://github.com/ananya/job-scheduler', '', 'Java 21, Spring Boot, Redis', 140, FALSE)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 5. Insert Certificates
INSERT INTO certificates (id, user_id, title, issuing_org, issue_date, credential_url, proof_description, status, admin_notes) VALUES
('cert-1', 'user-1', 'Meta Certified Frontend Developer', 'Meta / Coursera', 'March 2025', 'https://coursera.org/meta-cert-123', 'Verified professional certificate in React and state management.', 'APPROVED', 'Verified by campus admin.'),
('cert-2', 'user-2', 'DeepLearning.AI TensorFlow Specialization', 'DeepLearning.AI', 'Jan 2025', 'https://deeplearning.ai/certs/tf-99', 'Sequence models and deep neural networks mastery.', 'APPROVED', 'Verified by campus admin.'),
('cert-3', 'user-3', 'Oracle Certified Professional: Java SE 17', 'Oracle', 'August 2024', 'https://oracle.com/certs/ocp-java-88', 'Passed OCP exam with 93% score.', 'APPROVED', 'Verified by campus admin.')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 6. Insert Sessions
INSERT INTO sessions (id, session_type, teacher_id, learner_id, skill_topic, description, scheduled_time, duration_minutes, credit_amount, status, meeting_link, total_seats, booked_seats) VALUES
('sess-1', 'DIRECT_SWAP', 'user-4', 'user-5', 'High-Throughput Rate Limiter Design', 'Pair session designing Token Bucket and Leaky Bucket in distributed Redis.', 'Tomorrow, 6:00 PM', 60, 25, 'ACCEPTED', 'https://meet.jit.si/SkillSwap-sess-1', 1, 1),
('sess-2', 'GROUP_CLASS', 'user-1', 'GROUP_SESSION', 'Modern React 19 State Architecture & Hooks Masterclass', 'Comprehensive 90-min live session covering Server Components, Actions, and optimistic UI transitions.', 'Saturday, 4:00 PM', 90, 15, 'ACCEPTED', 'https://meet.jit.si/SkillSwap-sess-2', 12, 1),
('sess-3', 'DOUBT_SPRINT', 'user-2', 'user-1', 'PyTorch CUDA out of memory error during backward pass', 'Gradient tensor accumulation bug inside custom training loop. Need 15 min quick fix.', 'HIGH', 15, 2, 'COMPLETED', 'https://meet.jit.si/SkillSwap-sess-3', 1, 1)
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 7. Insert Reviews
INSERT INTO reviews (id, session_id, teacher_id, learner_id, rating, review_text) VALUES
('rev-1', 'sess-3', 'user-2', 'user-1', 5, 'Rahul spotted the tensor memory accumulation leak in under 5 minutes! Unblocked my submission just in time.')
ON DUPLICATE KEY UPDATE rating=VALUES(rating);

-- 8. Insert Credit Ledger
INSERT INTO credit_ledger (id, user_id, transaction_type, amount, balance_after, description, reference_id) VALUES
('tx-1', 'user-1', 'SIGNUP_BONUS', 100, 100, 'Campus Sign-Up Welcome Bonus', 'WELCOME'),
('tx-2', 'user-1', 'TEACHING_REWARD', 50, 150, 'Completed React Mentorship Session', 'sess-10'),
('tx-3', 'user-1', 'PURCHASE', 100, 250, 'Recharged wallet (Demo College Pass)', 'TXN-9981'),
('tx-4', 'user-1', 'ESCROW_HOLD', -10, 240, 'Escrow held for Group Class', 'sess-2')
ON DUPLICATE KEY UPDATE amount=VALUES(amount);

-- 9. Insert Hackathon Teams
INSERT INTO hackathon_teams (id, organizer_id, title, hackathon_name, description, looking_for_skills, max_members) VALUES
('ht-1', 'user-1', 'MedAI - Diagnostic Assistant for Rural Clinics', 'Smart India Hackathon 2026', 'Building an offline-first mobile web app with quantized local LLMs for symptom triage.', 'Machine Learning Engineer, Backend Go Developer', 4),
('ht-2', 'user-4', 'CampusRide - Zero-Gas Peer Carpooling', 'EcoHacks Global', 'Microservice-based carpool route optimization for university commuter students.', 'Frontend UI/UX, Flutter/React dev', 4)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- 10. Insert Knowledge Base Posts
INSERT INTO knowledge_posts (id, author_id, title, category, tags, content, upvotes, answers_count) VALUES
('kp-1', 'user-3', 'How does Java Virtual Thread (Project Loom) handle IO-bound blocking?', 'Java & Backend', 'Java21, Concurrency, VirtualThreads', 'Virtual threads are user-mode threads managed by the JDK rather than OS kernel threads. When a virtual thread performs a blocking I/O operation, the runtime unmounts it from the carrier thread, parking the continuation and freeing the carrier thread for other work!', 18, 1),
('kp-2', 'user-1', 'Why use useActionState over manual useState for forms in modern React?', 'Frontend & React', 'React19, Hooks, Forms', 'useActionState natively handles pending state, server error responses, and form reset lifecycles without requiring duplicate isSubmitting or error state variables.', 14, 0)
ON DUPLICATE KEY UPDATE upvotes=VALUES(upvotes);
