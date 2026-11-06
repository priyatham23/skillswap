/**
 * SkillSwap Main Application Controller
 * Router, Global Modals, Toast Notification Dispatcher & Quick Demo Switcher
 */
const App = {
  currentRoute: 'explore',

  async init() {
    await Auth.init();

    // Listen to hash changes for deep linking
    window.addEventListener('hashchange', () => this.handleHashChange());
    this.handleHashChange();

    // Setup modal backdrop close on click
    const backdrop = document.getElementById('global-modal-backdrop');
    if (backdrop) {
      backdrop.addEventListener('click', (e) => {
        if (e.target === backdrop) this.closeModal();
      });
    }

    // Load initial notification count if logged in
    this.checkNotifications();
    setInterval(() => this.checkNotifications(), 15000); // refresh every 15s
  },

  handleHashChange() {
    const hash = window.location.hash.replace('#', '') || 'explore';
    if (hash.startsWith('passport/')) {
      const uid = hash.split('/')[1];
      Profile.openPassportModal(uid);
      return;
    }
    this.navigate(hash, false);
  },

  navigate(route, updateHash = true) {
    this.currentRoute = route;
    if (updateHash) {
      window.location.hash = route;
    }

    // Update sidebar active indicators
    document.querySelectorAll('.nav-item').forEach(el => {
      el.classList.remove('active');
      if (el.dataset.route === route) {
        el.classList.add('active');
      }
    });

    // Hide all view panels
    document.querySelectorAll('.route-view').forEach(el => {
      el.style.display = 'none';
    });

    // Show active panel and trigger render
    const activeView = document.getElementById(`view-${route}`);
    if (activeView) {
      activeView.style.display = 'block';
    }

    switch (route) {
      case 'explore':
        Matching.render();
        break;
      case 'sessions':
        Sessions.render();
        break;
      case 'quiz':
        Quiz.render();
        break;
      case 'profile':
        Profile.render();
        break;
      case 'wallet':
        Wallet.render();
        break;
      case 'community':
        Community.render();
        break;
      case 'mentors':
        Matching.renderMentorsView();
        break;
      case 'roadmaps':
        Roadmaps.render();
        break;
      case 'admin':
        Admin.render();
        break;
      case 'login':
        this.renderLoginView();
        break;
      default:
        Matching.render();
    }

    window.scrollTo({ top: 0, behavior: 'smooth' });
  },

  renderLoginView() {
    const container = document.getElementById('login-content-area');
    if (!container) return;

    container.innerHTML = `
      <div style="max-width: 480px; margin: 40px auto;">
        <div class="card" style="padding: 36px;">
          <!-- 1-Click Fast Demo Login Switcher -->
          <div style="background: rgba(99, 102, 241, 0.1); border: 1px solid rgba(99, 102, 241, 0.3); border-radius: var(--radius-md); padding: 16px; margin-bottom: 24px;">
            <div style="font-size: 0.78rem; text-transform: uppercase; color: #a5b4fc; font-weight: 700; margin-bottom: 8px;">
              ⚡ 1-Click Fast Demo Login:
            </div>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px;">
              <button class="btn btn-secondary btn-sm" onclick="Auth.quickLogin('priya@skillswap.edu', 'priya123')">
                Priya (Pro React)
              </button>
              <button class="btn btn-secondary btn-sm" onclick="Auth.quickLogin('rahul@skillswap.edu', 'rahul123')">
                Rahul (ML Researcher)
              </button>
              <button class="btn btn-secondary btn-sm" onclick="Auth.quickLogin('ananya@skillswap.edu', 'ananya123')">
                Ananya (Java & DSA)
              </button>
              <button class="btn btn-primary btn-sm" onclick="Auth.quickLogin('admin@skillswap.edu', 'admin123')">
                Dean / Admin Panel
              </button>
            </div>
          </div>

          <div style="display: flex; gap: 8px; border-bottom: 1px solid var(--border-subtle); margin-bottom: 20px; padding-bottom: 8px;">
            <button id="auth-tab-btn-login" class="btn btn-ghost btn-sm" style="font-weight: 700; color: #fff;" onclick="App.toggleAuthMode('login')">Sign In</button>
            <button id="auth-tab-btn-register" class="btn btn-ghost btn-sm" style="color: var(--text-dim);" onclick="App.toggleAuthMode('register')">Register Student Account</button>
          </div>

          <!-- Sign In Form -->
          <form id="auth-form-login" onsubmit="event.preventDefault(); App.handleManualLogin();">
            <div class="form-group">
              <label class="form-label">Campus Email</label>
              <input type="email" id="login-email" class="form-control" placeholder="name@college.edu" required/>
            </div>
            <div class="form-group">
              <label class="form-label">Password</label>
              <input type="password" id="login-pwd" class="form-control" placeholder="••••••••" required/>
            </div>
            <button type="submit" class="btn btn-primary btn-lg" style="width: 100%; margin-top: 10px;">Sign In to Campus</button>
          </form>

          <!-- Register Form -->
          <form id="auth-form-register" style="display: none;" onsubmit="event.preventDefault(); App.handleManualRegister();">
            <div class="form-group">
              <label class="form-label">Full Name</label>
              <input type="text" id="reg-name" class="form-control" placeholder="e.g. Priyatham Kumar" required/>
            </div>
            <div class="form-group">
              <label class="form-label">Campus / College</label>
              <input type="text" id="reg-college" class="form-control" placeholder="e.g. IIT Bombay / University Campus" required/>
            </div>
            <div class="form-group">
              <label class="form-label">Campus Email</label>
              <input type="email" id="reg-email" class="form-control" placeholder="name@college.edu" required/>
            </div>
            <div class="form-group">
              <label class="form-label">Password</label>
              <input type="password" id="reg-pwd" class="form-control" placeholder="At least 6 characters" required/>
            </div>
            <div style="font-size: 0.8rem; color: var(--emerald); margin-bottom: 12px;">
              🎁 Includes 100 free Sign-Up Welcome Credits!
            </div>
            <button type="submit" class="btn btn-emerald btn-lg" style="width: 100%;">Create Account & Claim 100 CR</button>
          </form>
        </div>
      </div>
    `;
  },

  toggleAuthMode(mode) {
    const loginForm = document.getElementById('auth-form-login');
    const regForm = document.getElementById('auth-form-register');
    const tabLogin = document.getElementById('auth-tab-btn-login');
    const tabReg = document.getElementById('auth-tab-btn-register');

    if (mode === 'login') {
      loginForm.style.display = 'block';
      regForm.style.display = 'none';
      tabLogin.style.color = '#fff';
      tabReg.style.color = 'var(--text-dim)';
    } else {
      loginForm.style.display = 'none';
      regForm.style.display = 'block';
      tabLogin.style.color = 'var(--text-dim)';
      tabReg.style.color = '#fff';
    }
  },

  async handleManualLogin() {
    const email = document.getElementById('login-email').value;
    const pwd = document.getElementById('login-pwd').value;
    try {
      await Auth.login(email, pwd);
    } catch (err) {
      this.showToast(err.message, 'error');
    }
  },

  async handleManualRegister() {
    const name = document.getElementById('reg-name').value;
    const college = document.getElementById('reg-college').value;
    const email = document.getElementById('reg-email').value;
    const pwd = document.getElementById('reg-pwd').value;

    try {
      await Auth.register(name, email, pwd, college);
    } catch (err) {
      this.showToast(err.message, 'error');
    }
  },

  // Global Modals
  openModal(title, htmlContent) {
    const backdrop = document.getElementById('global-modal-backdrop');
    const titleEl = document.getElementById('global-modal-title');
    const bodyEl = document.getElementById('global-modal-body');

    if (titleEl) titleEl.textContent = title;
    if (bodyEl) bodyEl.innerHTML = htmlContent;
    if (backdrop) backdrop.classList.add('open');
  },

  closeModal() {
    const backdrop = document.getElementById('global-modal-backdrop');
    if (backdrop) backdrop.classList.remove('open');
  },

  // Toast Notification System
  showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
      <span>${type === 'success' ? '✓' : (type === 'error' ? '⚠' : 'ℹ')}</span>
      <div style="flex: 1;">${message}</div>
    `;

    container.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      toast.style.transition = 'all 0.25s ease';
      setTimeout(() => toast.remove(), 250);
    }, 4000);
  },

  async checkNotifications() {
    if (!Auth.isAuthenticated()) return;
    try {
      const notifs = await API.get('/community/notifications');
      const unread = notifs.filter(n => !n.isRead);
      const dot = document.getElementById('notif-badge-dot');
      if (dot) {
        dot.style.display = unread.length > 0 ? 'block' : 'none';
      }
    } catch (e) {}
  },

  async openNotifications() {
    if (!Auth.isAuthenticated()) {
      this.navigate('login');
      return;
    }
    try {
      const notifs = await API.get('/community/notifications');
      const body = `
        <div style="display: flex; flex-direction: column; gap: 10px; max-height: 480px; overflow-y: auto;">
          ${notifs.length > 0 ? notifs.map(n => `
            <div style="background: var(--bg-surface-2); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 12px 16px;">
              <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 4px;">
                <div style="font-weight: 700; font-size: 0.95rem; color: #fff;">${n.title}</div>
                <div style="font-size: 0.72rem; color: var(--text-dim);">${new Date(n.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</div>
              </div>
              <div style="font-size: 0.85rem; color: var(--text-muted);">${n.message}</div>
            </div>
          `).join('') : '<div style="color: var(--text-dim); text-align: center; padding: 32px;">No notifications yet.</div>'}
        </div>
      `;
      this.openModal('Campus Notifications', body);
    } catch (err) {
      this.showToast(err.message, 'error');
    }
  }
};

// Start application when DOM loads
window.addEventListener('DOMContentLoaded', () => App.init());

