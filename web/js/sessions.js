/**
 * SkillSwap Sessions & Video Exchange Manager
 * Handles polymorphic session lifecycles, escrow releases, video meeting rooms, and reviews
 */
const Sessions = {
  currentFilter: 'ALL',

  async render() {
    const container = document.getElementById('sessions-content-area');
    if (!container) return;

    if (!Auth.isAuthenticated()) {
      container.innerHTML = `
        <div class="card" style="text-align: center; padding: 48px 24px;">
          <div style="font-size: 2.4rem; margin-bottom: 12px;">📅</div>
          <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 8px;">Sign in to view your learning & teaching sessions</h3>
          <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 20px; font-size: 0.9rem;">
            Track learning sessions, join live WebRTC video rooms, and manage escrow payouts.
          </p>
          <div style="display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;">
            <button class="btn btn-primary" onclick="Auth.quickLogin('rahul@skillswap.edu', 'rahul123')">⚡ Quick Login as Rahul (View Active Doubt Sprint)</button>
            <button class="btn btn-secondary" onclick="App.navigate('login')">Sign In with Other Account</button>
          </div>
        </div>
      `;
      return;
    }

    container.innerHTML = `
      <div class="page-header">
        <div>
          <h1 class="page-title">Sessions & Meeting Rooms</h1>
          <p class="page-desc">Track booked learning sessions, join scheduled video rooms, and complete escrow payouts.</p>
        </div>
        <div style="display: flex; gap: 8px;">
          <button class="btn btn-secondary btn-sm" onclick="Sessions.openGroupClassCreateModal()">+ Host Group Masterclass</button>
          <button class="btn btn-primary btn-sm" onclick="App.navigate('explore')">+ Book New Session</button>
        </div>
      </div>

      <!-- Filters & Stats Bar -->
      <div style="display: flex; gap: 8px; margin-bottom: 24px; border-bottom: 1px solid var(--border-subtle); padding-bottom: 12px;">
        <button class="btn ${this.currentFilter === 'ALL' ? 'btn-primary' : 'btn-ghost'} btn-sm" onclick="Sessions.filter('ALL')">All Sessions</button>
        <button class="btn ${this.currentFilter === 'REQUESTED' ? 'btn-primary' : 'btn-ghost'} btn-sm" onclick="Sessions.filter('REQUESTED')">Pending Requests</button>
        <button class="btn ${this.currentFilter === 'ACCEPTED' ? 'btn-primary' : 'btn-ghost'} btn-sm" onclick="Sessions.filter('ACCEPTED')">Active / Scheduled</button>
        <button class="btn ${this.currentFilter === 'COMPLETED' ? 'btn-primary' : 'btn-ghost'} btn-sm" onclick="Sessions.filter('COMPLETED')">Completed History</button>
      </div>

      <div id="sessions-list-grid" style="display: flex; flex-direction: column; gap: 16px;">
        <div style="color: var(--text-dim); padding: 20px;">Loading sessions...</div>
      </div>
    `;

    this.loadSessions();
  },

  filter(f) {
    this.currentFilter = f;
    this.render();
  },

  async loadSessions() {
    const grid = document.getElementById('sessions-list-grid');
    if (!grid) return;

    try {
      const sessions = await API.get('/sessions/my');
      const filtered = this.currentFilter === 'ALL' ? sessions : sessions.filter(s => s.status === this.currentFilter);

      if (filtered.length === 0) {
        grid.innerHTML = `<div class="card" style="text-align: center; color: var(--text-dim); padding: 48px;">No sessions found for this status.</div>`;
        return;
      }

      grid.innerHTML = filtered.map(s => {
        const isTeacher = Auth.currentUser.id === s.teacherId;
        const statusClass = s.status === 'ACCEPTED' ? 'badge-emerald' :
                           (s.status === 'REQUESTED' ? 'badge-amber' :
                           (s.status === 'COMPLETED' ? 'badge-primary' : 'badge-rose'));

        const typeLabels = {
          'DIRECT_SWAP': 'One-to-One Mentorship',
          'DOUBT_SPRINT': '⚡ 15-Min Doubt Sprint',
          'MOCK_INTERVIEW': '💼 Mock Interview Practice',
          'CODE_REVIEW': '🔍 Code & Architecture Review',
          'GROUP_CLASS': '👥 Group Masterclass',
          'BUILD_TOGETHER': '🚀 Build Together Milestone'
        };

        return `
          <div class="card" style="padding: 20px 24px;">
            <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px;">
              <div>
                <div style="display: flex; gap: 8px; align-items: center; margin-bottom: 4px;">
                  <span class="badge badge-primary">${typeLabels[s.sessionType] || s.sessionType}</span>
                  <span class="badge ${statusClass}">${s.status}</span>
                  <span style="font-size: 0.8rem; color: var(--emerald); font-weight: 700;">${s.creditAmount} Credits ${s.status === 'ACCEPTED' || s.status === 'REQUESTED' ? '(in Escrow)' : ''}</span>
                </div>
                <h3 style="font-size: 1.15rem; font-weight: 700;">${s.skillTopic}</h3>
              </div>
              <div style="font-size: 0.82rem; color: var(--text-muted); text-align: right;">
                <div>Scheduled: <strong>${s.scheduledTime || 'Flexible'}</strong></div>
                <div>Duration: ${s.durationMinutes} Mins</div>
              </div>
            </div>

            <p style="font-size: 0.88rem; color: #cbd5e1; margin-bottom: 16px; line-height: 1.5;">
              ${s.description || 'No additional details.'}
            </p>

            ${s.sessionType === 'DOUBT_SPRINT' && s.errorSnippet ? `
              <div style="background: #020408; border: 1px solid var(--border-subtle); border-radius: var(--radius-sm); padding: 10px 14px; font-family: var(--font-mono); font-size: 0.78rem; color: #f43f5e; margin-bottom: 14px; overflow-x: auto;">
                ${s.errorSnippet}
              </div>
            ` : ''}

            ${s.sessionType === 'GROUP_CLASS' ? `
              <div style="font-size: 0.82rem; color: var(--cyan); margin-bottom: 14px;">
                Seats: ${s.bookedSeats || 0} / ${s.totalSeats || 10} booked • ${s.pricePerSeat} credits/seat
              </div>
            ` : ''}

            <!-- Actions Bar -->
            <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--border-subtle); padding-top: 14px;">
              <div style="font-size: 0.82rem; color: var(--text-dim);">
                Role: <strong>${isTeacher ? 'Teacher / Mentor' : 'Learner'}</strong>
              </div>

              <div style="display: flex; gap: 8px;">
                <!-- Requested State Actions -->
                ${s.status === 'REQUESTED' && isTeacher ? `
                  <button class="btn btn-emerald btn-sm" onclick="Sessions.acceptSession('${s.id}')">Accept Request</button>
                  <button class="btn btn-danger btn-sm" onclick="Sessions.rejectSession('${s.id}')">Decline</button>
                ` : ''}

                ${s.status === 'REQUESTED' && !isTeacher ? `
                  <button class="btn btn-ghost btn-sm" onclick="Sessions.cancelSession('${s.id}')">Cancel Request</button>
                ` : ''}

                <!-- Accepted State Actions -->
                ${s.status === 'ACCEPTED' ? `
                  <button class="btn btn-emerald btn-sm" onclick="Sessions.openVideoRoom('${s.id}', '${s.skillTopic}', '${s.meetingLink}')">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
                    Join Video Room
                  </button>
                  <button class="btn btn-primary btn-sm" onclick="Sessions.completeSession('${s.id}')">Mark Complete & Release Escrow</button>
                  <button class="btn btn-ghost btn-sm" onclick="Sessions.cancelSession('${s.id}')">Cancel</button>
                ` : ''}

                <!-- Completed State Actions -->
                ${s.status === 'COMPLETED' && !isTeacher ? `
                  <button class="btn btn-secondary btn-sm" onclick="Sessions.openReviewModal('${s.id}', '${s.skillTopic}')">
                    ★ Rate Teacher & Leave Review
                  </button>
                ` : ''}
              </div>
            </div>
          </div>
        `;
      }).join('');

    } catch (err) {
      grid.innerHTML = `<div class="card" style="color: var(--rose);">Error loading sessions: ${err.message}</div>`;
    }
  },

  openRequestModal(teacherId, defaultSkill = '', teacherName = '', creditCost = 15) {
    if (!Auth.isAuthenticated()) {
      App.showToast('Please sign in or select a demo account to book a session.', 'info');
      App.navigate('login');
      return;
    }

    const userCredits = Auth.currentUser ? (Auth.currentUser.credits || 0) : 0;
    const body = `
      <form id="booking-form" onsubmit="event.preventDefault(); Sessions.submitSessionRequest();">
        <input type="hidden" id="req-teacher-id" value="${teacherId}"/>

        <!-- Teacher / Mentor Info Strip -->
        <div style="background: var(--bg-surface-2); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 12px 16px; margin-bottom: 18px; display: flex; justify-content: space-between; align-items: center;">
          <div>
            <div style="font-size: 0.74rem; color: var(--text-dim); text-transform: uppercase; font-weight: 700;">Selected Mentor</div>
            <div style="font-size: 1rem; font-weight: 700; color: #fff;">${teacherName || 'Campus Mentor'}</div>
          </div>
          <div style="text-align: right;">
            <div style="font-size: 0.74rem; color: var(--text-dim); text-transform: uppercase; font-weight: 700;">Rate</div>
            <div style="font-size: 0.95rem; font-weight: 700; color: var(--emerald);" id="display-session-cost">${creditCost} Credits</div>
          </div>
        </div>

        <!-- Selected Skill -->
        <div class="form-group">
          <label class="form-label">Selected Skill to Learn <span style="color: var(--rose);">*</span></label>
          <input type="text" id="req-topic" class="form-control" value="${defaultSkill}" required placeholder="e.g. React, Java, DSA, Machine Learning, Spring Boot"/>
        </div>

        <!-- Session Format -->
        <div class="form-group">
          <label class="form-label">Session Format <span style="color: var(--rose);">*</span></label>
          <select id="req-session-type" class="form-control" onchange="Sessions.updateFormatFields()">
            <option value="DIRECT_SWAP">One-to-One Session (${creditCost} Credits)</option>
            <option value="GROUP_CLASS">Group Session (15 Credits)</option>
            <option value="DOUBT_SPRINT">⚡ 15-Minute Doubt Sprint (2 Credits)</option>
            <option value="MOCK_INTERVIEW">💼 Mock Interview Practice (20 Credits)</option>
            <option value="CODE_REVIEW">🔍 Code & Architecture Review (15 Credits)</option>
          </select>
        </div>

        <div id="dynamic-format-fields">
          <!-- Optional fields dynamically populated for specialized formats like Doubt Sprint or Code Review -->
        </div>

        <!-- Preferred Time Slot -->
        <div class="form-group">
          <label class="form-label">Preferred Time Slot <span style="color: var(--rose);">*</span></label>
          <input type="text" id="req-time" class="form-control" placeholder="e.g. Tomorrow at 5:00 PM or Saturday at 3:00 PM" required/>
        </div>

        <!-- Session Agenda / Questions -->
        <div class="form-group">
          <label class="form-label">Topics / Notes for the Session</label>
          <textarea id="req-desc" class="form-control" rows="2" placeholder="What specific topics or questions do you want to focus on?"></textarea>
        </div>

        <!-- Credit Balance & Cost Status -->
        <div style="background: rgba(13, 19, 34, 0.7); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 14px 16px; margin-bottom: 20px;">
          <div style="display: flex; justify-content: space-between; font-size: 0.88rem; margin-bottom: 6px;">
            <span style="color: var(--text-muted);">Your Available Credits:</span>
            <strong style="color: #60a5fa;">${userCredits} Credits</strong>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 0.88rem; margin-bottom: 8px;">
            <span style="color: var(--text-muted);">Session Credit Cost:</span>
            <strong style="color: var(--emerald);" id="summary-credit-cost">${creditCost} Credits</strong>
          </div>
          <div style="font-size: 0.78rem; color: #a7f3d0; border-top: 1px solid rgba(255, 255, 255, 0.08); padding-top: 8px;">
            🔒 <strong>Escrow Protected:</strong> Credits are deducted and safely held until the session is completed. No credits are awarded upfront. If cancelled or declined, credits are 100% refunded immediately.
          </div>
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 10px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary" id="btn-submit-booking">Book Session</button>
        </div>
      </form>
    `;
    App.openModal('Book Learning Session', body);
    Sessions.updateFormatFields();
  },

  updateFormatFields() {
    const format = document.getElementById('req-session-type').value;
    const dynamicDiv = document.getElementById('dynamic-format-fields');
    if (!dynamicDiv) return;

    if (format === 'DOUBT_SPRINT') {
      dynamicDiv.innerHTML = `
        <div class="form-group">
          <label class="form-label">Urgency Level</label>
          <select id="req-urgency" class="form-control">
            <option value="HIGH">🔥 Urgent (Assignment / Hackathon Deadlocked)</option>
            <option value="MEDIUM" selected>Medium</option>
            <option value="LOW">Low</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">Error Snippet / Stack Trace (Optional)</label>
          <textarea id="req-error-snippet" class="form-control" rows="2" placeholder="Paste runtime exception, stack trace, or buggy snippet..."></textarea>
        </div>
      `;
      const costEl = document.getElementById('summary-credit-cost');
      if (costEl) costEl.textContent = '2 Credits';
    } else if (format === 'MOCK_INTERVIEW') {
      dynamicDiv.innerHTML = `
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
          <div class="form-group">
            <label class="form-label">Target Role</label>
            <input type="text" id="req-target-role" class="form-control" placeholder="e.g. Frontend Engineer / SDE-1"/>
          </div>
          <div class="form-group">
            <label class="form-label">Target Company</label>
            <input type="text" id="req-target-company" class="form-control" placeholder="e.g. Startups / FAANG"/>
          </div>
        </div>
      `;
      const costEl = document.getElementById('summary-credit-cost');
      if (costEl) costEl.textContent = '20 Credits';
    } else if (format === 'CODE_REVIEW') {
      dynamicDiv.innerHTML = `
        <div class="form-group">
          <label class="form-label">GitHub Pull Request or Repo URL</label>
          <input type="url" id="req-repo-url" class="form-control" placeholder="https://github.com/user/repo/pull/1"/>
        </div>
      `;
      const costEl = document.getElementById('summary-credit-cost');
      if (costEl) costEl.textContent = '15 Credits';
    } else {
      dynamicDiv.innerHTML = '';
      const displayCost = document.getElementById('display-session-cost');
      const costEl = document.getElementById('summary-credit-cost');
      if (costEl && displayCost) costEl.textContent = displayCost.textContent;
    }
  },

  async submitSessionRequest() {
    const sessionType = document.getElementById('req-session-type').value;
    const teacherId = document.getElementById('req-teacher-id').value;
    const skillTopic = document.getElementById('req-topic').value;
    const description = document.getElementById('req-desc').value;
    const scheduledTime = document.getElementById('req-time').value;

    const data = {
      sessionType,
      teacherId,
      skillTopic,
      description,
      scheduledTime
    };

    if (sessionType === 'DOUBT_SPRINT') {
      data.urgencyLevel = document.getElementById('req-urgency') ? document.getElementById('req-urgency').value : 'HIGH';
      data.errorSnippet = document.getElementById('req-error-snippet') ? document.getElementById('req-error-snippet').value : '';
    } else if (sessionType === 'MOCK_INTERVIEW') {
      data.targetRole = document.getElementById('req-target-role') ? document.getElementById('req-target-role').value : '';
      data.targetCompany = document.getElementById('req-target-company') ? document.getElementById('req-target-company').value : '';
    } else if (sessionType === 'CODE_REVIEW') {
      data.pullRequestUrl = document.getElementById('req-repo-url') ? document.getElementById('req-repo-url').value : '';
    }

    try {
      await API.post('/sessions/request', data);
      App.closeModal();
      App.showToast('Session booked successfully! Credits held in escrow until completion.', 'success');
      await Auth.init(); // update escrow and credits pill
      App.navigate('sessions');
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async acceptSession(sessionId) {
    try {
      await API.post('/sessions/accept', { sessionId });
      App.showToast('Session accepted! Meeting link is now active.', 'success');
      Sessions.loadSessions();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async rejectSession(sessionId) {
    if (!confirm('Decline this session request? Escrow credits will be refunded to learner.')) return;
    try {
      await API.post('/sessions/reject', { sessionId });
      App.showToast('Session declined and credits refunded.', 'info');
      Sessions.loadSessions();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async cancelSession(sessionId) {
    if (!confirm('Cancel this session? Any held escrow credits will be refunded.')) return;
    try {
      await API.post('/sessions/cancel', { sessionId });
      App.showToast('Session cancelled.', 'info');
      Auth.init();
      Sessions.loadSessions();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async completeSession(sessionId) {
    if (!confirm('Confirm session completion? Held escrow credits will be released to the teacher.')) return;
    try {
      await API.post('/sessions/complete', { sessionId });
      App.showToast('Session marked complete! Payout released to teacher.', 'success');
      Auth.init();
      Sessions.loadSessions();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openVideoRoom(sessionId, topic, meetingLink) {
    const roomBody = `
      <div style="background: #040812; border-radius: var(--radius-lg); overflow: hidden;">
        <div style="background: #090e1a; padding: 14px 20px; border-bottom: 1px solid var(--border-subtle); display: flex; justify-content: space-between; align-items: center;">
          <div>
            <div style="font-weight: 700; font-size: 1.1rem; color: #fff;">${topic}</div>
            <div style="font-size: 0.78rem; color: var(--emerald); display: flex; align-items: center; gap: 6px;">
              <span style="display: inline-block; width: 8px; height: 8px; border-radius: 50%; background: var(--emerald); box-shadow: 0 0 8px var(--emerald);"></span>
              Live Peer Session Active • <span id="call-timer-display" style="font-family: var(--font-mono); color: #fff;">14:59</span>
            </div>
          </div>
          <div style="display: flex; gap: 8px;">
            <button class="btn btn-secondary btn-sm" onclick="Sessions.toggleVideoMode('${meetingLink}')">Toggle Mode (Jitsi / Studio)</button>
            <a href="${meetingLink}" target="_blank" class="btn btn-emerald btn-sm">Full Tab ↗</a>
          </div>
        </div>

        <div id="video-display-area" style="height: 480px; width: 100%; position: relative; background: #000;">
          <!-- Default Mode: Integrated Peer Video Studio Simulator -->
          <div id="studio-simulator-view" style="width: 100%; height: 100%; display: grid; grid-template-columns: 1fr 1fr; gap: 12px; padding: 14px; background: #070c18;">
            <!-- Teacher Camera Feed -->
            <div style="background: #0f172a; border-radius: var(--radius-md); position: relative; display: flex; flex-direction: column; align-items: center; justify-content: center; border: 1px solid rgba(255,255,255,0.08); overflow: hidden;">
              <img src="https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80" style="width: 100%; height: 100%; object-fit: cover; opacity: 0.85;" alt="Teacher Stream"/>
              <div style="position: absolute; bottom: 12px; left: 14px; background: rgba(0,0,0,0.6); padding: 4px 10px; border-radius: 4px; font-size: 0.8rem; color: #fff;">
                🎤 Peer Mentor (Teacher)
              </div>
            </div>

            <!-- Learner Camera Feed -->
            <div style="background: #0f172a; border-radius: var(--radius-md); position: relative; display: flex; flex-direction: column; align-items: center; justify-content: center; border: 1px solid rgba(255,255,255,0.08); overflow: hidden;">
              <img src="${Auth.currentUser.avatarUrl || 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80'}" style="width: 100%; height: 100%; object-fit: cover; opacity: 0.85;" alt="Learner Stream"/>
              <div style="position: absolute; bottom: 12px; left: 14px; background: rgba(0,0,0,0.6); padding: 4px 10px; border-radius: 4px; font-size: 0.8rem; color: #fff;">
                🎤 You (${Auth.currentUser.name})
              </div>
            </div>
          </div>

          <!-- Jitsi Iframe Mode (Hidden by default, toggleable) -->
          <iframe id="jitsi-iframe-view" src="${meetingLink}"
                  allow="camera; microphone; fullscreen; display-capture; autoplay"
                  style="width: 100%; height: 100%; border: none; display: none;">
          </iframe>
        </div>

        <div style="padding: 12px 20px; background: #090e1a; display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--border-subtle); font-size: 0.85rem; color: var(--text-dim);">
          <div style="display: flex; gap: 8px;">
            <button class="btn btn-secondary btn-sm" onclick="App.showToast('Microphone toggled', 'info')">🎙️ Mute</button>
            <button class="btn btn-secondary btn-sm" onclick="App.showToast('Camera toggled', 'info')">📷 Video</button>
            <button class="btn btn-secondary btn-sm" onclick="App.showToast('Screen sharing started', 'info')">🖥️ Share Screen</button>
          </div>
          <button class="btn btn-primary btn-sm" onclick="App.closeModal(); Sessions.completeSession('${sessionId}')">
            End & Complete Session
          </button>
        </div>
      </div>
    `;
    App.openModal('Live Video Exchange Studio', roomBody);
  },

  toggleVideoMode(meetingLink) {
    const studio = document.getElementById('studio-simulator-view');
    const iframe = document.getElementById('jitsi-iframe-view');
    if (studio && iframe) {
      if (iframe.style.display === 'none') {
        iframe.style.display = 'block';
        studio.style.display = 'none';
        App.showToast('Switched to WebRTC Jitsi Meet iframe', 'info');
      } else {
        iframe.style.display = 'none';
        studio.style.display = 'grid';
        App.showToast('Switched to Peer Studio Simulator view', 'info');
      }
    }
  },

  openReviewModal(sessionId, topic) {
    const body = `
      <form onsubmit="event.preventDefault(); Sessions.submitReview('${sessionId}');">
        <h4 style="margin-bottom: 12px; font-size: 1.1rem;">Rate your session: ${topic}</h4>
        <div class="form-group">
          <label class="form-label">Star Rating (1 to 5 Stars)</label>
          <select id="rev-rating" class="form-control">
            <option value="5" selected>★★★★★ - Exceptional (5/5)</option>
            <option value="4">★★★★☆ - Great (4/5)</option>
            <option value="3">★★★☆☆ - Average (3/5)</option>
            <option value="2">★★☆☆☆ - Needs Improvement (2/5)</option>
            <option value="1">★☆☆☆☆ - Poor (1/5)</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">Detailed Review & Feedback</label>
          <textarea id="rev-text" class="form-control" rows="3" placeholder="Explain what went well, teacher's clarity, and unblocked topics..." required></textarea>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Submit Review</button>
        </div>
      </form>
    `;
    App.openModal('Leave Session Review', body);
  },

  async submitReview(sessionId) {
    const rating = parseInt(document.getElementById('rev-rating').value);
    const reviewText = document.getElementById('rev-text').value;

    try {
      await API.post('/sessions/review', { sessionId, rating, reviewText });
      App.closeModal();
      App.showToast('Thank you! Review recorded and teacher tier updated.', 'success');
      Sessions.loadSessions();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openGroupClassCreateModal() {
    const body = `
      <form onsubmit="event.preventDefault(); Sessions.createGroupClass();">
        <div class="form-group">
          <label class="form-label">Masterclass Topic</label>
          <input type="text" id="gc-topic" class="form-control" placeholder="e.g. Distributed Caching in Redis & System Design" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Description</label>
          <textarea id="gc-desc" class="form-control" rows="3" placeholder="Outline curriculum and topics covered..."></textarea>
        </div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
          <div class="form-group">
            <label class="form-label">Total Learner Seats</label>
            <input type="number" id="gc-seats" class="form-control" value="10" min="2" max="50"/>
          </div>
          <div class="form-group">
            <label class="form-label">Price per Seat (Credits)</label>
            <input type="number" id="gc-price" class="form-control" value="15" min="5" max="100"/>
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">Scheduled Date & Time</label>
          <input type="text" id="gc-time" class="form-control" placeholder="e.g. Saturday at 6:00 PM" required/>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-emerald">Publish Masterclass</button>
        </div>
      </form>
    `;
    App.openModal('Host a Group Masterclass', body);
  },

  async createGroupClass() {
    const data = {
      sessionType: 'GROUP_CLASS',
      teacherId: Auth.currentUser.id,
      skillTopic: document.getElementById('gc-topic').value,
      description: document.getElementById('gc-desc').value,
      totalSeats: parseInt(document.getElementById('gc-seats').value),
      pricePerSeat: parseInt(document.getElementById('gc-price').value),
      scheduledTime: document.getElementById('gc-time').value
    };

    try {
      await API.post('/sessions/request', data);
      App.closeModal();
      App.showToast('Group masterclass published!', 'success');
      Sessions.loadSessions();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  }
};

