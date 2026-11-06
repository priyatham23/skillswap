/**
 * SkillSwap Profile Management & LinkedIn-style Showcase
 */
const Profile = {
  async render() {
    const container = document.getElementById('profile-content-area');
    if (!container) return;

    if (!Auth.isAuthenticated()) {
      container.innerHTML = `
        <div class="card" style="text-align: center; padding: 48px 24px;">
          <div style="font-size: 2.4rem; margin-bottom: 12px;">👤</div>
          <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 8px;">Sign in to view and customize your Profile</h3>
          <p style="color: var(--text-muted); max-width: 500px; margin: 0 auto 20px; font-size: 0.9rem;">
            Showcase your skills to teach, GitHub projects, verified certificates, and public Skill Passport.
          </p>
          <div style="display: flex; justify-content: center; gap: 10px; flex-wrap: wrap;">
            <button class="btn btn-primary" onclick="Auth.quickLogin('priya@skillswap.edu', 'priya123')">⚡ Demo Login as Priya (Pro React)</button>
            <button class="btn btn-secondary" onclick="App.navigate('login')">Sign In with Other Account</button>
          </div>
        </div>
      `;
      return;
    }

    try {
      const user = await API.get('/profile', { userId: Auth.currentUser.id });
      Auth.currentUser = user; // refresh local copy

      const tierClass = user.tier === 'Pro' ? 'tier-pro' : (user.tier === 'Intermediate' ? 'tier-intermediate' : 'tier-beginner');

      container.innerHTML = `
        <!-- LinkedIn-style Profile Header Banner -->
        <div class="card" style="padding: 0; overflow: hidden; margin-bottom: 24px;">
          <div style="height: 180px; background: linear-gradient(135deg, #1e1b4b, #312e81, #0f172a); position: relative;">
            <div style="position: absolute; top: 16px; right: 20px; display: flex; gap: 10px;">
              <button class="btn btn-secondary btn-sm" onclick="Profile.openPassportModal('${user.id}')">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path></svg>
                View Skill Passport
              </button>
              <button class="btn btn-secondary btn-sm" onclick="Profile.openEditModal()">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
                Edit Profile
              </button>
            </div>
          </div>

          <div style="padding: 0 32px 28px; position: relative;">
            <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-top: -60px; margin-bottom: 16px;">
              <img src="${user.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80'}"
                   style="width: 120px; height: 120px; border-radius: 50%; border: 4px solid var(--bg-surface-1); object-fit: cover; box-shadow: var(--shadow-lg);"
                   alt="${user.name}"/>
              <div style="display: flex; gap: 8px;">
                <span class="tier-badge ${tierClass}">${user.tier} Tier</span>
                <span class="badge badge-emerald">🔥 ${user.streakDays || 1}-Day Streak</span>
              </div>
            </div>

            <h2 style="font-size: 1.6rem; font-weight: 700; margin-bottom: 4px;">${user.name}</h2>
            <p style="font-size: 1rem; color: var(--text-muted); margin-bottom: 8px;">${user.headline || 'Campus Peer Mentor & Learner'}</p>
            <p style="font-size: 0.85rem; color: var(--text-dim); display: flex; align-items: center; gap: 6px; margin-bottom: 14px;">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 10v6M2 10l10-5 10 5-10 5z"></path><path d="M6 12v5c3 3 9 3 12 0v-5"></path></svg>
              ${user.college || 'Engineering University'}
            </p>

            <p style="font-size: 0.92rem; line-height: 1.6; color: #cbd5e1; max-width: 800px; margin-bottom: 16px;">
              ${user.bio || 'Add your bio to let peers know your learning interests and mentorship goals.'}
            </p>

            <!-- Social Links Strip -->
            <div style="display: flex; gap: 14px; flex-wrap: wrap; border-top: 1px solid var(--border-subtle); padding-top: 14px;">
              ${user.linkedinUrl ? `
                <a href="${user.linkedinUrl}" target="_blank" class="badge badge-primary" style="padding: 6px 12px; font-size: 0.82rem;">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M19 0h-14c-2.761 0-5 2.239-5 5v14c0 2.761 2.239 5 5 5h14c2.762 0 5-2.239 5-5v-14c0-2.761-2.238-5-5-5zm-11 19h-3v-11h3v11zm-1.5-12.268c-.966 0-1.75-.79-1.75-1.764s.784-1.764 1.75-1.764 1.75.79 1.75 1.764-.783 1.764-1.75 1.764zm13.5 12.268h-3v-5.604c0-3.368-4-3.113-4 0v5.604h-3v-11h3v1.765c1.396-2.586 7-2.777 7 2.476v6.759z"/></svg>
                  LinkedIn Profile
                </a>
              ` : `
                <button class="btn btn-ghost btn-sm" onclick="Profile.openEditModal()">+ Add LinkedIn Profile</button>
              `}

              ${user.githubUsername ? `
                <a href="https://github.com/${user.githubUsername}" target="_blank" class="badge badge-emerald" style="padding: 6px 12px; font-size: 0.82rem;">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/></svg>
                  github.com/${user.githubUsername}
                </a>
              ` : `
                <button class="btn btn-ghost btn-sm" onclick="Profile.openGithubImportModal()">+ Connect & Import GitHub</button>
              `}
            </div>
          </div>
        </div>

        <!-- 2-Column Section: Skills & Education / Projects & Certificates -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">

          <!-- Left Column: Skills & Education -->
          <div>
            <!-- Skills Card -->
            <div class="card" style="margin-bottom: 24px;">
              <div class="card-title">
                <span>Skills Offered & Desired</span>
                <button class="btn btn-primary btn-sm" onclick="Profile.openAddSkillModal()">+ Add Skill</button>
              </div>

              <div style="margin-top: 14px;">
                <h4 style="font-size: 0.85rem; text-transform: uppercase; color: var(--emerald); margin-bottom: 10px;">I Can Teach:</h4>
                <div style="display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 18px;">
                  ${user.skills.filter(s => s.skillType === 'TEACH').map(s => `
                    <div class="badge ${s.isVerified ? 'badge-emerald' : 'badge-primary'}" style="padding: 6px 12px; font-size: 0.82rem; gap: 8px;">
                      <span>${s.skillName} (${s.proficiencyLevel})</span>
                      ${s.isVerified ? `<span title="Verified Skill Badge">✓ Verified (${s.quizScore}%)</span>` : `<button class="btn btn-ghost btn-sm" style="padding: 0; color: var(--amber);" onclick="Quiz.start('${s.skillName}')">Take Test</button>`}
                      <span style="cursor: pointer; opacity: 0.7;" onclick="Profile.deleteSkill('${s.id}')">×</span>
                    </div>
                  `).join('') || '<p style="color: var(--text-dim); font-size: 0.85rem;">No teaching skills added yet.</p>'}
                </div>

                <h4 style="font-size: 0.85rem; text-transform: uppercase; color: var(--primary); margin-bottom: 10px;">I Want to Learn:</h4>
                <div style="display: flex; flex-wrap: wrap; gap: 8px;">
                  ${user.skills.filter(s => s.skillType === 'LEARN').map(s => `
                    <div class="badge badge-amber" style="padding: 6px 12px; font-size: 0.82rem; gap: 8px;">
                      <span>${s.skillName} (${s.proficiencyLevel})</span>
                      <span style="cursor: pointer; opacity: 0.7;" onclick="Profile.deleteSkill('${s.id}')">×</span>
                    </div>
                  `).join('') || '<p style="color: var(--text-dim); font-size: 0.85rem;">No learning targets added yet.</p>'}
                </div>
              </div>
            </div>

            <!-- Education Card -->
            <div class="card">
              <div class="card-title">
                <span>Education History</span>
                <button class="btn btn-secondary btn-sm" onclick="Profile.openAddEduModal()">+ Add Education</button>
              </div>
              <div style="margin-top: 14px; display: flex; flex-direction: column; gap: 14px;">
                ${user.educations && user.educations.length > 0 ? user.educations.map(e => `
                  <div style="border-left: 2px solid var(--primary); padding-left: 14px;">
                    <div style="font-weight: 600; font-size: 0.95rem;">${e.school}</div>
                    <div style="color: var(--text-muted); font-size: 0.85rem;">${e.degree} in ${e.fieldOfStudy}</div>
                    <div style="color: var(--text-dim); font-size: 0.78rem;">${e.startYear} - ${e.endYear}</div>
                  </div>
                `).join('') : '<p style="color: var(--text-dim); font-size: 0.85rem;">No education entries added.</p>'}
              </div>
            </div>
          </div>

          <!-- Right Column: Projects & Certificates -->
          <div>
            <!-- Projects Card -->
            <div class="card" style="margin-bottom: 24px;">
              <div class="card-title">
                <span>Projects Portfolio</span>
                <div style="display: flex; gap: 6px;">
                  <button class="btn btn-emerald btn-sm" onclick="Profile.openGithubImportModal()">Import GitHub</button>
                  <button class="btn btn-secondary btn-sm" onclick="Profile.openAddProjectModal()">+ Manual</button>
                </div>
              </div>
              <div style="margin-top: 14px; display: flex; flex-direction: column; gap: 12px;">
                ${user.projects && user.projects.length > 0 ? user.projects.map(p => `
                  <div style="background: var(--bg-surface-2); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 14px;">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                      <div style="font-weight: 600; font-size: 0.95rem;">${p.title}</div>
                      ${p.isImported ? '<span class="badge badge-emerald">GitHub</span>' : ''}
                    </div>
                    <p style="font-size: 0.84rem; color: var(--text-muted); margin: 6px 0;">${p.description || 'No description provided.'}</p>
                    <div style="display: flex; justify-content: space-between; align-items: center; font-size: 0.78rem;">
                      <span style="color: var(--cyan);">${p.tags || 'Code'}</span>
                      ${p.githubRepoUrl ? `<a href="${p.githubRepoUrl}" target="_blank" style="color: var(--primary); text-decoration: underline;">View Repo →</a>` : ''}
                    </div>
                  </div>
                `).join('') : '<p style="color: var(--text-dim); font-size: 0.85rem;">No projects added yet.</p>'}
              </div>
            </div>

            <!-- Certificates & Verification Proofs Card -->
            <div class="card">
              <div class="card-title">
                <span>Certificates & Proofs</span>
                <button class="btn btn-secondary btn-sm" onclick="Profile.openSubmitCertModal()">+ Submit Proof</button>
              </div>
              <div style="margin-top: 14px; display: flex; flex-direction: column; gap: 12px;">
                ${user.certificates && user.certificates.length > 0 ? user.certificates.map(c => `
                  <div style="background: var(--bg-surface-2); border: 1px solid var(--border-subtle); border-radius: var(--radius-md); padding: 14px;">
                    <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                      <div style="font-weight: 600; font-size: 0.92rem;">${c.title}</div>
                      <span class="badge ${c.status === 'APPROVED' ? 'badge-emerald' : (c.status === 'REJECTED' ? 'badge-rose' : 'badge-amber')}">
                        ${c.status}
                      </span>
                    </div>
                    <div style="font-size: 0.82rem; color: var(--text-dim); margin-top: 4px;">Issuer: ${c.issuingOrg || 'Accredited Org'} • ${c.issueDate || ''}</div>
                    ${c.adminNotes ? `<div style="font-size: 0.78rem; color: #fcd34d; margin-top: 4px;">Admin Feedback: ${c.adminNotes}</div>` : ''}
                  </div>
                `).join('') : '<p style="color: var(--text-dim); font-size: 0.85rem;">No certificates submitted yet.</p>'}
              </div>
            </div>
          </div>

        </div>
      `;
    } catch (err) {
      container.innerHTML = `<div class="card" style="color: var(--rose);">Failed to load profile: ${err.message}</div>`;
    }
  },

  pendingAvatarData: null,
  pendingAvatarFileName: null,
  isAvatarRemoved: false,

  openEditModal() {
    this.pendingAvatarData = null;
    this.pendingAvatarFileName = null;
    this.isAvatarRemoved = false;
    const u = Auth.currentUser;
    const currentAvatar = u.avatarUrl || 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80';

    const body = `
      <form id="edit-profile-form" onsubmit="event.preventDefault(); Profile.saveProfile();">
        <!-- Interactive Native Avatar Upload Component -->
        <div style="display: flex; align-items: center; gap: 20px; background: var(--bg-surface-2); padding: 16px 20px; border-radius: var(--radius-md); border: 1px solid var(--border-subtle); margin-bottom: 20px;">
          <div style="position: relative; width: 84px; height: 84px; flex-shrink: 0;">
            <img id="edit-avatar-preview" src="${currentAvatar}" 
                 style="width: 84px; height: 84px; border-radius: 50%; object-fit: cover; border: 3px solid var(--primary); box-shadow: var(--shadow-md); display: block;" 
                 alt="${u.name}"/>
            <div onclick="document.getElementById('avatar-file-input').click()" 
                 title="Click to change photo"
                 style="position: absolute; bottom: 0; right: 0; background: var(--primary); color: #fff; width: 28px; height: 28px; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; border: 2px solid var(--bg-surface-2); box-shadow: var(--shadow-sm);">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
            </div>
          </div>

          <div style="flex: 1; min-width: 0;">
            <div style="font-weight: 700; font-size: 0.95rem; color: #fff; margin-bottom: 4px;">Profile Photo</div>
            <div style="font-size: 0.8rem; color: var(--text-dim); margin-bottom: 10px;">Select a photo from your gallery or desktop folder (JPG, PNG, WEBP).</div>
            
            <div style="display: flex; gap: 10px; flex-wrap: wrap;">
              <button type="button" class="btn btn-secondary btn-sm" onclick="document.getElementById('avatar-file-input').click()">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="17 8 12 3 7 8"></polyline><line x1="12" y1="3" x2="12" y2="15"></line></svg>
                Change Avatar
              </button>
              <button type="button" class="btn btn-ghost btn-sm" style="color: var(--rose);" onclick="Profile.removeAvatar()">
                Remove
              </button>
            </div>

            <!-- Native File Input supporting mobile Gallery / File Manager and Desktop local folders -->
            <input type="file" id="avatar-file-input" accept="image/jpeg,image/png,image/webp,image/jpg" style="display: none;" onchange="Profile.handleAvatarFileSelect(this)"/>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Full Name <span style="color: var(--rose);">*</span></label>
          <input type="text" id="prof-name" class="form-control" value="${u.name || ''}" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Headline</label>
          <input type="text" id="prof-headline" class="form-control" value="${u.headline || ''}" placeholder="e.g. Frontend Engineer | React & TypeScript"/>
        </div>
        <div class="form-group">
          <label class="form-label">College / University</label>
          <input type="text" id="prof-college" class="form-control" value="${u.college || ''}" placeholder="e.g. IIT Bombay / BITS Pilani"/>
        </div>
        <div class="form-group">
          <label class="form-label">Bio / Learning Interests</label>
          <textarea id="prof-bio" class="form-control" rows="3" placeholder="Tell peers what you're passionate about...">${u.bio || ''}</textarea>
        </div>
        <div class="form-group">
          <label class="form-label">LinkedIn Profile URL</label>
          <input type="url" id="prof-linkedin" class="form-control" value="${u.linkedinUrl || ''}" placeholder="https://linkedin.com/in/username"/>
        </div>
        <div class="form-group">
          <label class="form-label">GitHub Username</label>
          <input type="text" id="prof-github" class="form-control" value="${u.githubUsername || ''}" placeholder="username"/>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Save Changes</button>
        </div>
      </form>
    `;
    App.openModal('Edit Profile', body);
  },

  handleAvatarFileSelect(input) {
    if (!input.files || !input.files[0]) return;
    const file = input.files[0];

    // Validate image format: JPG, JPEG, PNG, WEBP
    const validMimes = ['image/jpeg', 'image/png', 'image/webp', 'image/jpg'];
    if (!validMimes.includes(file.type.toLowerCase())) {
      App.showToast('Please select a valid image file (JPG, PNG, or WEBP).', 'error');
      return;
    }

    // Limit to 5MB
    if (file.size > 5 * 1024 * 1024) {
      App.showToast('Image file size must be less than 5MB.', 'error');
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      this.pendingAvatarData = e.target.result;
      this.pendingAvatarFileName = file.name;
      this.isAvatarRemoved = false;
      const preview = document.getElementById('edit-avatar-preview');
      if (preview) {
        preview.src = e.target.result;
      }
      App.showToast('Image selected! Click "Save Changes" to save and apply.', 'info');
    };
    reader.readAsDataURL(file);
  },

  removeAvatar() {
    this.pendingAvatarData = null;
    this.pendingAvatarFileName = null;
    this.isAvatarRemoved = true;
    const preview = document.getElementById('edit-avatar-preview');
    const defaultPlaceholder = 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80';
    if (preview) {
      preview.src = defaultPlaceholder;
    }
    const fileInput = document.getElementById('avatar-file-input');
    if (fileInput) fileInput.value = '';
    App.showToast('Avatar reset to default. Click "Save Changes" to apply.', 'info');
  },

  async saveProfile() {
    try {
      let avatarUrl = Auth.currentUser.avatarUrl;

      // 1. If avatar changed via file picker, upload to server
      if (this.pendingAvatarData) {
        const uploadRes = await API.post('/profile/avatar', {
          imageData: this.pendingAvatarData,
          fileName: this.pendingAvatarFileName
        });
        if (uploadRes && uploadRes.avatarUrl) {
          avatarUrl = uploadRes.avatarUrl;
        }
      } else if (this.isAvatarRemoved) {
        avatarUrl = 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=250&q=80';
      }

      // 2. Save profile fields
      const data = {
        name: document.getElementById('prof-name').value,
        headline: document.getElementById('prof-headline').value,
        college: document.getElementById('prof-college').value,
        bio: document.getElementById('prof-bio').value,
        linkedinUrl: document.getElementById('prof-linkedin').value,
        githubUsername: document.getElementById('prof-github').value,
        avatarUrl: avatarUrl
      };

      const updatedUser = await API.put('/profile', data);
      Auth.currentUser = updatedUser;

      App.closeModal();
      App.showToast('Profile and avatar updated successfully!', 'success');
      Auth.updateHeaderUI();
      Profile.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openAddSkillModal() {
    const body = `
      <form onsubmit="event.preventDefault(); Profile.saveSkill();">
        <div class="form-group">
          <label class="form-label">Skill Name</label>
          <input type="text" id="skill-name-input" class="form-control" placeholder="e.g. Java, React, Python, Machine Learning, DSA" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Type</label>
          <select id="skill-type-input" class="form-control">
            <option value="TEACH">I Want to Teach (Earn Credits)</option>
            <option value="LEARN">I Want to Learn (Spend Credits)</option>
          </select>
        </div>
        <div class="form-group">
          <label class="form-label">Proficiency Level</label>
          <select id="skill-level-input" class="form-control">
            <option value="Beginner">Beginner</option>
            <option value="Intermediate" selected>Intermediate</option>
            <option value="Advanced">Advanced</option>
            <option value="Expert">Expert</option>
          </select>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Add Skill</button>
        </div>
      </form>
    `;
    App.openModal('Add Skill', body);
  },

  async saveSkill() {
    const skillName = document.getElementById('skill-name-input').value;
    const skillType = document.getElementById('skill-type-input').value;
    const proficiencyLevel = document.getElementById('skill-level-input').value;

    try {
      await API.post('/skills/add', { skillName, skillType, proficiencyLevel });
      App.closeModal();
      App.showToast(`Skill '${skillName}' added!`, 'success');
      Profile.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async deleteSkill(skillId) {
    if (!confirm('Remove this skill?')) return;
    try {
      await API.del('/skills/delete', { skillId });
      App.showToast('Skill removed.', 'info');
      Profile.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openAddEduModal() {
    const body = `
      <form onsubmit="event.preventDefault(); Profile.saveEdu();">
        <div class="form-group">
          <label class="form-label">School / College</label>
          <input type="text" id="edu-school" class="form-control" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Degree</label>
          <input type="text" id="edu-degree" class="form-control" placeholder="e.g. B.Tech / B.S." required/>
        </div>
        <div class="form-group">
          <label class="form-label">Field of Study</label>
          <input type="text" id="edu-field" class="form-control" placeholder="e.g. Computer Science" required/>
        </div>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
          <div class="form-group">
            <label class="form-label">Start Year</label>
            <input type="number" id="edu-start" class="form-control" value="2022"/>
          </div>
          <div class="form-group">
            <label class="form-label">End Year</label>
            <input type="number" id="edu-end" class="form-control" value="2026"/>
          </div>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Save Education</button>
        </div>
      </form>
    `;
    App.openModal('Add Education', body);
  },

  async saveEdu() {
    const data = {
      school: document.getElementById('edu-school').value,
      degree: document.getElementById('edu-degree').value,
      fieldOfStudy: document.getElementById('edu-field').value,
      startYear: parseInt(document.getElementById('edu-start').value),
      endYear: parseInt(document.getElementById('edu-end').value)
    };
    try {
      await API.post('/profile/education', data);
      App.closeModal();
      App.showToast('Education added!', 'success');
      Profile.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openAddProjectModal() {
    const body = `
      <form onsubmit="event.preventDefault(); Profile.saveProject();">
        <div class="form-group">
          <label class="form-label">Project Title</label>
          <input type="text" id="proj-title" class="form-control" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Description</label>
          <textarea id="proj-desc" class="form-control" rows="2" placeholder="Explain architecture and highlights..."></textarea>
        </div>
        <div class="form-group">
          <label class="form-label">GitHub Repo URL</label>
          <input type="url" id="proj-repo" class="form-control" placeholder="https://github.com/..."/>
        </div>
        <div class="form-group">
          <label class="form-label">Tech Stack Tags</label>
          <input type="text" id="proj-tags" class="form-control" placeholder="e.g. React, Java, Docker"/>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Save Project</button>
        </div>
      </form>
    `;
    App.openModal('Add Project', body);
  },

  async saveProject() {
    const data = {
      title: document.getElementById('proj-title').value,
      description: document.getElementById('proj-desc').value,
      githubRepoUrl: document.getElementById('proj-repo').value,
      tags: document.getElementById('proj-tags').value
    };
    try {
      await API.post('/profile/project', data);
      App.closeModal();
      App.showToast('Project added!', 'success');
      Profile.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openGithubImportModal() {
    const u = Auth.currentUser;
    const body = `
      <form onsubmit="event.preventDefault(); Profile.executeGithubImport();">
        <p style="font-size: 0.9rem; color: var(--text-muted); margin-bottom: 16px;">
          Enter your GitHub username to automatically fetch public repositories, stargazers count, and languages into your portfolio.
        </p>
        <div class="form-group">
          <label class="form-label">GitHub Username</label>
          <input type="text" id="import-github-user" class="form-control" value="${u.githubUsername || ''}" placeholder="e.g. torvalds" required/>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-emerald">Sync Repositories</button>
        </div>
      </form>
    `;
    App.openModal('Import Projects from GitHub', body);
  },

  async executeGithubImport() {
    const githubUsername = document.getElementById('import-github-user').value;
    try {
      const repos = await API.post('/profile/import-github', { githubUsername });
      App.closeModal();
      App.showToast(`Imported ${repos.length} repositories from GitHub!`, 'success');
      Profile.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  openSubmitCertModal() {
    const body = `
      <form onsubmit="event.preventDefault(); Profile.saveCertProof();">
        <div class="form-group">
          <label class="form-label">Certificate / Credential Title</label>
          <input type="text" id="cert-title" class="form-control" placeholder="e.g. AWS Certified Solutions Architect" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Issuing Organization</label>
          <input type="text" id="cert-org" class="form-control" placeholder="e.g. Amazon Web Services / Coursera / Oracle" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Issue Date</label>
          <input type="text" id="cert-date" class="form-control" placeholder="e.g. Jan 2025" required/>
        </div>
        <div class="form-group">
          <label class="form-label">Credential Verification URL</label>
          <input type="url" id="cert-url" class="form-control" placeholder="https://..." required/>
        </div>
        <div class="form-group">
          <label class="form-label">Proof Details / Description</label>
          <textarea id="cert-proof" class="form-control" rows="2" placeholder="Summary of course curriculum, grade, or verification code..."></textarea>
        </div>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px;">
          <button type="button" class="btn btn-ghost" onclick="App.closeModal()">Cancel</button>
          <button type="submit" class="btn btn-primary">Submit for Admin Approval</button>
        </div>
      </form>
    `;
    App.openModal('Submit Certificate & Proof', body);
  },

  async saveCertProof() {
    const data = {
      title: document.getElementById('cert-title').value,
      issuingOrg: document.getElementById('cert-org').value,
      issueDate: document.getElementById('cert-date').value,
      credentialUrl: document.getElementById('cert-url').value,
      proofDescription: document.getElementById('cert-proof').value
    };
    try {
      await API.post('/profile/certificate', data);
      App.closeModal();
      App.showToast('Certificate submitted for verification!', 'success');
      Profile.render();
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  },

  async openPassportModal(userId) {
    try {
      const p = await API.get('/profile/passport', { userId });
      const u = p.user;
      const tierBadge = p.tier === 'Pro' ? 'tier-pro' : (p.tier === 'Intermediate' ? 'tier-intermediate' : 'tier-beginner');

      const body = `
        <div style="background: radial-gradient(circle at top right, rgba(99, 102, 241, 0.15), transparent), var(--bg-surface-1); border: 2px solid rgba(99, 102, 241, 0.4); border-radius: var(--radius-lg); padding: 24px; position: relative;">
          <!-- Official Skill Passport Header -->
          <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border-subtle); padding-bottom: 16px; margin-bottom: 16px;">
            <div>
              <span style="font-size: 0.72rem; letter-spacing: 0.1em; color: var(--emerald); font-weight: 800; text-transform: uppercase;">Official Campus Credential</span>
              <h3 style="font-size: 1.4rem; font-weight: 800; letter-spacing: -0.02em;">SKILL PASSPORT</h3>
            </div>
            <span class="tier-badge ${tierBadge}">${p.tier} Tier Verified</span>
          </div>

          <div style="display: flex; gap: 16px; align-items: center; margin-bottom: 20px;">
            <img src="${u.avatarUrl}" style="width: 72px; height: 72px; border-radius: 50%; object-fit: cover; border: 2px solid var(--primary);" alt="Avatar"/>
            <div>
              <div style="font-size: 1.2rem; font-weight: 700;">${u.name}</div>
              <div style="font-size: 0.88rem; color: var(--text-muted);">${u.college || 'Engineering Campus'}</div>
              <div style="font-size: 0.82rem; color: #f59e0b; margin-top: 2px;">★ ${p.averageRating} Rating (${p.totalReviews} student reviews) • ${p.completedTeachingSessions} Sessions Taught</div>
            </div>
          </div>

          <div style="margin-bottom: 16px;">
            <h5 style="font-size: 0.78rem; text-transform: uppercase; color: var(--text-dim); margin-bottom: 8px;">Verified Skill Badges:</h5>
            <div style="display: flex; flex-wrap: wrap; gap: 8px;">
              ${p.verifiedBadges && p.verifiedBadges.length > 0 ? p.verifiedBadges.map(b => `
                <span class="badge badge-emerald" style="padding: 6px 12px;">✓ ${b.skill} (${b.score}% Test Score)</span>
              `).join('') : '<span style="font-size: 0.85rem; color: var(--text-dim);">No verified test badges yet.</span>'}
            </div>
          </div>

          <div style="margin-bottom: 16px;">
            <h5 style="font-size: 0.78rem; text-transform: uppercase; color: var(--text-dim); margin-bottom: 8px;">Approved Verified Certificates:</h5>
            <div style="display: flex; flex-direction: column; gap: 6px;">
              ${p.approvedCertificates && p.approvedCertificates.length > 0 ? p.approvedCertificates.map(c => `
                <div style="font-size: 0.84rem; display: flex; justify-content: space-between; background: var(--bg-surface-2); padding: 8px 12px; border-radius: var(--radius-sm);">
                  <span>${c.title} (${c.issuingOrg})</span>
                  <span style="color: var(--emerald);">✓ Certified</span>
                </div>
              `).join('') : '<span style="font-size: 0.85rem; color: var(--text-dim);">No verified certificates yet.</span>'}
            </div>
          </div>

          <div style="display: flex; justify-content: space-between; align-items: center; border-top: 1px solid var(--border-subtle); padding-top: 16px; margin-top: 16px;">
            <span style="font-size: 0.8rem; color: var(--text-dim);">Peer Exchange Rate: <strong>${p.tierPrice} credits/session</strong></span>
            <button class="btn btn-primary btn-sm" onclick="navigator.clipboard.writeText(window.location.origin + '/#passport/' + '${u.id}'); App.showToast('Public Passport link copied to clipboard!', 'success');">
              Copy Public Share Link
            </button>
          </div>
        </div>
      `;
      App.openModal('Skill Passport', body);
    } catch (err) {
      App.showToast(err.message, 'error');
    }
  }
};

