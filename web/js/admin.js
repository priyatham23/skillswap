/**
 * SkillSwap Admin Panel
 * User moderation, certificate verification, and MySQL system telemetry
 */
const Admin = {
  async render() {
    const container = document.getElementById('admin-content-area');
    if (!container) return;

    if (!Auth.isAdmin()) {
      container.innerHTML = `
        <div class="card" style="text-align: center; padding: 48px 24px;">
          <div style="font-size: 2.4rem; margin-bottom: 12px;">🛡️</div>
          <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 8px;">Restricted Campus Administration Area</h3>
          <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 20px; font-size: 0.9rem;">
            Certificate review queues, user moderation, and platform telemetry are restricted to campus administrators.
          </p>
          <div style="display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;">
            <button class="btn btn-primary" onclick="Auth.quickLogin('admin@skillswap.edu', 'admin123')">⚡ 1-Click Login as Campus Admin (Dean)</button>
          </div>
        </div>
      `;
      return;
    }

    container.innerHTML = `
      <div class="page-header">
        <div>
          <h1 class="page-title">Campus Administration & Verification Panel</h1>
          <p class="page-desc">Approve pending credentials, moderate users, and monitor system telemetry & MySQL database connection.</p>
        </div>
        <div>
          <button class="btn btn-emerald btn-sm" onclick="Admin.testDatabase()">Sync / Reconnect MySQL</button>
        </div>
      </div>

      <!-- Telemetry Stats Cards -->
      <div id="admin-telemetry-grid" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-bottom: 28px;">
        <div style="color: var(--text-dim);">Loading telemetry...</div>
      </div>

      <!-- Pending Certificates for Verification Section -->
      <div class="card" style="margin-bottom: 28px;">
        <div class="card-title">
          <span>Pending Certificates & Proofs Awaiting Review</span>
          <span class="badge badge-amber" id="pending-certs-count">Review Queue</span>
        </div>
        <div id="pending-certs-table-box" style="margin-top: 16px; overflow-x: auto;">
          <div style="color: var(--text-dim); padding: 20px;">Checking pending submissions...</div>
        </div>
      </div>

      <!-- User Moderation Table -->
      <div class="card">
        <div class="card-title">
          <span>User Management & Moderation</span>
        </div>
        <div id="admin-users-table-box" style="margin-top: 16px; overflow-x: auto;">
          <div style="color: var(--text-dim); padding: 20px;">Loading campus directory...</div>
        </div>
      </div>
    `;

    this.loadStats();
    this.loadPendingCertificates();
    this.loadUsers();
  },

  async loadStats() {
    const grid = document.getElementById('admin-telemetry-grid');
    if (!grid) return;

    try {
      const stats = await API.get('/admin/stats');
      grid.innerHTML = `
        <div class="card" style="padding: 18px;">
          <div style="font-size: 0.75rem; color: var(--text-dim); text-transform: uppercase;">Total Users</div>
          <div style="font-size: 1.8rem; font-weight: 800; color: #fff; margin-top: 4px;">${stats.totalUsers}</div>
        </div>
        <div class="card" style="padding: 18px;">
          <div style="font-size: 0.75rem; color: var(--emerald); text-transform: uppercase;">Active Teachers</div>
          <div style="font-size: 1.8rem; font-weight: 800; color: #34d399; margin-top: 4px;">${stats.activeTeachers}</div>
        </div>
        <div class="card" style="padding: 18px;">
          <div style="font-size: 0.75rem; color: #a5b4fc; text-transform: uppercase;">Circulating Credits</div>
          <div style="font-size: 1.8rem; font-weight: 800; color: #a5b4fc; margin-top: 4px;">${stats.totalCirculatingCredits}</div>
        </div>
        <div class="card" style="padding: 18px;">
          <div style="font-size: 0.75rem; color: var(--amber); text-transform: uppercase;">Held in Escrow</div>
          <div style="font-size: 1.8rem; font-weight: 800; color: #fbbf24; margin-top: 4px;">${stats.totalEscrowHeld}</div>
        </div>
        <div class="card" style="padding: 18px;">
          <div style="font-size: 0.75rem; color: var(--cyan); text-transform: uppercase;">Completed Swaps</div>
          <div style="font-size: 1.8rem; font-weight: 800; color: #22d3ee; margin-top: 4px;">${stats.completedSessions}</div>
        </div>
        <div class="card" style="padding: 18px; border: 1px solid ${stats.mySqlConnected ? 'rgba(16, 185, 129, 0.4)' : 'rgba(244, 63, 94, 0.4)'};">
          <div style="font-size: 0.75rem; text-transform: uppercase; color: ${stats.mySqlConnected ? 'var(--emerald)' : 'var(--rose)'};">MySQL Database</div>
          <div style="font-size: 1rem; font-weight: 700; margin-top: 6px; color: #fff;">
            ${stats.mySqlConnected ? '● Online & Synced' : '○ Standby / Config Mode'}
          </div>
        </div>
      `;
    } catch (err) {
      grid.innerHTML = `<div style="color: var(--rose);">Error loading stats.</div>`;
    }
  },

  async loadPendingCertificates() {
    const box = document.getElementById('pending-certs-table-box');
    if (!box) return;

    try {
      const pending = await API.get('/admin/certificates');
      const countEl = document.getElementById('pending-certs-count');
      if (countEl) countEl.textContent = `${pending.length} Pending`;

      if (pending.length === 0) {
        box.innerHTML = `<div style="text-align: center; color: var(--text-dim); padding: 32px;">No certificates pending review. All submissions up to date!</div>`;
        return;
      }

      box.innerHTML = `
        <table style="width: 100%; border-collapse: collapse; font-size: 0.88rem; text-align: left;">
          <thead>
            <tr style="border-bottom: 1px solid var(--border-subtle); color: var(--text-dim); font-size: 0.78rem; text-transform: uppercase;">
              <th style="padding: 10px 14px;">User</th>
              <th style="padding: 10px 14px;">Certificate Title</th>
              <th style="padding: 10px 14px;">Issuer & Date</th>
              <th style="padding: 10px 14px;">Credential Proof URL</th>
              <th style="padding: 10px 14px; text-align: right;">Action</th>
            </tr>
          </thead>
          <tbody>
            ${pending.map(c => `
              <tr style="border-bottom: 1px solid var(--border-subtle);">
                <td style="padding: 12px 14px; font-weight: 600;">${c.userName}</td>
                <td style="padding: 12px 14px;">${c.title}</td>
                <td style="padding: 12px 14px; color: var(--text-muted);">${c.issuingOrg} (${c.issueDate})</td>
                <td style="padding: 12px 14px;">
                  <a href="${c.credentialUrl}" target="_blank" style="color: var(--primary); text-decoration: underline;">Verify Credential ↗</a>
                </td>
                <td style="padding: 12px 14px; text-align: right;">
                  <div style="display: flex; justify-content: flex-end; gap: 8px;">
                    <button class="btn btn-emerald btn-sm" onclick="Admin.reviewCert('${c.id}', 'APPROVED')">Approve & Elevate Tier</button>
                    <button class="btn btn-danger btn-sm" onclick="Admin.reviewCert('${c.id}', 'REJECTED')">Reject</button>
                  </div>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      `;
    } catch (err) {
      box.innerHTML = `<div style="color: var(--rose);">Error loading certificates.</div>`;
    }
  },

  async reviewCert(certificateId, status) {
    const notes = prompt(`Enter optional review feedback for this certificate (${status}):`, status === 'APPROVED' ? 'Verified by campus admin.' : 'Insufficient credential proof.');
    if (notes === null) return;

    try {
      await API.post('/admin/certificates/review', { certificateId, status, adminNotes: notes });
      App.showToast(`Certificate ${status.toLowerCase()}!`, 'success');
      Admin.loadPendingCertificates();
      Admin.loadStats();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async loadUsers() {
    const box = document.getElementById('admin-users-table-box');
    if (!box) return;

    try {
      const users = await API.get('/admin/users');
      box.innerHTML = `
        <table style="width: 100%; border-collapse: collapse; font-size: 0.88rem; text-align: left;">
          <thead>
            <tr style="border-bottom: 1px solid var(--border-subtle); color: var(--text-dim); font-size: 0.78rem; text-transform: uppercase;">
              <th style="padding: 10px 14px;">User</th>
              <th style="padding: 10px 14px;">Email</th>
              <th style="padding: 10px 14px;">College</th>
              <th style="padding: 10px 14px;">Tier</th>
              <th style="padding: 10px 14px;">Credits</th>
              <th style="padding: 10px 14px;">Status</th>
              <th style="padding: 10px 14px; text-align: right;">Actions</th>
            </tr>
          </thead>
          <tbody>
            ${users.map(u => `
              <tr style="border-bottom: 1px solid var(--border-subtle);">
                <td style="padding: 12px 14px; font-weight: 600;">${u.name}</td>
                <td style="padding: 12px 14px; color: var(--text-muted);">${u.email}</td>
                <td style="padding: 12px 14px; color: var(--text-dim); font-size: 0.82rem;">${u.college || '-'}</td>
                <td style="padding: 12px 14px;">
                  <span class="badge ${u.tier === 'Pro' ? 'badge-amber' : 'badge-primary'}">${u.tier}</span>
                </td>
                <td style="padding: 12px 14px; font-weight: 700; color: var(--emerald);">${u.credits} CR</td>
                <td style="padding: 12px 14px;">
                  <span class="badge ${u.isBanned ? 'badge-rose' : 'badge-emerald'}">${u.isBanned ? 'Banned' : 'Active'}</span>
                </td>
                <td style="padding: 12px 14px; text-align: right;">
                  <div style="display: flex; justify-content: flex-end; gap: 8px;">
                    <button class="btn btn-ghost btn-sm" onclick="Profile.openPassportModal('${u.id}')">Passport</button>
                    ${u.role !== 'ADMIN' ? `
                      <button class="btn ${u.isBanned ? 'btn-emerald' : 'btn-danger'} btn-sm" onclick="Admin.toggleBan('${u.id}')">
                        ${u.isBanned ? 'Unban' : 'Suspend'}
                      </button>
                    ` : '<span style="font-size: 0.78rem; color: var(--text-dim); padding: 4px 8px;">Admin</span>'}
                  </div>
                </td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      `;
    } catch (err) {
      box.innerHTML = `<div style="color: var(--rose);">Error loading users.</div>`;
    }
  },

  async toggleBan(targetUserId) {
    if (!confirm('Are you sure you want to change this user\'s access status?')) return;
    try {
      await API.post('/admin/users/ban', { targetUserId });
      App.showToast('User access status updated.', 'success');
      Admin.loadUsers();
      Admin.loadStats();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async testDatabase() {
    try {
      const res = await API.post('/admin/db/reconnect');
      App.showToast(res.message, res.connected ? 'success' : 'info');
      Admin.loadStats();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  }
};

