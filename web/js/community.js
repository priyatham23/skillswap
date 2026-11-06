/**
 * SkillSwap Community, Chat, Hackathon Team Finder & Knowledge Base
 */
const Community = {
  currentTab: 'hackathons', // 'hackathons', 'leaderboard', 'knowledge', 'chat'
  activeChatPeerId: null,
  activeChatPeerName: null,

  async render() {
    const container = document.getElementById('community-content-area');
    if (!container) return;

    container.innerHTML = `
      <div class="page-header">
        <div>
          <h1 class="page-title">Campus Community & Collaboration</h1>
          <p class="page-desc">Direct messaging, hackathon teammate finder, campus leaderboard, and answered doubts knowledge base.</p>
        </div>
        <div style="display: flex; gap: 8px;">
          <button class="btn ${this.currentTab === 'hackathons' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="Community.switchTab('hackathons')">Hackathon Finder</button>
          <button class="btn ${this.currentTab === 'leaderboard' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="Community.switchTab('leaderboard')">Leaderboard & Streaks</button>
          <button class="btn ${this.currentTab === 'knowledge' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="Community.switchTab('knowledge')">Knowledge Base Q&A</button>
          <button class="btn ${this.currentTab === 'chat' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="Community.switchTab('chat')">Messages</button>
        </div>
      </div>

      <div id="community-tab-container"></div>
    `;

    this.renderActiveTab();
  },

  switchTab(t) {
    this.currentTab = t;
    this.render();
  },

  async renderActiveTab() {
    const container = document.getElementById('community-tab-container');
    if (!container) return;

    if (this.currentTab === 'hackathons') {
      await this.renderHackathons(container);
    } else if (this.currentTab === 'leaderboard') {
      await this.renderLeaderboard(container);
    } else if (this.currentTab === 'knowledge') {
      await this.renderKnowledge(container);
    } else if (this.currentTab === 'chat') {
      await this.renderChat(container);
    }
  },

  // ==================== HACKATHON FINDER ====================

  async renderHackathons(container) {
    container.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h3 style="font-size: 1.15rem; font-weight: 700;">Active Hackathon Teams Looking for Teammates</h3>
        <button class="btn btn-primary btn-sm" onclick="Community.openCreateHackathonModal()">+ Post Hackathon Team</button>
      </div>

      <div id="hackathons-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(380px, 1fr)); gap: 20px;">
        <div style="color: var(--text-dim); padding: 20px;">Loading hackathon teams...</div>
      </div>
    `;

    try {
      const teams = await API.get('/community/hackathons');
      const grid = document.getElementById('hackathons-grid');
      if (!grid) return;

      if (teams.length === 0) {
        grid.innerHTML = `<div class="card" style="grid-column: 1/-1; text-align: center; color: var(--text-dim); padding: 40px;">No hackathon teams posted yet. Be the first to assemble a team!</div>`;
        return;
      }

      grid.innerHTML = teams.map(t => {
        const spotsLeft = t.maxMembers - t.currentMembers;
        return `
          <div class="card card-hover hover-lift">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px;">
              <div>
                <span class="badge badge-emerald" style="font-size: 0.72rem; margin-bottom: 6px;">${t.hackathonName}</span>
                <h3 style="font-size: 1.15rem; font-weight: 700;">${t.title}</h3>
              </div>
              <span class="badge ${spotsLeft > 0 ? 'badge-primary' : 'badge-rose'}">
                ${spotsLeft > 0 ? `${spotsLeft} Spots Open` : 'Team Full'}
              </span>
            </div>

            <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 16px; line-height: 1.5;">
              ${t.description || 'No description provided.'}
            </p>

            <div style="background: var(--bg-surface-2); border-radius: var(--radius-md); padding: 12px 14px; margin-bottom: 16px;">
              <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--text-dim); font-weight: 700;">Looking For Roles:</div>
              <div style="font-size: 0.88rem; font-weight: 600; color: #a5b4fc; margin-top: 2px;">${t.lookingForSkills}</div>
            </div>

            <div style="border-top: 1px solid var(--border-subtle); padding-top: 14px; display: flex; justify-content: space-between; align-items: center;">
              <div style="font-size: 0.8rem; color: var(--text-dim);">
                Led by: <strong>${t.organizerName}</strong> (${t.currentMembers}/${t.maxMembers} Members)
              </div>
              ${spotsLeft > 0 ? `
                <button class="btn btn-emerald btn-sm" onclick="Community.openJoinHackathonModal('${t.id}', '${t.title}')">
                  Join Team
                </button>
              ` : `
                <button class="btn btn-ghost btn-sm" disabled>Full</button>
              `}
            </div>
          </div>
        `;
      }).join('');
    } catch (err) {
      container.innerHTML = `<div class="card" style="color: var(--rose);">Error loading teams: ${err.message}</div>`;
    }
  },

  openCreateHackathonModal() {
    if (!Auth.isAuthenticated()) {
      App.showToast('Please log in first.', 'info');
      return;
    }
    const body = `
      <form onsubmit="event.preventDefault(); Community.submitHackathonTeam();">
        <div class="form-group">
          <label class="form-label">Project / Idea Title</label>
          <input type="text" id="hack-title" class="form-control" placeholder="e.g. AI MedAssist - Offline Triage Mobile App" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Hackathon Name</label>
          <input type="text" id="hack-name" class="form-control" placeholder="e.g. Smart India Hackathon 2026 / ETHGlobal" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Idea Description</label>
          <textarea id="hack-desc" class="form-control" rows="3" placeholder="Explain the problem statement and approach..."></textarea>
        </div>
        <div class="form-group">
          <label class="form-label">Roles & Skills Needed</label>
          <input type="text" id="hack-skills" class="form-control" placeholder="e.g. PyTorch / ML Engineer, React Frontend Dev" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Team Capacity (Max Members)</label>
          <input type="number" id="hack-capacity" class="form-control" value="4" min="2" max="6"/>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Publish Team Listing</button>
        </div>
      </form>
    `;
    App.openModal('Post a Hackathon Team', body);
  },

  async submitHackathonTeam() {
    const data = {
      title: document.getElementById('hack-title').value,
      hackathonName: document.getElementById('hack-name').value,
      description: document.getElementById('hack-desc').value,
      lookingForSkills: document.getElementById('hack-skills').value,
      maxMembers: parseInt(document.getElementById('hack-capacity').value)
    };

    try {
      await API.post('/community/hackathons/create', data);
      App.closeModal();
      App.showToast('Hackathon team listing created!', 'success');
      Community.renderActiveTab();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openJoinHackathonModal(teamId, title) {
    if (!Auth.isAuthenticated()) {
      App.showToast('Please log in to join teams.', 'info');
      return;
    }
    const body = `
      <form onsubmit="event.preventDefault(); Community.submitJoinTeam('${teamId}');">
        <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 16px;">
          Applying to join: <strong>${title}</strong>
        </p>
        <div class="form-group">
          <label class="form-label">Your Proposed Role in this Team</label>
          <input type="text" id="join-role" class="form-control" placeholder="e.g. Frontend UI/UX Architect or Backend Spring Boot dev" required/>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-emerald">Confirm & Join Team</button>
        </div>
      </form>
    `;
    App.openModal('Join Hackathon Team', body);
  },

  async submitJoinTeam(teamId) {
    const role = document.getElementById('join-role').value;
    try {
      await API.post('/community/hackathons/join', { teamId, role });
      App.closeModal();
      App.showToast('Successfully joined team!', 'success');
      Community.renderActiveTab();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  // ==================== LEADERBOARD ====================

  async renderLeaderboard(container) {
    try {
      const ranked = await API.get('/community/leaderboard');

      container.innerHTML = `
        <div class="card" style="margin-bottom: 24px; padding: 24px;">
          <div class="card-title">
            <span>Campus Leaderboard & Streaks</span>
            <span class="badge badge-emerald">Ranked by Teaching Activity & Verified Badges</span>
          </div>

          <div style="overflow-x: auto; margin-top: 18px;">
            <table style="width: 100%; border-collapse: collapse; font-size: 0.9rem; text-align: left;">
              <thead>
                <tr style="border-bottom: 1px solid var(--border-subtle); color: var(--text-dim); font-size: 0.78rem; text-transform: uppercase;">
                  <th style="padding: 12px 14px;">Rank</th>
                  <th style="padding: 12px 14px;">Peer Mentor</th>
                  <th style="padding: 12px 14px;">Campus / University</th>
                  <th style="padding: 12px 14px;">Tier</th>
                  <th style="padding: 12px 14px; text-align: center;">Verified Skills</th>
                  <th style="padding: 12px 14px; text-align: center;">Streak</th>
                  <th style="padding: 12px 14px; text-align: right;">Credits Balance</th>
                </tr>
              </thead>
              <tbody>
                ${ranked.map(r => {
                  const medal = r.rank === 1 ? '🥇' : (r.rank === 2 ? '🥈' : (r.rank === 3 ? '🥉' : `#${r.rank}`));
                  const tierBadge = r.tier === 'Pro' ? 'tier-pro' : (r.tier === 'Intermediate' ? 'tier-intermediate' : 'tier-beginner');

                  return `
                    <tr style="border-bottom: 1px solid var(--border-subtle); cursor: pointer;" onclick="Profile.openPassportModal('${r.id}')">
                      <td style="padding: 14px; font-weight: 800; font-size: 1.1rem; color: ${r.rank <= 3 ? '#fbbf24' : 'var(--text-dim)'};">${medal}</td>
                      <td style="padding: 14px;">
                        <div style="display: flex; gap: 12px; align-items: center;">
                          <img src="${r.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80'}"
                               style="width: 38px; height: 38px; border-radius: 50%; object-fit: cover;" alt="Avatar"/>
                          <div>
                            <div style="font-weight: 700; color: #fff;">${r.name}</div>
                            <div style="font-size: 0.78rem; color: var(--text-muted);">${r.headline || 'Peer Learner'}</div>
                          </div>
                        </div>
                      </td>
                      <td style="padding: 14px; color: var(--text-muted); font-size: 0.84rem;">${r.college || 'Engineering Campus'}</td>
                      <td style="padding: 14px;"><span class="tier-badge ${tierBadge}">${r.tier}</span></td>
                      <td style="padding: 14px; text-align: center;">
                        <span class="badge badge-emerald">✓ ${r.verifiedSkillsCount} Badges</span>
                      </td>
                      <td style="padding: 14px; text-align: center; font-weight: 700; color: #f59e0b;">
                        🔥 ${r.streakDays} Days
                      </td>
                      <td style="padding: 14px; text-align: right; font-weight: 800; color: var(--emerald);">
                        ${r.credits} CR
                      </td>
                    </tr>
                  `;
                }).join('')}
              </tbody>
            </table>
          </div>
        </div>
      `;
    } catch (err) {
      container.innerHTML = `<div class="card" style="color: var(--rose);">Error loading leaderboard: ${err.message}</div>`;
    }
  },

  // ==================== KNOWLEDGE BASE ====================

  async renderKnowledge(container) {
    container.innerHTML = `
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h3 style="font-size: 1.15rem; font-weight: 700;">Answered Doubts & Session Summaries</h3>
        <button class="btn btn-primary btn-sm" onclick="Community.openPostDoubtModal()">+ Ask a Doubt</button>
      </div>

      <div id="knowledge-posts-list" style="display: flex; flex-direction: column; gap: 16px;">
        <div style="color: var(--text-dim); padding: 20px;">Loading questions & summaries...</div>
      </div>
    `;

    try {
      const posts = await API.get('/community/knowledge');
      const listDiv = document.getElementById('knowledge-posts-list');
      if (!listDiv) return;

      if (posts.length === 0) {
        listDiv.innerHTML = `<div class="card" style="text-align: center; color: var(--text-dim); padding: 40px;">No doubt posts yet. Be the first to ask!</div>`;
        return;
      }

      listDiv.innerHTML = posts.map(p => {
        return `
          <div class="card" style="padding: 22px 26px;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px;">
              <div>
                <span class="badge badge-primary" style="font-size: 0.72rem; margin-bottom: 6px;">${p.category}</span>
                <h3 style="font-size: 1.1rem; font-weight: 700; color: #fff;">${p.title}</h3>
              </div>
              <button class="btn btn-secondary btn-sm" onclick="Community.upvotePost('${p.id}')">
                ▲ Upvote (${p.upvotes || 0})
              </button>
            </div>

            <p style="font-size: 0.9rem; color: #cbd5e1; margin-bottom: 14px; line-height: 1.6;">
              ${p.content}
            </p>

            <div style="font-size: 0.78rem; color: var(--cyan); margin-bottom: 14px;">Tags: ${p.tags || 'General'}</div>

            <!-- Answers List -->
            ${p.answers && p.answers.length > 0 ? `
              <div style="background: var(--bg-surface-2); border-left: 2px solid var(--emerald); border-radius: var(--radius-sm); padding: 12px 16px; margin-bottom: 14px;">
                <div style="font-size: 0.75rem; text-transform: uppercase; color: var(--emerald); font-weight: 700; margin-bottom: 6px;">Top Verified Answer:</div>
                <div style="font-size: 0.88rem; color: #e2e8f0; line-height: 1.5;">${p.answers[0].answerText}</div>
                <div style="font-size: 0.75rem; color: var(--text-dim); margin-top: 4px;">— ${p.answers[0].responderName}</div>
              </div>
            ` : ''}

            <div style="border-top: 1px solid var(--border-subtle); padding-top: 12px; display: flex; justify-content: space-between; align-items: center; font-size: 0.8rem; color: var(--text-dim);">
              <span>Asked by <strong>${p.authorName}</strong></span>
              <button class="btn btn-ghost btn-sm" onclick="Community.openAnswerModal('${p.id}', '${p.title}')">+ Add Answer</button>
            </div>
          </div>
        `;
      }).join('');
    } catch (err) {
      container.innerHTML = `<div class="card" style="color: var(--rose);">Error loading knowledge base: ${err.message}</div>`;
    }
  },

  openPostDoubtModal() {
    if (!Auth.isAuthenticated()) {
      App.showToast('Please log in to post doubts.', 'info');
      return;
    }
    const body = `
      <form onsubmit="event.preventDefault(); Community.submitDoubt();">
        <div class="form-group">
          <label class="form-label">Question / Doubt Title</label>
          <input type="text" id="doubt-title" class="form-control" placeholder="e.g. Why does my Spring Data JPA query trigger N+1 queries?" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Category</label>
          <select id="doubt-cat" class="form-control">
            <option value="Java & Spring">Java & Spring Boot</option>
            <option value="Frontend & React">Frontend & React</option>
            <option value="Machine Learning">Machine Learning / AI</option>
            <option value="Data Structures & Algorithms">Data Structures & Algorithms</option>
            <option value="DevOps & Cloud">DevOps & Cloud</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">Tags</label>
          <input type="text" id="doubt-tags" class="form-control" placeholder="e.g. Hibernate, Performance, Database"/>
        </div>
        <div class="form-group">
          <label class="form-label">Detailed Explanation</label>
          <textarea id="doubt-content" class="form-control" rows="4" placeholder="Include context, code behavior, and attempts made..." required></textarea>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Post Doubt to Campus</button>
        </div>
      </form>
    `;
    App.openModal('Ask a Doubt to Campus Peers', body);
  },

  async submitDoubt() {
    const data = {
      title: document.getElementById('doubt-title').value,
      category: document.getElementById('doubt-cat').value,
      tags: document.getElementById('doubt-tags').value,
      content: document.getElementById('doubt-content').value
    };

    try {
      await API.post('/community/knowledge/create', data);
      App.closeModal();
      App.showToast('Question published!', 'success');
      Community.renderActiveTab();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async upvotePost(postId) {
    if (!Auth.isAuthenticated()) {
      App.showToast('Please log in to upvote.', 'info');
      return;
    }
    try {
      await API.post('/community/knowledge/upvote', { postId });
      Community.renderActiveTab();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openAnswerModal(postId, title) {
    if (!Auth.isAuthenticated()) {
      App.showToast('Please log in to answer.', 'info');
      return;
    }
    const body = `
      <form onsubmit="event.preventDefault(); Community.submitAnswer('${postId}');">
        <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 14px;">
          Answering: <strong>${title}</strong>
        </p>
        <div class="form-group">
          <label class="form-label">Your Solution / Explanation</label>
          <textarea id="ans-text" class="form-control" rows="4" placeholder="Provide a clear technical explanation or code fix..." required></textarea>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-emerald">Post Answer</button>
        </div>
      </form>
    `;
    App.openModal('Answer Doubt', body);
  },

  async submitAnswer(postId) {
    const answerText = document.getElementById('ans-text').value;
    try {
      await API.post('/community/knowledge/answer', { postId, answerText });
      App.closeModal();
      App.showToast('Answer submitted!', 'success');
      Community.renderActiveTab();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  // ==================== DIRECT MESSAGING CHAT ====================

  async renderChat(container) {
    if (!Auth.isAuthenticated()) {
      container.innerHTML = `
        <div class="card" style="text-align: center; padding: 48px 24px;">
          <div style="font-size: 2.4rem; margin-bottom: 12px;">💬</div>
          <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 8px;">Sign in to chat with peers and mentor partners</h3>
          <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 20px; font-size: 0.9rem;">
            Communicate directly with swap partners, coordinate times, and share code links.
          </p>
          <div style="display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;">
            <button class="btn btn-primary" onclick="Auth.quickLogin('ananya@skillswap.edu', 'ananya123')">⚡ Demo Login as Ananya</button>
            <button class="btn btn-secondary" onclick="App.navigate('login')">Sign In with Other Account</button>
          </div>
        </div>
      `;
      return;
    }

    // Default chat to Rahul or Priya if none selected
    if (!this.activeChatPeerId) {
      this.activeChatPeerId = Auth.currentUser.id === 'user-1' ? 'user-2' : 'user-1';
      this.activeChatPeerName = Auth.currentUser.id === 'user-1' ? 'Rahul Verma' : 'Priya Sharma';
    }

    container.innerHTML = `
      <div style="display: grid; grid-template-columns: 260px 1fr; gap: 20px; height: 580px;">
        <!-- Left: Peers List -->
        <div class="card" style="padding: 16px; overflow-y: auto;">
          <h4 style="font-size: 0.8rem; text-transform: uppercase; color: var(--text-dim); margin-bottom: 12px;">Active Conversations</h4>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <div class="user-mini-card" style="cursor: pointer; ${this.activeChatPeerId === 'user-1' ? 'background: var(--bg-surface-3); border-color: var(--primary);' : ''}" onclick="Community.openChat('user-1', 'Priya Sharma')">
              <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=250&q=80" class="user-mini-avatar" alt="Priya"/>
              <div class="user-mini-info">
                <div class="user-mini-name">Priya Sharma</div>
                <div style="font-size: 0.72rem; color: var(--emerald);">React Architect</div>
              </div>
            </div>

            <div class="user-mini-card" style="cursor: pointer; ${this.activeChatPeerId === 'user-2' ? 'background: var(--bg-surface-3); border-color: var(--primary);' : ''}" onclick="Community.openChat('user-2', 'Rahul Verma')">
              <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=250&q=80" class="user-mini-avatar" alt="Rahul"/>
              <div class="user-mini-info">
                <div class="user-mini-name">Rahul Verma</div>
                <div style="font-size: 0.72rem; color: var(--primary);">ML Specialist</div>
              </div>
            </div>

            <div class="user-mini-card" style="cursor: pointer; ${this.activeChatPeerId === 'user-3' ? 'background: var(--bg-surface-3); border-color: var(--primary);' : ''}" onclick="Community.openChat('user-3', 'Ananya Patel')">
              <img src="https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=250&q=80" class="user-mini-avatar" alt="Ananya"/>
              <div class="user-mini-info">
                <div class="user-mini-name">Ananya Patel</div>
                <div style="font-size: 0.72rem; color: #f59e0b;">Java & DSA</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Right: Active Chat Messages Thread -->
        <div class="card" style="display: flex; flex-direction: column; padding: 0; overflow: hidden;">
          <div style="padding: 14px 20px; border-bottom: 1px solid var(--border-subtle); background: var(--bg-surface-2); display: flex; justify-content: space-between; align-items: center;">
            <div style="font-weight: 700; color: #fff;">Chat with ${this.activeChatPeerName}</div>
            <button class="btn btn-primary btn-sm" onclick="Sessions.openRequestModal('${this.activeChatPeerId}')">Book Session</button>
          </div>

          <div id="chat-messages-box" style="flex: 1; padding: 20px; overflow-y: auto; display: flex; flex-direction: column; gap: 12px;">
            <div style="color: var(--text-dim); text-align: center; margin: auto;">Loading message history...</div>
          </div>

          <form onsubmit="event.preventDefault(); Community.sendChatMessage();" style="padding: 14px 20px; border-top: 1px solid var(--border-subtle); display: flex; gap: 10px; background: var(--bg-surface-2);">
            <input type="text" id="chat-input" class="form-control" placeholder="Type message to ${this.activeChatPeerName}..." required autocomplete="off"/>
            <button type="submit" class="btn btn-primary">Send</button>
          </form>
        </div>
      </div>
    `;

    this.loadMessages();
  },

  openChat(peerId, peerName) {
    this.activeChatPeerId = peerId;
    this.activeChatPeerName = peerName;
    this.currentTab = 'chat';
    this.render();
  },

  async loadMessages() {
    const box = document.getElementById('chat-messages-box');
    if (!box) return;

    try {
      const msgs = await API.get('/community/message/conversation', { peerId: this.activeChatPeerId });

      if (msgs.length === 0) {
        box.innerHTML = `<div style="color: var(--text-dim); text-align: center; margin: auto;">No previous messages with ${this.activeChatPeerName}. Say hello to start swapping skills!</div>`;
        return;
      }

      box.innerHTML = msgs.map(m => {
        const isMe = m.senderId === Auth.currentUser.id;
        return `
          <div style="display: flex; justify-content: ${isMe ? 'flex-end' : 'flex-start'};">
            <div style="max-width: 70%; background: ${isMe ? 'var(--primary)' : 'var(--bg-surface-3)'}; color: #fff; padding: 10px 16px; border-radius: var(--radius-md); font-size: 0.9rem; line-height: 1.4;">
              <div>${m.content}</div>
              <div style="font-size: 0.7rem; color: rgba(255,255,255,0.6); text-align: right; margin-top: 4px;">
                ${new Date(m.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </div>
            </div>
          </div>
        `;
      }).join('');

      box.scrollTop = box.scrollHeight;
    } catch (err) {
      box.innerHTML = `<div style="color: var(--rose);">Error loading conversation.</div>`;
    }
  },

  async sendChatMessage() {
    const input = document.getElementById('chat-input');
    if (!input || !input.value.trim()) return;

    const content = input.value.trim();
    input.value = '';

    try {
      await API.post('/community/message/send', {
        receiverId: this.activeChatPeerId,
        content
      });
      Community.loadMessages();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  }
};

