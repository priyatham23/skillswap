/**
 * SkillSwap Learning Roadmaps
 * Curated career progression paths (e.g. Full-Stack in 3 Months, ML Engineer)
 */
const Roadmaps = {
  async render() {
    const container = document.getElementById('roadmaps-content-area');
    if (!container) return;

    container.innerHTML = `
      <div class="page-header">
        <div>
          <h1 class="page-title">Learning Roadmaps & Career Tracks</h1>
          <p class="page-desc">Structured step-by-step milestones with vetted resources and 1-click peer mentor discovery per stage.</p>
        </div>
      </div>

      <div id="roadmaps-list-container" style="display: flex; flex-direction: column; gap: 32px;">
        <div style="color: var(--text-dim); padding: 20px;">Loading roadmaps...</div>
      </div>
    `;

    try {
      const roadmaps = await API.get('/community/roadmaps');
      const listDiv = document.getElementById('roadmaps-list-container');
      if (!listDiv) return;

      listDiv.innerHTML = roadmaps.map(r => `
        <div class="card" style="padding: 28px;">
          <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; border-bottom: 1px solid var(--border-subtle); padding-bottom: 16px;">
            <div>
              <span class="badge badge-emerald" style="font-size: 0.75rem; margin-bottom: 6px;">${r.duration} • ${r.role}</span>
              <h2 style="font-size: 1.4rem; font-weight: 800; color: #fff;">${r.title}</h2>
              <p style="font-size: 0.9rem; color: var(--text-muted); margin-top: 4px;">${r.description}</p>
            </div>
          </div>

          <!-- Milestones Timeline -->
          <div style="display: flex; flex-direction: column; gap: 16px;">
            ${r.steps.map((s, idx) => `
              <div style="background: var(--bg-surface-2); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 18px 22px;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                  <div style="font-weight: 700; font-size: 1.05rem; color: #fff;">
                    Stage ${idx + 1}: ${s.title}
                  </div>
                  <button class="btn btn-emerald btn-sm" onclick="Roadmaps.findMentors('${s.skillToSwap}')">
                    Find ${s.skillToSwap} Mentors ➔
                  </button>
                </div>

                <p style="font-size: 0.88rem; color: #cbd5e1; margin-bottom: 12px; line-height: 1.5;">
                  ${s.description}
                </p>

                <div style="display: flex; gap: 12px; flex-wrap: wrap; font-size: 0.8rem; color: var(--text-dim);">
                  <span style="font-weight: 600; color: var(--text-muted);">Recommended Curated Resources:</span>
                  ${s.resources.map(res => `<span class="badge badge-primary">${res}</span>`).join('')}
                </div>
              </div>
            `).join('')}
          </div>
        </div>
      `).join('');
    } catch (err) {
      container.innerHTML = `<div class="card" style="color: var(--rose);">Error loading roadmaps: ${err.message}</div>`;
    }
  },

  findMentors(skill) {
    App.navigate('explore');
    setTimeout(() => {
      const input = document.getElementById('teacher-search-query');
      if (input) {
        input.value = skill;
        Matching.filterTeachers();
      }
    }, 150);
  }
};

