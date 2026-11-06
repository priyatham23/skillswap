/**
 * SkillSwap Authentication Manager
 * Handles login, registration, quick demo user switching, and user session cache
 */
const Auth = {
  currentUser: null,

  async init() {
    const token = API.getToken();
    if (token) {
      try {
        this.currentUser = await API.get('/auth/me');
        this.updateHeaderUI();
      } catch (err) {
        console.warn('Session expired or invalid, logging out.');
        this.logout();
      }
    }
  },

  async login(email, password) {
    const res = await API.post('/auth/login', { email, password });
    API.setToken(res.token);
    this.currentUser = res.user;
    this.updateHeaderUI();
    App.showToast(`Welcome back, ${this.currentUser.name}!`, 'success');
    App.navigate('explore');
    return this.currentUser;
  },

  async register(name, email, password, college) {
    const res = await API.post('/auth/register', { name, email, password, college });
    API.setToken(res.token);
    this.currentUser = res.user;
    this.updateHeaderUI();
    App.showToast(`Account created! 100 Sign-Up Bonus Credits credited.`, 'success');
    App.navigate('profile');
    return this.currentUser;
  },

  async logout() {
    try {
      await API.post('/auth/logout');
    } catch (e) {}
    API.setToken(null);
    this.currentUser = null;
    this.updateHeaderUI();
    App.showToast('Logged out.', 'info');
    App.navigate('login');
  },

  // 1-Click Fast Login for Demo Accounts (Priya, Rahul, Ananya, Admin)
  async quickLogin(email, password) {
    return this.login(email, password);
  },

  isAuthenticated() {
    return !!this.currentUser;
  },

  isAdmin() {
    return this.currentUser && this.currentUser.role === 'ADMIN';
  },

  updateHeaderUI() {
    const userBox = document.getElementById('sidebar-user-box');
    const creditAmountEl = document.getElementById('nav-credit-amount');
    const escrowAmountEl = document.getElementById('nav-escrow-amount');
    const authNavEl = document.getElementById('auth-nav-items');
    const guestNavEl = document.getElementById('guest-nav-items');
    const adminTab = document.getElementById('nav-tab-admin');
    const adminNavLabel = document.getElementById('admin-nav-label');

    if (this.currentUser) {
      if (userBox) {
        userBox.innerHTML = `
          <div class="user-mini-card" onclick="App.navigate('profile')">
            <img src="${this.currentUser.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80'}" class="user-mini-avatar" alt="Avatar"/>
            <div class="user-mini-info">
              <div class="user-mini-name">${this.currentUser.name}</div>
              <div class="user-mini-tier">
                <span class="badge ${this.currentUser.tier === 'Pro' ? 'badge-amber' : 'badge-primary'}">${this.currentUser.tier} Tier</span>
              </div>
            </div>
            <button class="btn btn-ghost btn-sm" onclick="event.stopPropagation(); Auth.logout();" title="Logout">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
            </button>
          </div>
        `;
      }

      if (creditAmountEl) creditAmountEl.textContent = `${this.currentUser.credits || 0} Credits`;
      if (escrowAmountEl) escrowAmountEl.textContent = `${this.currentUser.escrowCredits || 0} in Escrow`;

      if (authNavEl) authNavEl.style.display = 'block';
      if (guestNavEl) guestNavEl.style.display = 'none';
      if (adminTab) adminTab.style.display = this.isAdmin() ? 'flex' : 'none';
      if (adminNavLabel) adminNavLabel.style.display = this.isAdmin() ? 'block' : 'none';

    } else {
      if (userBox) {
        userBox.innerHTML = `
          <button class="btn btn-primary btn-sm" style="width: 100%;" onclick="App.navigate('login')">
            Log In / Sign Up
          </button>
        `;
      }
      if (creditAmountEl) creditAmountEl.textContent = '0 Credits';
      if (escrowAmountEl) escrowAmountEl.textContent = '0 in Escrow';
      if (authNavEl) authNavEl.style.display = 'none';
      if (guestNavEl) guestNavEl.style.display = 'block';
      if (adminTab) adminTab.style.display = 'none';
      if (adminNavLabel) adminNavLabel.style.display = 'none';
    }
  }
};

