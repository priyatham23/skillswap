/**
 * SkillSwap - Core Learning & Skill Discovery
 * Pure Credit-Based Learning Ecosystem:
 * Students teach skills to earn credits, and spend credits to learn new skills.
 * Zero forced exchanges. Zero direct swaps. Zero reciprocal barter.
 */
const Matching = {
  currentTab: 'catalog', // 'catalog' or 'mentors'

  async render() {
    const container = document.getElementById('matching-content-area');
    if (!container) return;

    container.innerHTML = `
      <!-- Hero Section: Modern Startup-Style Credit-Based Ecosystem -->
      <div class="card" style="padding: 40px; margin-bottom: 28px; background: linear-gradient(135deg, rgba(30, 27, 75, 0.75), rgba(15, 23, 42, 0.95)), radial-gradient(circle at top right, rgba(99, 102, 241, 0.15), transparent 50%); border: 1px solid rgba(99, 102, 241, 0.25); position: relative; overflow: hidden;">
        <div style="position: absolute; top: -60px; right: -60px; width: 220px; height: 220px; background: radial-gradient(circle, rgba(99, 102, 241, 0.2) 0%, transparent 70%); border-radius: 50%; pointer-events: none;"></div>
        <div style="position: absolute; bottom: -80px; left: 20%; width: 260px; height: 260px; background: radial-gradient(circle, rgba(16, 185, 129, 0.12) 0%, transparent 70%); border-radius: 50%; pointer-events: none;"></div>

        <div style="max-width: 860px; position: relative; z-index: 1;">
          <div style="display: inline-flex; align-items: center; gap: 8px; background: rgba(99, 102, 241, 0.14); border: 1px solid rgba(99, 102, 241, 0.35); padding: 6px 14px; border-radius: var(--radius-full); margin-bottom: 18px;">
            <span style="font-size: 0.8rem; font-weight: 700; color: #a5b4fc; text-transform: uppercase; letter-spacing: 0.5px;">🎓 Student-Powered Learning Community</span>
            <span style="width: 4px; height: 4px; background: #a5b4fc; border-radius: 50%;"></span>
            <span style="font-size: 0.8rem; color: #cbd5e1;">Credit-Based Knowledge Exchange</span>
          </div>

          <h1 style="font-size: 2.3rem; font-weight: 800; line-height: 1.25; margin-bottom: 10px; color: #ffffff; letter-spacing: -0.5px;">
            Learn Something. Teach Something. Grow Together.
          </h1>

          <h2 style="font-size: 1.2rem; font-weight: 600; color: #a5b4fc; margin-bottom: 18px; line-height: 1.4;">
            Your Skills Have Value. Share Them. Earn Credits. Learn More.
          </h2>

          <div style="font-size: 0.96rem; color: #cbd5e1; line-height: 1.7; margin-bottom: 26px; display: flex; flex-direction: column; gap: 10px;">
            <p>
              SkillSwap is a student-powered learning community where everyone has something valuable to teach and something new to learn.
            </p>
            <p>
              Share your knowledge by teaching skills you already know, earn learning credits after completing sessions, and use those credits to explore new skills from other students.
            </p>
            <p style="color: #94a3b8; font-style: italic;">
              No forced exchanges. No limitations. Just a community where knowledge creates opportunities.
            </p>
          </div>

          <div style="display: flex; gap: 14px; flex-wrap: wrap; align-items: center;">
            <button class="btn btn-primary" style="padding: 12px 24px; font-weight: 700; font-size: 0.95rem; box-shadow: 0 4px 18px rgba(99, 102, 241, 0.4);" onclick="Matching.scrollToDiscovery()">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
              Explore Skills
            </button>
            <button class="btn btn-secondary" style="padding: 12px 22px; font-weight: 700; font-size: 0.95rem; border: 1px solid rgba(255, 255, 255, 0.2);" onclick="Matching.openBecomeMentorModal()">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 14l9-5-9-5-9 5 9 5z"></path><path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z"></path></svg>
              Become a Mentor
            </button>
            <div style="display: flex; align-items: center; gap: 16px; margin-left: auto; color: var(--text-dim); font-size: 0.82rem;">
              <span>🔒 Escrow Protected</span>
              <span>•</span>
              <span>⚡ Independent Learning</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Discovery Section Anchor -->
      <div id="skills-discovery-section">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 12px;">
          <div>
            <h2 style="font-size: 1.4rem; font-weight: 700; color: #fff;">Explore Skills & Mentors</h2>
            <p style="color: var(--text-muted); font-size: 0.88rem;">Find campus peers teaching the skills you want to learn. Book 1-on-1 sessions using your credits.</p>
          </div>
          <div style="display: flex; gap: 8px;">
            <button class="btn btn-secondary btn-sm" onclick="App.navigate('mentors')">
              View All Mentors
            </button>
            <button class="btn btn-primary btn-sm" onclick="Matching.openBecomeMentorModal()">
              + Become a Mentor
            </button>
          </div>
        </div>

        <!-- Filters Bar -->
        <div class="card" style="margin-bottom: 24px; padding: 18px 24px;">
          <div style="display: flex; gap: 14px; flex-wrap: wrap; align-items: center;">
            <div style="flex: 1; min-width: 240px;">
              <input type="text" id="teacher-search-query" class="form-control" placeholder="Search by skill (e.g. React, Java, Python, ML), mentor name, or college..." onkeyup="if(event.key==='Enter') Matching.filterTeachers()"/>
            </div>
            <div style="width: 170px;">
              <select id="teacher-tier-filter" class="form-control" onchange="Matching.filterTeachers()">
                <option value="ALL">All Expertise Tiers</option>
                <option value="Pro">Pro Mentors</option>
                <option value="Intermediate">Intermediate+</option>
                <option value="Beginner">Beginner</option>
              </select>
            </div>
            <button class="btn btn-primary" onclick="Matching.filterTeachers()">
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
              Search
            </button>
          </div>
        </div>

        <!-- Mentors Grid -->
        <div id="teachers-results-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 20px;">
          <div style="color: var(--text-dim); padding: 20px;">Loading verified skills and mentors...</div>
        </div>
      </div>
    `;

    this.filterTeachers();
  },

  scrollToDiscovery() {
    const el = document.getElementById('skills-discovery-section');
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
      const input = document.getElementById('teacher-search-query');
      if (input) setTimeout(() => input.focus(), 400);
    }
  },

  async filterTeachers() {
    const grid = document.getElementById('teachers-results-grid');
    if (!grid) return;

    const q = document.getElementById('teacher-search-query') ? document.getElementById('teacher-search-query').value : '';
    const tier = document.getElementById('teacher-tier-filter') ? document.getElementById('teacher-tier-filter').value : 'ALL';

    try {
      const teachers = await API.get('/skills/search', { q, tier });
      if (!teachers || teachers.length === 0) {
        grid.innerHTML = `
          <div class="card" style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 48px;">
            <div style="font-size: 2.2rem; margin-bottom: 12px;">🔍</div>
            <h3 style="font-size: 1.15rem; font-weight: 700; color: #fff; margin-bottom: 6px;">No mentors found</h3>
            <p style="font-size: 0.88rem; color: var(--text-dim); max-width: 440px; margin: 0 auto 18px;">
              Try searching for a different skill (like React, Java, Python, or DSA) or clear your filters.
            </p>
            <button class="btn btn-secondary btn-sm" onclick="document.getElementById('teacher-search-query').value=''; document.getElementById('teacher-tier-filter').value='ALL'; Matching.filterTeachers();">
              Reset Filters
            </button>
          </div>
        `;
        return;
      }

      grid.innerHTML = teachers.map(item => {
        const t = item.teacher;
        const s = item.skill;
        const tierBadge = t.tier === 'Pro' ? 'tier-pro' : (t.tier === 'Intermediate' ? 'tier-intermediate' : 'tier-beginner');
        const formats = t.availableFormats || 'One-to-One, Group Session';
        const slots = t.availableTimeSlots || 'Evenings & Weekends';
        const intro = t.mentorIntro || t.headline || 'Dedicated student mentor helping peers master practical software skills.';

        return `
          <div class="card card-hover hover-lift" style="display: flex; flex-direction: column;">
            <div style="display: flex; gap: 14px; align-items: flex-start; margin-bottom: 14px;">
              <img src="${t.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80'}"
                   style="width: 58px; height: 58px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border-subtle);" alt="${t.name}"/>
              <div style="flex: 1; min-width: 0;">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <h3 style="font-size: 1.05rem; font-weight: 700; color: #fff;">${t.name}</h3>
                  <span class="tier-badge ${tierBadge}">${t.tier || 'Mentor'}</span>
                </div>
                <div style="font-size: 0.82rem; color: var(--text-muted); margin-top: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                  ${t.headline || 'Campus Peer Mentor'}
                </div>
                <div style="font-size: 0.78rem; color: var(--text-dim); margin-top: 2px;">
                  ${t.college || 'Engineering University'}
                </div>
              </div>
            </div>

            <!-- Skill Tag & Verification -->
            <div style="background: var(--bg-surface-2); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 12px 14px; margin-bottom: 12px;">
              <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;">
                <span style="font-size: 0.72rem; color: var(--text-dim); text-transform: uppercase; font-weight: 700;">Offers To Teach</span>
                ${s.isVerified ? `<span class="badge badge-emerald" style="font-size: 0.75rem;">✓ Verified Badge (${s.quizScore}%)</span>` : ''}
              </div>
              <div style="font-size: 1rem; font-weight: 700; color: #a5b4fc;">
                ${s.skillName} <span style="font-size: 0.82rem; font-weight: 500; color: var(--text-muted);">(${s.proficiencyLevel})</span>
              </div>
            </div>

            <p style="font-size: 0.84rem; color: #cbd5e1; line-height: 1.5; margin-bottom: 12px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
              ${intro}
            </p>

            <div style="font-size: 0.78rem; color: var(--text-dim); margin-bottom: 14px; display: flex; flex-direction: column; gap: 4px;">
              <div>📅 <strong>Slots:</strong> ${slots}</div>
              <div>👥 <strong>Formats:</strong> ${formats}</div>
            </div>

            <!-- Pricing & Booking -->
            <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--border-subtle); padding-top: 12px; margin-top: auto;">
              <div>
                <div style="display: flex; align-items: center; gap: 4px;">
                  <span style="color: #f59e0b; font-weight: 700; font-size: 0.88rem;">★ ${item.averageRating || '5.0'}</span>
                  <span style="color: var(--text-dim); font-size: 0.76rem;">(${item.reviewsCount || 0} reviews)</span>
                </div>
                <div style="font-size: 0.86rem; font-weight: 700; color: var(--emerald); margin-top: 2px;">
                  ${item.tierPrice || 15} Credits / session
                </div>
              </div>
              <div style="display: flex; gap: 6px;">
                <button class="btn btn-ghost btn-sm" onclick="Profile.openPassportModal('${t.id}')">Passport</button>
                <button class="btn btn-primary btn-sm" onclick="Sessions.openRequestModal('${t.id}', '${s.skillName}', '${t.name}', ${item.tierPrice || 15})">Book Session</button>
              </div>
            </div>
          </div>
        `;
      }).join('');

    } catch (err) {
      grid.innerHTML = `<div class="card" style="grid-column: 1/-1; color: var(--rose);">Error loading teachers: ${err.message}</div>`;
    }
  },

  /**
   * Dedicated Skill Mentors Page View
   */
  async renderMentorsView() {
    const container = document.getElementById('mentors-content-area');
    if (!container) return;

    container.innerHTML = `
      <div class="page-header" style="margin-bottom: 24px;">
        <div>
          <h1 class="page-title">Skill Mentors</h1>
          <p class="page-desc">Connect with verified student mentors, view their session formats and schedules, or become a mentor yourself to earn learning credits.</p>
        </div>
        <div style="display: flex; gap: 10px;">
          <button class="btn btn-primary" onclick="Matching.openBecomeMentorModal()">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 14l9-5-9-5-9 5 9 5z"></path><path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z"></path></svg>
            Become a Skill Mentor
          </button>
        </div>
      </div>

      <!-- Quick Mentor Stats / Guidelines -->
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 16px; margin-bottom: 24px;">
        <div class="card" style="padding: 18px 20px; border-left: 3px solid var(--emerald);">
          <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase; font-weight: 700; margin-bottom: 4px;">Teach What You Know</div>
          <div style="font-size: 0.92rem; color: #cbd5e1;">Earn credits every time you complete a verified 1-on-1 session with a peer.</div>
        </div>
        <div class="card" style="padding: 18px 20px; border-left: 3px solid var(--primary);">
          <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase; font-weight: 700; margin-bottom: 4px;">Flexible Learning</div>
          <div style="font-size: 0.92rem; color: #cbd5e1;">Spend your earned credits whenever you want to learn something new. No reciprocal obligations.</div>
        </div>
        <div class="card" style="padding: 18px 20px; border-left: 3px solid var(--amber);">
          <div style="font-size: 0.8rem; color: var(--text-dim); text-transform: uppercase; font-weight: 700; margin-bottom: 4px;">Escrow Security</div>
          <div style="font-size: 0.92rem; color: #cbd5e1;">Credits are locked in escrow during booking and paid out immediately upon session completion.</div>
        </div>
      </div>

      <!-- Search & Filters -->
      <div class="card" style="margin-bottom: 24px; padding: 18px 24px;">
        <div style="display: flex; gap: 14px; flex-wrap: wrap; align-items: center;">
          <div style="flex: 1; min-width: 240px;">
            <input type="text" id="mentors-search-input" class="form-control" placeholder="Search mentors by name, skills, or college..." onkeyup="if(event.key==='Enter') Matching.filterMentorsPage()"/>
          </div>
          <div style="width: 170px;">
            <select id="mentors-tier-select" class="form-control" onchange="Matching.filterMentorsPage()">
              <option value="ALL">All Tiers</option>
              <option value="Pro">Pro Mentors</option>
              <option value="Intermediate">Intermediate+</option>
              <option value="Beginner">Beginner</option>
            </select>
          </div>
          <button class="btn btn-primary" onclick="Matching.filterMentorsPage()">Search Mentors</button>
        </div>
      </div>

      <!-- Mentors Grid -->
      <div id="mentors-view-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 20px;">
        <div style="color: var(--text-dim); padding: 20px;">Loading Skill Mentors...</div>
      </div>
    `;

    this.filterMentorsPage();
  },

  async filterMentorsPage() {
    const grid = document.getElementById('mentors-view-grid');
    if (!grid) return;

    const q = document.getElementById('mentors-search-input') ? document.getElementById('mentors-search-input').value : '';
    const tier = document.getElementById('mentors-tier-select') ? document.getElementById('mentors-tier-select').value : 'ALL';

    try {
      const teachers = await API.get('/skills/search', { q, tier });
      if (!teachers || teachers.length === 0) {
        grid.innerHTML = `
          <div class="card" style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 48px;">
            <div style="font-size: 2.2rem; margin-bottom: 12px;">🎓</div>
            <h3 style="font-size: 1.15rem; font-weight: 700; color: #fff; margin-bottom: 6px;">No mentors found</h3>
            <p style="font-size: 0.88rem; color: var(--text-dim); max-width: 440px; margin: 0 auto 18px;">
              Try adjusting your search criteria or register as a mentor to be featured here!
            </p>
            <button class="btn btn-primary btn-sm" onclick="Matching.openBecomeMentorModal()">+ Become a Skill Mentor</button>
          </div>
        `;
        return;
      }

      grid.innerHTML = teachers.map(item => {
        const t = item.teacher;
        const s = item.skill;
        const tierBadge = t.tier === 'Pro' ? 'tier-pro' : (t.tier === 'Intermediate' ? 'tier-intermediate' : 'tier-beginner');
        const formats = t.availableFormats || 'One-to-One, Group Session';
        const slots = t.availableTimeSlots || 'Mon-Fri Evenings, Weekends';
        const intro = t.mentorIntro || t.bio || t.headline || 'Passionate student mentor ready to guide peers in building real-world projects.';

        return `
          <div class="card card-hover hover-lift" style="display: flex; flex-direction: column;">
            <div style="display: flex; gap: 14px; align-items: flex-start; margin-bottom: 14px;">
              <img src="${t.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80'}"
                   style="width: 60px; height: 60px; border-radius: 50%; object-fit: cover; border: 2px solid var(--border-subtle);" alt="${t.name}"/>
              <div style="flex: 1; min-width: 0;">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                  <h3 style="font-size: 1.1rem; font-weight: 700; color: #fff;">${t.name}</h3>
                  <span class="tier-badge ${tierBadge}">${t.tier || 'Mentor'}</span>
                </div>
                <div style="font-size: 0.82rem; color: var(--text-muted); margin-top: 2px;">
                  ${t.headline || 'Campus Peer Mentor'}
                </div>
                <div style="font-size: 0.78rem; color: var(--text-dim); margin-top: 2px;">
                  ${t.college || 'Engineering Campus'}
                </div>
              </div>
            </div>

            <!-- Skills Taught -->
            <div style="background: var(--bg-surface-2); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 12px 14px; margin-bottom: 12px;">
              <div style="font-size: 0.72rem; color: var(--text-dim); text-transform: uppercase; font-weight: 700; margin-bottom: 4px;">Skills They Can Teach:</div>
              <div style="display: flex; align-items: center; justify-content: space-between;">
                <span style="font-size: 0.98rem; font-weight: 700; color: #a5b4fc;">
                  ${s.skillName} <span style="font-size: 0.8rem; font-weight: 500; color: var(--text-muted);">(${s.proficiencyLevel})</span>
                </span>
                ${s.isVerified ? `<span class="badge badge-emerald" style="font-size: 0.75rem;">✓ Verified (${s.quizScore}%)</span>` : ''}
              </div>
            </div>

            <!-- Short Intro -->
            <p style="font-size: 0.86rem; color: #cbd5e1; line-height: 1.5; margin-bottom: 14px; display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;">
              ${intro}
            </p>

            <!-- Availability & Formats -->
            <div style="font-size: 0.8rem; color: var(--text-dim); margin-bottom: 16px; display: flex; flex-direction: column; gap: 5px; background: rgba(0,0,0,0.15); padding: 10px; border-radius: var(--radius-sm);">
              <div><strong style="color: var(--text-muted);">Session Formats:</strong> ${formats}</div>
              <div><strong style="color: var(--text-muted);">Preferred Slots:</strong> ${slots}</div>
            </div>

            <!-- Pricing & Booking -->
            <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--border-subtle); padding-top: 14px; margin-top: auto;">
              <div>
                <div style="display: flex; align-items: center; gap: 4px;">
                  <span style="color: #f59e0b; font-weight: 700; font-size: 0.9rem;">★ ${item.averageRating || '5.0'}</span>
                  <span style="color: var(--text-dim); font-size: 0.76rem;">(${item.reviewsCount || 0} reviews)</span>
                </div>
                <div style="font-size: 0.88rem; font-weight: 700; color: var(--emerald); margin-top: 2px;">
                  ${item.tierPrice || 15} Credits / session
                </div>
              </div>
              <div style="display: flex; gap: 8px;">
                <button class="btn btn-ghost btn-sm" onclick="Profile.openPassportModal('${t.id}')">Passport</button>
                <button class="btn btn-primary btn-sm" onclick="Sessions.openRequestModal('${t.id}', '${s.skillName}', '${t.name}', ${item.tierPrice || 15})">Book Session</button>
              </div>
            </div>
          </div>
        `;
      }).join('');

    } catch (err) {
      grid.innerHTML = `<div class="card" style="grid-column: 1/-1; color: var(--rose);">Error loading mentors: ${err.message}</div>`;
    }
  },

  /**
   * Become a Skill Mentor Modal
   * Allows any student to register what skills they teach, their intro, formats, slots and credit rate.
   */
  openBecomeMentorModal() {
    if (!Auth.isAuthenticated()) {
      App.showToast('Please sign in or choose a demo account to become a mentor.', 'info');
      App.navigate('login');
      return;
    }

    const u = Auth.currentUser;
    const body = `
      <form id="become-mentor-form" onsubmit="event.preventDefault(); Matching.submitBecomeMentor();">
        <div class="form-group">
          <label class="form-label">Skills You Can Teach <span style="color: var(--rose); font-weight: normal;">*</span></label>
          <input type="text" id="mentor-skills-input" class="form-control" placeholder="e.g. React, Java, Spring Boot, Data Structures, Python" required/>
          <div style="font-size: 0.76rem; color: var(--text-dim); margin-top: 4px;">Enter one or multiple skills separated by commas.</div>
        </div>

        <div class="form-group">
          <label class="form-label">Short Introduction / Mentor Bio <span style="color: var(--rose); font-weight: normal;">*</span></label>
          <textarea id="mentor-intro-input" class="form-control" rows="3" placeholder="Briefly describe what you can teach and how you can help fellow students..." required>${u.mentorIntro || u.bio || ''}</textarea>
        </div>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px;">
          <div class="form-group">
            <label class="form-label">Expertise Level</label>
            <select id="mentor-level-select" class="form-control">
              <option value="Beginner" ${u.tier === 'Beginner' ? 'selected' : ''}>Beginner (Fundamentals)</option>
              <option value="Intermediate" ${u.tier === 'Intermediate' || !u.tier ? 'selected' : ''}>Intermediate (Practical / Project)</option>
              <option value="Pro" ${u.tier === 'Pro' ? 'selected' : ''}>Pro (Advanced / Industry Intern)</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Session Credit Rate</label>
            <input type="number" id="mentor-rate-input" class="form-control" min="5" max="100" value="${u.tierPrice || 15}" required/>
            <div style="font-size: 0.76rem; color: var(--text-dim); margin-top: 4px;">Credits you receive per completed session.</div>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Available Session Formats</label>
          <div style="display: flex; gap: 16px; margin-top: 6px;">
            <label style="display: flex; align-items: center; gap: 8px; font-size: 0.88rem; cursor: pointer;">
              <input type="checkbox" id="fmt-one-to-one" checked/>
              <span>One-to-One Session</span>
            </label>
            <label style="display: flex; align-items: center; gap: 8px; font-size: 0.88rem; cursor: pointer;">
              <input type="checkbox" id="fmt-group-session" checked/>
              <span>Group Session</span>
            </label>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Preferred / Available Time Slots</label>
          <input type="text" id="mentor-slots-input" class="form-control" placeholder="e.g. Mon-Fri 6:00 PM - 9:00 PM, Weekends 10 AM - 2 PM" value="${u.availableTimeSlots || 'Evenings & Weekends'}"/>
        </div>

        <div style="background: rgba(16, 185, 129, 0.1); border: 1px solid rgba(16, 185, 129, 0.3); border-radius: var(--radius-md); padding: 12px; margin-bottom: 20px; font-size: 0.85rem; color: #a7f3d0;">
          💡 <strong>Earn While Helping:</strong> When a learner books a session with you, their credits are held in secure escrow and released straight to your balance as soon as the session is completed!
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 10px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Register as Mentor</button>
        </div>
      </form>
    `;

    App.openModal('Become a Skill Mentor', body);
  },

  async submitBecomeMentor() {
    const rawSkills = document.getElementById('mentor-skills-input').value;
    const mentorIntro = document.getElementById('mentor-intro-input').value;
    const expertiseLevel = document.getElementById('mentor-level-select').value;
    const tierPrice = parseInt(document.getElementById('mentor-rate-input').value) || 15;
    const slots = document.getElementById('mentor-slots-input').value;

    const formats = [];
    if (document.getElementById('fmt-one-to-one').checked) formats.push('One-to-One');
    if (document.getElementById('fmt-group-session').checked) formats.push('Group Session');

    const skills = rawSkills.split(',').map(s => s.trim()).filter(Boolean);

    try {
      await API.post('/skills/become-mentor', {
        skills,
        mentorIntro,
        expertiseLevel,
        availableFormats: formats.join(', ') || 'One-to-One',
        availableTimeSlots: slots,
        tierPrice
      });

      App.closeModal();
      App.showToast('You are now registered as a Skill Mentor! Learners can book sessions with you to earn credits.', 'success');
      await Auth.init();
      Matching.render();
      if (document.getElementById('mentors-content-area')) {
        Matching.renderMentorsView();
      }
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  }
};
