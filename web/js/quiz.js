/**
 * SkillSwap Interactive Skill Verification & MCQ Testing Engine
 */
const Quiz = {
  currentSkill: null,
  questions: [],
  userAnswers: {},

  async render() {
    const container = document.getElementById('quiz-content-area');
    if (!container) return;

    if (!Auth.isAuthenticated()) {
      container.innerHTML = `
        <div class="card" style="text-align: center; padding: 48px 24px;">
          <div style="font-size: 2.4rem; margin-bottom: 12px;">🧪</div>
          <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 8px;">Sign in to take Skill Tests & earn Verified Badges</h3>
          <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 20px; font-size: 0.9rem;">
            Assess your proficiency in Java, React, Python, or DSA to earn official verified badges and 20 credit bonuses.
          </p>
          <div style="display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;">
            <button class="btn btn-primary" onclick="Auth.quickLogin('ananya@skillswap.edu', 'ananya123')">⚡ Quick Login as Ananya</button>
            <button class="btn btn-secondary" onclick="App.navigate('login')">Sign In with Other Account</button>
          </div>
        </div>
      `;
      return;
    }

    container.innerHTML = `
      <div class="page-header">
        <div>
          <h1 class="page-title">Skill Verification & Tests</h1>
          <p class="page-desc">Take interactive multiple-choice assessments. Pass (>= 70%) to unlock verified badges, earn 20 bonus credits, and elevate your Teacher Tier.</p>
        </div>
      </div>

      <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 20px; margin-bottom: 32px;">
        ${['Java', 'React', 'Python', 'DSA', 'SQL', 'System Design'].map(skill => `
          <div class="card card-hover hover-lift" style="cursor: pointer; text-align: center; padding: 24px;" onclick="Quiz.start('${skill}')">
            <div style="font-size: 2rem; margin-bottom: 10px;">
              ${skill === 'Java' ? '☕' : (skill === 'React' ? '⚛️' : (skill === 'Python' ? '🐍' : (skill === 'DSA' ? '📊' : '⚡')))}
            </div>
            <h3 style="font-size: 1.15rem; font-weight: 700; margin-bottom: 6px;">${skill} Assessment</h3>
            <p style="font-size: 0.82rem; color: var(--text-muted); margin-bottom: 16px;">4-5 Multiple Choice Questions • 70% to pass</p>
            <button class="btn btn-primary btn-sm" style="width: 100%;">Start Assessment</button>
          </div>
        `).join('')}
      </div>

      <div id="active-quiz-frame"></div>
    `;
  },

  async start(skillName) {
    this.currentSkill = skillName;
    this.userAnswers = {};

    const frame = document.getElementById('active-quiz-frame');
    if (!frame) return;

    frame.innerHTML = `<div class="card" style="text-align: center; color: var(--text-muted); padding: 40px;">Loading ${skillName} assessment questions...</div>`;
    frame.scrollIntoView({ behavior: 'smooth' });

    try {
      this.questions = await API.get('/quiz/questions', { skill: skillName });

      if (this.questions.length === 0) {
        frame.innerHTML = `<div class="card" style="color: var(--rose);">No questions found for ${skillName}.</div>`;
        return;
      }

      this.renderQuestionForm(frame);
    } catch (err) {
      frame.innerHTML = `<div class="card" style="color: var(--rose);">Error loading quiz: ${err.message}</div>`;
    }
  },

  renderQuestionForm(frame) {
    frame.innerHTML = `
      <div class="card" style="border: 1.5px solid var(--primary); padding: 32px;">
        <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-subtle); padding-bottom: 16px; margin-bottom: 24px;">
          <div>
            <span class="badge badge-primary">Assessment in Progress</span>
            <h2 style="font-size: 1.35rem; font-weight: 800; margin-top: 4px;">${this.currentSkill} Skill Certification</h2>
          </div>
          <span style="font-size: 0.88rem; color: var(--text-dim);">${this.questions.length} Questions</span>
        </div>

        <form id="quiz-runner-form" onsubmit="event.preventDefault(); Quiz.submit();">
          <div style="display: flex; flex-direction: column; gap: 28px;">
            ${this.questions.map((q, qIdx) => `
              <div style="background: var(--bg-surface-2); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 20px;">
                <div style="font-weight: 700; font-size: 1rem; margin-bottom: 14px; color: #fff;">
                  Q${qIdx + 1}. ${q.question}
                </div>
                <div style="display: flex; flex-direction: column; gap: 10px;">
                  ${q.options.map((opt, optIdx) => `
                    <label style="display: flex; align-items: center; gap: 12px; background: var(--bg-surface-1); border: 1px solid var(--border-subtle); padding: 10px 14px; border-radius: var(--radius-sm); cursor: pointer; transition: all 0.15s;">
                      <input type="radio" name="q_${q.id}" value="${optIdx}" onchange="Quiz.userAnswers['${q.id}'] = ${optIdx}" required/>
                      <span style="font-size: 0.9rem; color: #cbd5e1;">${opt}</span>
                    </label>
                  `).join('')}
                </div>
              </div>
            `).join('')}
          </div>

          <div style="display: flex; justify-content: flex-end; gap: 12px; margin-top: 28px;">
            <button type="button" class="btn btn-ghost" onclick="document.getElementById('active-quiz-frame').innerHTML = '';">Cancel</button>
            <button type="submit" class="btn btn-emerald btn-lg">Submit Assessment for Grading</button>
          </div>
        </form>
      </div>
    `;
  },

  async submit() {
    const frame = document.getElementById('active-quiz-frame');
    try {
      const result = await API.post('/quiz/submit', {
        skillName: this.currentSkill,
        answers: this.userAnswers
      });

      frame.innerHTML = `
        <div class="card" style="border: 2px solid ${result.passed ? 'var(--emerald)' : 'var(--rose)'}; padding: 36px; text-align: center;">
          <div style="font-size: 3rem; margin-bottom: 12px;">
            ${result.passed ? '🎉' : '📚'}
          </div>

          <h2 style="font-size: 1.8rem; font-weight: 800; color: #fff; margin-bottom: 8px;">
            ${result.passed ? 'Congratulations! You Passed!' : 'Assessment Not Passed'}
          </h2>

          <div style="font-size: 2.2rem; font-weight: 800; color: ${result.passed ? 'var(--emerald)' : 'var(--rose)'}; margin-bottom: 12px;">
            ${result.scorePercentage}%
          </div>

          <p style="font-size: 0.95rem; color: var(--text-muted); max-width: 580px; margin: 0 auto 24px;">
            ${result.passed ?
              `You answered ${result.correctCount} of ${result.totalQuestions} questions correctly! You have earned the <strong>Official Verified Badge in ${this.currentSkill}</strong>, elevated your ranking, and +20 bonus credits have been credited to your wallet!` :
              `You scored ${result.scorePercentage}%. 70% is required to earn the verified badge. You can review the explanations below and re-test anytime!`
            }
          </p>

          <!-- Explanations Breakdown -->
          <div style="text-align: left; margin: 24px 0; display: flex; flex-direction: column; gap: 12px;">
            <h4 style="font-size: 0.9rem; text-transform: uppercase; color: var(--text-dim);">Detailed Review & Explanations:</h4>
            ${result.review.map(r => `
              <div style="background: var(--bg-surface-2); border-left: 3px solid ${r.isCorrect ? 'var(--emerald)' : 'var(--rose)'}; padding: 14px 18px; border-radius: 4px;">
                <div style="font-weight: 600; font-size: 0.92rem; margin-bottom: 4px;">${r.question}</div>
                <div style="font-size: 0.82rem; color: ${r.isCorrect ? '#34d399' : '#fb7185'}; font-weight: 600;">
                  ${r.isCorrect ? '✓ Correct Answer' : '✗ Incorrect Answer'}
                </div>
                <div style="font-size: 0.82rem; color: var(--text-muted); margin-top: 4px;">
                  <strong>Explanation:</strong> ${r.explanation}
                </div>
              </div>
            `).join('')}
          </div>

          <div style="display: flex; justify-content: center; gap: 14px; margin-top: 24px;">
            <button class="btn btn-secondary" onclick="Profile.render(); App.navigate('profile');">View Updated Profile & Badges</button>
            <button class="btn btn-primary" onclick="Quiz.render()">Take Another Assessment</button>
          </div>
        </div>
      `;

      Auth.init(); // refresh wallet credits
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  }
};

