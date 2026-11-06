/**
 * SkillSwap Wallet, Escrow & Simulated Payment Gateway
 * Credit ledger, simulated payment gateway for demo, and Pay It Forward pool
 */
const Wallet = {
  async render() {
    const container = document.getElementById('wallet-content-area');
    if (!container) return;

    if (!Auth.isAuthenticated()) {
      container.innerHTML = `
        <div class="card" style="text-align: center; padding: 48px 24px;">
          <div style="font-size: 2.4rem; margin-bottom: 12px;">💳</div>
          <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 8px;">Sign in to access your Credits & Escrow Ledger</h3>
          <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 20px; font-size: 0.9rem;">
            Inspect your available credit balance, escrow protection, simulated recharge gateway, and full transaction history.
          </p>
          <div style="display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;">
            <button class="btn btn-primary" onclick="Auth.quickLogin('priya@skillswap.edu', 'priya123')">⚡ Quick Login as Priya (240 Credits)</button>
            <button class="btn btn-secondary" onclick="App.navigate('login')">Sign In with Other Account</button>
          </div>
        </div>
      `;
      return;
    }

    try {
      const user = await API.get('/auth/me');
      Auth.currentUser = user;
      const ledger = await API.get('/credits/ledger');
      const pool = await API.get('/credits/pool');

      container.innerHTML = `
        <div class="page-header">
          <div>
            <h1 class="page-title">Wallet & Credit Ledger</h1>
            <p class="page-desc">Manage credits, transparent escrow holds, simulated payment recharge, and community pay-it-forward pool.</p>
          </div>
          <div style="display: flex; gap: 10px;">
            <button class="btn btn-secondary btn-sm" onclick="Wallet.openDonateModal()">Donate to Pool</button>
            <button class="btn btn-emerald btn-sm" onclick="Wallet.openRechargeModal()">+ Buy Credits (Demo Gateway)</button>
          </div>
        </div>

        <!-- Wallet Metric Cards Grid -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px; margin-bottom: 28px;">
          <!-- Available Balance -->
          <div class="card" style="background: radial-gradient(circle at top left, rgba(16, 185, 129, 0.15), transparent), var(--bg-surface-1); border: 1px solid rgba(16, 185, 129, 0.3);">
            <div style="font-size: 0.8rem; color: var(--emerald); font-weight: 700; text-transform: uppercase;">Available Spendable Credits</div>
            <div style="font-size: 2.2rem; font-weight: 800; color: #fff; margin: 8px 0;">${user.credits || 0} <span style="font-size: 1rem; color: #34d399;">CR</span></div>
            <div style="font-size: 0.82rem; color: var(--text-muted);">Spend on mentorship, mock interviews, or code reviews.</div>
          </div>

          <!-- Escrow Funds -->
          <div class="card" style="background: radial-gradient(circle at top left, rgba(99, 102, 241, 0.15), transparent), var(--bg-surface-1); border: 1px solid rgba(99, 102, 241, 0.3);">
            <div style="font-size: 0.8rem; color: #a5b4fc; font-weight: 700; text-transform: uppercase;">Locked in Escrow Guarantee</div>
            <div style="font-size: 2.2rem; font-weight: 800; color: #fff; margin: 8px 0;">${user.escrowCredits || 0} <span style="font-size: 1rem; color: #a5b4fc;">CR</span></div>
            <div style="font-size: 0.82rem; color: var(--text-muted);">Held securely for booked sessions until marked completed.</div>
          </div>

          <!-- Pay It Forward Pool -->
          <div class="card" style="background: radial-gradient(circle at top left, rgba(245, 158, 11, 0.15), transparent), var(--bg-surface-1); border: 1px solid rgba(245, 158, 11, 0.3);">
            <div style="font-size: 0.8rem; color: #fcd34d; font-weight: 700; text-transform: uppercase;">Pay It Forward Pool Balance</div>
            <div style="font-size: 2.2rem; font-weight: 800; color: #fff; margin: 8px 0;">${pool.poolBalance || 0} <span style="font-size: 1rem; color: #fcd34d;">CR</span></div>
            <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.82rem; margin-top: 4px;">
              <span style="color: var(--text-muted);">${pool.totalDonations || 0} community donations</span>
              <button class="btn btn-ghost btn-sm" style="padding: 0; color: var(--amber);" onclick="Wallet.openClaimModal()">Need Help? Claim 20 CR</button>
            </div>
          </div>
        </div>

        <!-- Full Transparent Credit Ledger Table -->
        <div class="card">
          <div class="card-title">
            <span>Transaction Ledger History</span>
            <span class="badge badge-primary">${ledger.length} Recorded Transactions</span>
          </div>

          <div style="overflow-x: auto; margin-top: 16px;">
            <table style="width: 100%; border-collapse: collapse; font-size: 0.88rem; text-align: left;">
              <thead>
                <tr style="border-bottom: 1px solid var(--border-subtle); color: var(--text-dim); font-size: 0.78rem; text-transform: uppercase;">
                  <th style="padding: 12px 14px;">Date & Time</th>
                  <th style="padding: 12px 14px;">Type</th>
                  <th style="padding: 12px 14px;">Description</th>
                  <th style="padding: 12px 14px;">Reference ID</th>
                  <th style="padding: 12px 14px; text-align: right;">Amount</th>
                  <th style="padding: 12px 14px; text-align: right;">Balance After</th>
                </tr>
              </thead>
              <tbody>
                ${ledger.length > 0 ? ledger.map(t => {
                  const isPositive = t.amount > 0;
                  const typeColors = {
                    'SIGNUP_BONUS': 'badge-emerald',
                    'PURCHASE': 'badge-emerald',
                    'TEACHING_EARNING': 'badge-emerald',
                    'ESCROW_RELEASE': 'badge-primary',
                    'ESCROW_REFUND': 'badge-emerald',
                    'ESCROW_HOLD': 'badge-amber',
                    'DONATION': 'badge-rose',
                    'POOL_GRANT': 'badge-emerald',
                    'QUIZ_VERIFICATION_BONUS': 'badge-emerald'
                  };

                  return `
                    <tr style="border-bottom: 1px solid var(--border-subtle); transition: background 0.15s;">
                      <td style="padding: 12px 14px; color: var(--text-muted); font-size: 0.82rem;">${new Date(t.createdAt).toLocaleString()}</td>
                      <td style="padding: 12px 14px;">
                        <span class="badge ${typeColors[t.transactionType] || 'badge-primary'}">${t.transactionType}</span>
                      </td>
                      <td style="padding: 12px 14px; font-weight: 500;">${t.description}</td>
                      <td style="padding: 12px 14px; font-family: var(--font-mono); font-size: 0.78rem; color: var(--text-dim);">${t.referenceId || '-'}</td>
                      <td style="padding: 12px 14px; text-align: right; font-weight: 700; color: ${isPositive ? 'var(--emerald)' : (t.amount === 0 ? 'var(--text-dim)' : 'var(--rose)')};">
                        ${isPositive ? '+' : ''}${t.amount} CR
                      </td>
                      <td style="padding: 12px 14px; text-align: right; font-weight: 600;">${t.balanceAfter} CR</td>
                    </tr>
                  `;
                }).join('') : `
                  <tr>
                    <td colspan="6" style="text-align: center; color: var(--text-dim); padding: 32px;">No transactions recorded yet.</td>
                  </tr>
                `}
              </tbody>
            </table>
          </div>
        </div>
      `;
    } catch (err) {
      container.innerHTML = `<div class="card" style="color: var(--rose);">Error loading wallet: ${err.message}</div>`;
    }
  },

  openRechargeModal() {
    const body = `
      <form onsubmit="event.preventDefault(); Wallet.executeRecharge();">
        <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 18px;">
          Simulated college demonstration payment gateway. Recharge demo credits instantly to book teachers and mock interviews.
        </p>

        <div class="form-group">
          <label class="form-label">Select Credit Package</label>
          <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 10px; margin-bottom: 12px;">
            <div class="card" style="padding: 14px; text-align: center; cursor: pointer; border: 1.5px solid var(--border-subtle);" onclick="Wallet.selectPackage(50, 5, this)">
              <div style="font-weight: 800; font-size: 1.2rem;">50 CR</div>
              <div style="font-size: 0.8rem; color: var(--emerald);">$5.00 Demo</div>
            </div>
            <div class="card" style="padding: 14px; text-align: center; cursor: pointer; border: 1.5px solid var(--primary); background: rgba(99, 102, 241, 0.1);" onclick="Wallet.selectPackage(100, 10, this)">
              <div style="font-weight: 800; font-size: 1.2rem; color: #a5b4fc;">100 CR</div>
              <div style="font-size: 0.8rem; color: var(--emerald);">$10.00 Demo</div>
            </div>
            <div class="card" style="padding: 14px; text-align: center; cursor: pointer; border: 1.5px solid var(--border-subtle);" onclick="Wallet.selectPackage(250, 20, this)">
              <div style="font-weight: 800; font-size: 1.2rem;">250 CR</div>
              <div style="font-size: 0.8rem; color: var(--emerald);">$20.00 Demo</div>
            </div>
          </div>
          <input type="hidden" id="recharge-amount" value="100"/>
        </div>

        <div class="form-group">
          <label class="form-label">Simulated Payment Method</label>
          <select id="recharge-method" class="form-control">
            <option value="UPI">Google Pay / PhonePe / UPI (Instant)</option>
            <option value="CARD">Campus Debit / Credit Card (Demo Sandbox)</option>
            <option value="NETBANKING">University Student NetBanking</option>
          </select>
        </div>

        <div style="background: rgba(99, 102, 241, 0.1); border-radius: var(--radius-md); padding: 12px; margin-bottom: 20px; font-size: 0.82rem; color: #cbd5e1;">
          ✓ Instant wallet crediting with SHA-256 transaction hash generation.
        </div>

        <div style="display: flex; justify-content: flex-end; gap: 10px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-emerald">Recharge Credits Now</button>
        </div>
      </form>
    `;
    App.openModal('Buy Credits (Simulated Gateway)', body);
  },

  selectPackage(cr, price, el) {
    document.getElementById('recharge-amount').value = cr;
    el.parentElement.querySelectorAll('.card').forEach(c => {
      c.style.border = '1.5px solid var(--border-subtle)';
      c.style.background = 'var(--bg-surface-1)';
    });
    el.style.border = '1.5px solid var(--primary)';
    el.style.background = 'rgba(99, 102, 241, 0.1)';
  },

  async executeRecharge() {
    const amount = parseInt(document.getElementById('recharge-amount').value);
    const paymentMethod = document.getElementById('recharge-method').value;

    try {
      await API.post('/credits/buy', { amount, paymentMethod });
      App.closeModal();
      App.showToast(`Payment successful! ${amount} credits added to your wallet.`, 'success');
      Auth.init();
      Wallet.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openDonateModal() {
    const body = `
      <form onsubmit="event.preventDefault(); Wallet.executeDonate();">
        <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 16px;">
          The <strong>Pay It Forward</strong> pool supports campus learners with zero credits who urgently need doubt clearing or interview prep.
        </p>
        <div class="form-group">
          <label class="form-label">Donation Amount (Credits)</label>
          <input type="number" id="donate-amount" class="form-control" value="20" min="5" max="500" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Note / Encouragement for Peers</label>
          <input type="text" id="donate-note" class="form-control" placeholder="e.g. Happy to help junior batch!" value="Supporting campus peers"/>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Donate Credits</button>
        </div>
      </form>
    `;
    App.openModal('Pay It Forward: Donate Credits', body);
  },

  async executeDonate() {
    const amount = parseInt(document.getElementById('donate-amount').value);
    const note = document.getElementById('donate-note').value;
    try {
      await API.post('/credits/pool/donate', { amount, note });
      App.closeModal();
      App.showToast('Thank you for donating to the student learning pool!', 'success');
      Auth.init();
      Wallet.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openClaimModal() {
    const body = `
      <form onsubmit="event.preventDefault(); Wallet.executeClaim();">
        <p style="font-size: 0.88rem; color: var(--text-muted); margin-bottom: 16px;">
          Students needing credits can claim assistance from the community pool to book a doubt sprint or mentorship session.
        </p>
        <div class="form-group">
          <label class="form-label">Reason / Purpose for Learning</label>
          <textarea id="claim-reason" class="form-control" rows="3" placeholder="Explain what topic you need help with and why..." required></textarea>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-emerald">Claim 20 Community Credits</button>
        </div>
      </form>
    `;
    App.openModal('Claim Community Learning Assistance', body);
  },

  async executeClaim() {
    const reason = document.getElementById('claim-reason').value;
    try {
      await API.post('/credits/pool/claim', { amount: 20, reason });
      App.closeModal();
      App.showToast('Community grant approved! 20 credits deposited into your wallet.', 'success');
      Auth.init();
      Wallet.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  }
};

