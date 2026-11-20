/* ChatAdmin UI prototype. Data is in-memory sample data; replace D with API calls
   backed by the SQL views/functions (v_user_friend_counts, fn_active_users, ...). */
const nav=[['Dashboard','index.html','▦'],['User Management','users.html','♙'],['Login History','login-history.html','◷'],['Group Management','groups.html','♧'],['Spam Reports','spam-reports.html','⚑'],['New Registrations','registrations.html','＋'],['Friend Statistics','friend-stats.html','◎'],['User Activity','user-activity.html','⌁'],['Registration Chart','registration-stats.html','▥'],['Activity Chart','activity-stats.html','▤']];

/* ---------- helpers ---------- */
const $=(s,r=document)=>r.querySelector(s);
const E=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const norm=s=>String(s).normalize('NFD').replace(/[\u0300-\u036f]/g,'').replace(/[đĐ]/g,'d').toLowerCase(); // accent-insensitive search
const today=new Date().toISOString().slice(0,10);
const fd=s=>new Date(s.slice(0,10)+'T00:00').toLocaleDateString('en-US',{month:'short',day:'numeric',year:'numeric'});
const ft=t=>new Date(t.replace(' ','T')).toLocaleString('en-US',{dateStyle:'medium',timeStyle:'short'});
const cmp=(v,op,n)=>n===''||(op==='<'?v<+n:op==='>'?v>+n:v==+n);
const inRange=(d,f,t)=>(!f||d>=f)&&(!t||d<=t);
const sortRows=(a,s)=>{const[k,dir]=s.split(':');return[...a].sort((x,y)=>(typeof x[k]==='number'?x[k]-y[k]:String(x[k]).localeCompare(String(y[k]),'vi'))*(dir==='desc'?-1:1))};
const match=(q,...f)=>!q||norm(f.join(' ')).includes(norm(q));
const badge=s=>`<span class="status ${['Active','Successful'].includes(s)?'ok':['Locked','Failed'].includes(s)?'locked':'pending'}">${s}</span>`;
const MON=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];

/* ---------- sample data ---------- */
const U=(id,u,n,e,c,x={})=>({id,u,n,e,a:'',b:'',g:'male',c,s:'Active',...x});
const D={
users:[U(1,'minh123','Nguyễn Minh','minh@gmail.com','2026-11-12',{a:'Quận 1, TP.HCM',b:'2002-05-14'}),U(2,'linh456','Trần Mỹ Linh','linh@gmail.com','2026-11-11',{g:'female',a:'Thủ Đức, TP.HCM',b:'2001-09-02'}),U(3,'hoang789','Lê Hoàng','hoang@gmail.com','2026-11-10',{s:'Locked'}),U(4,'anhnguyen','Nguyễn Ngọc Anh','anh@gmail.com','2026-11-09',{g:'female'}),U(5,'spamuser01','Spam User 01','spam01@mail.com','2026-11-05'),U(6,'user234','User 234','user234@mail.com','2026-11-04'),U(7,'user789','User 789','user789@mail.com','2026-11-03'),U(8,'user101','Phạm Gia Bảo','bao@gmail.com','2026-11-17'),U(9,'user102','Đặng Thu Hà','ha@gmail.com','2026-11-16',{g:'female'}),U(10,'user103','Võ Minh Khang','khang@gmail.com','2026-11-15')],
f:[['minh123','linh456'],['minh123','anhnguyen'],['minh123','hoang789'],['linh456','anhnguyen'],['anhnguyen','user101'],['hoang789','user102'],['user101','user103'],['linh456','user234']],
logins:[['2026-11-17 19:42','minh123',1],['2026-11-17 19:35','linh456',1],['2026-11-17 19:12','hoang789',1],['2026-11-17 18:50','anhnguyen',1],['2026-11-17 18:20','hoang789',0],['2026-11-16 21:05','minh123',1]].map(([t,u,ok])=>({t,u,ok})),
groups:[{id:'g1',n:'Nhóm học tập CS',c:'2026-11-12',m:[['minh123','admin'],['linh456','member'],['anhnguyen','member'],['hoang789','member']]},{id:'g2',n:'Team Project',c:'2026-11-10',m:[['linh456','admin'],['minh123','admin'],['user101','member']]},{id:'g3',n:'Bạn thân',c:'2026-11-08',m:[['hoang789','admin'],['user102','member'],['user103','member']]}],
spam:[{id:1,who:'spamuser01',by:'minh123',r:'Gửi tin nhắn quảng cáo',d:'2026-11-17',s:'Pending'},{id:2,who:'user234',by:'linh456',r:'Tin nhắn làm phiền',d:'2026-11-16',s:'Pending'},{id:3,who:'user789',by:'anhnguyen',r:'Nội dung đáng ngờ',d:'2026-11-15',s:'Reviewed'}],
act:[{u:'minh123',op:24,pe:18,gr:5,d:'2026-11-17'},{u:'linh456',op:19,pe:12,gr:7,d:'2026-11-16'},{u:'hoang789',op:15,pe:9,gr:3,d:'2026-11-15'},{u:'anhnguyen',op:28,pe:21,gr:8,d:'2026-11-17'}]};
const CR={2026:[32,41,37,55,49,68,61,77,70,88,80,0],2025:[20,25,31,28,40,44,52,49,58,63,60,72],2024:[10,14,18,17,22,26,30,29,35,38,41,45]};
const CA={2026:[48,62,57,70,76,82,75,90,84,96,88,0],2025:[40,45,50,52,58,61,66,64,70,74,78,85],2024:[22,26,30,33,36,41,44,46,50,53,57,60]};
const byU=u=>D.users.find(x=>x.u===u);
const friendsOf=u=>D.f.filter(p=>p.includes(u)).map(p=>p[0]===u?p[1]:p[0]);
const ext=u=>{const s=new Set();friendsOf(u).forEach(f=>{s.add(f);friendsOf(f).forEach(x=>x!==u&&s.add(x))});return s.size}; // direct + friends of friends

/* ---------- layout pieces ---------- */
const path=location.pathname.split('/').pop()||'index.html';
let S={};
function shell(c){document.getElementById('app').innerHTML=`<div class="layout"><aside class="sidebar"><div class="brand"><span class="brand-mark">✉</span>ChatAdmin</div><div class="side-label">ADMINISTRATION</div><nav class="nav">${nav.map(n=>`<a class="${n[1]===path?'active':''}" href="${n[1]}"><span class="ico">${n[2]}</span>${n[0]}</a>`).join('')}</nav><div class="sidebar-bottom">● &nbsp; Admin workspace<br><br>UI prototype</div></aside><main class="main"><header class="topbar"><input class="search" placeholder="⌕  Search the system..."><div class="top-right"><span>♧</span><span class="muted">Administrator</span><span class="avatar">AD</span></div></header><section class="content">${c}</section></main></div>`}
const heading=(t,d,a='')=>`<div class="heading"><div><div class="eyebrow">CHAT SYSTEM / ADMIN</div><h1>${t}</h1><div class="sub">${d}</div></div>${a}</div>`;
const grid=(h,rows)=>`<div class="table-wrap"><table class="table"><thead><tr>${h.map(x=>`<th>${x}</th>`).join('')}</tr></thead><tbody>${rows.length?rows.map(r=>`<tr>${r.map(v=>`<td>${v}</td>`).join('')}</tr>`).join(''):`<tr><td colspan="${h.length}" class="muted">No records found.</td></tr>`}</tbody></table></div>`;
const tbl=(h,rows,t='Data List')=>`<div class="card table-card"><div class="card-head"><div class="card-title">${t}</div><span class="muted">${rows.length} records</span></div>${grid(h,rows)}</div>`;
function ctl(c){const v=S[c.k]??'';
 if(c.t==='select')return`<select class="field" data-k="${c.k}">${c.o.map(o=>{const[a,b]=Array.isArray(o)?o:[o,o];return`<option value="${a}"${a==v?' selected':''}>${b}</option>`}).join('')}</select>`;
 const i=`<input class="field${c.k==='q'?' searchfield':''}" data-k="${c.k}" type="${c.t}" placeholder="${c.ph||''}" value="${E(v)}">`;
 return c.l?`<label class="lb">${c.l}${i}</label>`:i}
const SORT_NC=[['n:asc','Name A→Z'],['n:desc','Name Z→A'],['c:desc','Newest first'],['c:asc','Oldest first']];
const Q={k:'q',t:'text',ph:'⌕  Search by name or username'};
const CMP=[{k:'op',t:'select',o:[['=','Equal to'],['<','Less than'],['>','Greater than']]},{k:'num',t:'number',ph:'Number'}];

/* ---------- chart ---------- */
const bars=(ds,y)=>{const v=ds[y],m=Math.max(...v)||1;return v.map((n,i)=>`<div class="bar-wrap"><div class="bar" style="height:${n/m*85}%"><span class="val">${n}</span></div><span class="month">${MON[i]}</span></div>`).join('')};
function chartCard(t,key){const ds=key==='a'?CA:CR,ys=Object.keys(ds).sort().reverse();return`<div class="card" data-chart="${key}"><div class="card-head"><div><div class="card-title">${t}</div><div class="muted">Month on the x-axis, count on the y-axis</div></div><select class="field" data-year>${ys.map(k=>`<option>${k}</option>`).join('')}</select></div><div class="chart">${bars(ds,ys[0])}</div></div>`}

/* ---------- pages ---------- */
const acts=x=>`<div class="acts"><button class="btn small" data-act="edit" data-id="${x.id}">Edit</button><button class="btn small" data-act="lock" data-id="${x.id}">${x.s==='Locked'?'Unlock':'Lock'}</button><button class="btn small" data-act="pwd" data-id="${x.id}">Password</button><button class="btn small" data-act="logins" data-id="${x.id}">Logins</button><button class="btn small" data-act="friends" data-id="${x.id}">Friends</button><button class="btn small danger" data-act="del" data-id="${x.id}">Delete</button></div>`;
const P={
'users.html':{title:'User Management',desc:'View and manage user accounts in the system.',action:'<button class="btn primary" data-act="add">＋　Add User</button>',
 ctl:[Q,{k:'st',t:'select',o:[['','All statuses'],'Active','Locked']},{k:'sort',t:'select',d:'n:asc',o:SORT_NC}],
 h:['Username','Full Name','Email','Created Date','Status','Actions'],
 rows:()=>sortRows(D.users.filter(x=>match(S.q,x.u,x.n)&&(!S.st||x.s===S.st)),S.sort).map(x=>[E(x.u),E(x.n),E(x.e),fd(x.c),badge(x.s),acts(x)])},
'login-history.html':{title:'Login History',desc:'Track login times and user accounts, newest first.',
 ctl:[Q,{k:'from',t:'date',l:'From'},{k:'to',t:'date',l:'To'}],h:['Time','Username','Full Name','Status'],
 rows:()=>sortRows(D.logins.filter(l=>match(S.q,l.u,byU(l.u)?.n)&&inRange(l.t.slice(0,10),S.from,S.to)),'t:desc').map(l=>[ft(l.t),E(l.u),E(byU(l.u)?.n),badge(l.ok?'Successful':'Failed')])},
'groups.html':{title:'Group Management',desc:'View groups, their members, and group administrators.',
 ctl:[{k:'q',t:'text',ph:'⌕  Search by group name'},{k:'sort',t:'select',d:'n:asc',o:SORT_NC.map(o=>o)}],h:['Group Name','Members','Created Date','Group Admins','Actions'],
 rows:()=>sortRows(D.groups.filter(g=>match(S.q,g.n)),S.sort).map(g=>[E(g.n),g.m.length,fd(g.c),E(g.m.filter(x=>x[1]==='admin').map(x=>x[0]).join(', ')),`<a class="btn small" href="groups.html#${g.id}">View Details</a>`])},
'spam-reports.html':{title:'Spam Reports',desc:'Review reports and lock reported accounts.',
 ctl:[{k:'q',t:'text',ph:'⌕  Filter by username'},{k:'from',t:'date',l:'From'},{k:'to',t:'date',l:'To'},{k:'sort',t:'select',d:'d:desc',o:[['d:desc','Newest first'],['d:asc','Oldest first'],['who:asc','Username A→Z'],['who:desc','Username Z→A']]}],
 h:['Reported User','Reported By','Reason','Time','Status','Actions'],
 rows:()=>sortRows(D.spam.filter(s=>match(S.q,s.who)&&inRange(s.d,S.from,S.to)),S.sort).map(s=>[E(s.who),E(s.by),E(s.r),fd(s.d),badge(s.s),byU(s.who)?.s==='Locked'?'<span class="muted">Account locked</span>':`<button class="btn small danger" data-act="spamlock" data-id="${s.id}">Lock account</button>`])},
'registrations.html':{title:'New User Registrations',desc:'Accounts created within the selected date range.',
 ctl:[Q,{k:'from',t:'date',l:'From',d:'2026-11-01'},{k:'to',t:'date',l:'To',d:today},{k:'sort',t:'select',d:'c:desc',o:SORT_NC}],h:['Username','Full Name','Email','Registration Date'],
 rows:()=>sortRows(D.users.filter(x=>match(S.q,x.u,x.n)&&inRange(x.c,S.from,S.to)),S.sort).map(x=>[E(x.u),E(x.n),E(x.e),fd(x.c)])},
'friend-stats.html':{title:'Friend Statistics',desc:'Direct friends compared with friends and friends-of-friends combined.',
 ctl:[Q,{k:'sort',t:'select',d:'n:asc',o:SORT_NC},...CMP],h:['Username','Full Name','Direct Friends','Direct + Friends of Friends'],
 rows:()=>sortRows(D.users.map(x=>({...x,d:friendsOf(x.u).length,x:ext(x.u)})).filter(x=>match(S.q,x.u,x.n)&&cmp(x.d,S.op,S.num)),S.sort).map(x=>[E(x.u),E(x.n),x.d,x.x])},
'user-activity.html':{title:'User Activity',desc:'App usage during the selected period.',
 ctl:[Q,{k:'from',t:'date',l:'From',d:'2026-11-01'},{k:'to',t:'date',l:'To',d:today},{k:'sort',t:'select',d:'n:asc',o:SORT_NC},{k:'m',t:'select',d:'op',o:[['op','App opens'],['pe','1-on-1 chats'],['gr','Group chats']]},...CMP],
 h:['Username','Full Name','App Opens','1-on-1 Chats','Group Chats'],
 rows:()=>sortRows(D.act.filter(a=>byU(a.u)).map(a=>({...byU(a.u),...a})).filter(r=>match(S.q,r.u,r.n)&&inRange(r.d,S.from,S.to)&&cmp(r[S.m],S.op,S.num)),S.sort).map(r=>[E(r.u),E(r.n),r.op,r.pe,r.gr])},
'registration-stats.html':{title:'New Registration Statistics',desc:'Number of new accounts registered each month. Choose a year.',chart:['New Registrations','r']},
'activity-stats.html':{title:'Active User Statistics',desc:'Number of users who opened the app each month. Choose a year.',chart:['Users Who Opened the App','a']}
};

function dashboard(){const st=[['♙',D.users.length,'Total Users','Registered accounts','blue'],['⌁',D.act.length,'Active Users','In the latest period','green'],['♧',D.groups.length,'Chat Groups','Total groups created','purple'],['⚑',D.spam.filter(s=>s.s==='Pending').length,'Spam Reports','Awaiting review','orange']];
return heading('Dashboard','Welcome back, Administrator.',`<button class="btn">⌑　${fd(today)}</button>`)+`<div class="grid stats">${st.map(s=>`<div class="card"><div class="stat-top"><span class="stat-icon ${s[4]}">${s[0]}</span></div><div class="stat-value">${s[1]}</div><div class="stat-name">${s[2]}</div><div class="muted">${s[3]}</div></div>`).join('')}</div><div class="grid two-col">${chartCard('New User Registrations','r')}${chartCard('Active Users','a')}</div>${tbl(['Time','Username','Full Name','Status'],sortRows(D.logins,'t:desc').slice(0,3).map(l=>[ft(l.t),E(l.u),E(byU(l.u)?.n),badge(l.ok?'Successful':'Failed')]),'Recent Login Activity')}<br><div class="grid quick"><a href="users.html">♙　User Management<span>Account information</span></a><a href="spam-reports.html">⚑　Spam Reports<span>Review reports</span></a><a href="groups.html">♧　Chat Groups<span>Group list</span></a><a href="registration-stats.html">▥　Statistics<span>View charts</span></a></div>`}

const gdBody=g=>{const adm=S.tab==='admins';return grid(['Username','Full Name','Role'],g.m.filter(x=>!adm||x[1]==='admin').map(([u,r])=>[E(u),E(byU(u)?.n||'—'),r==='admin'?'Admin':'Member']))};
function groupDetail(id){const g=D.groups.find(x=>x.id===id);if(!g)return heading('Group not found','No group matches this link.','<a class="btn" href="groups.html">← Back to groups</a>');
 S.tab='members';const n=g.m.length,a=g.m.filter(x=>x[1]==='admin').length;
 return heading(E(g.n),`Created ${fd(g.c)}`,'<a class="btn" href="groups.html">← Back to groups</a>')+`<div class="tabs"><button class="tab on" data-act="tab" data-id="members">Members (${n})</button><button class="tab" data-act="tab" data-id="admins">Admins (${a})</button></div><div class="card table-card" id="gd">${gdBody(g)}</div>`}

/* ---------- render ---------- */
function draw(){const t=$('#tbl');if(t){const p=P[path],r=p.rows();t.innerHTML=tbl(p.h,r)}}
(function boot(){const p=P[path];
 if(path==='groups.html'&&location.hash.length>1)return shell(groupDetail(location.hash.slice(1)));
 if(!p||path==='index.html')return shell(dashboard());
 if(p.chart)return shell(heading(p.title,p.desc)+chartCard(...p.chart));
 S={};p.ctl.forEach(c=>S[c.k]=c.d??'');
 shell(heading(p.title,p.desc,p.action||'')+`<div class="toolbar">${p.ctl.map(ctl).join('')}</div><div id="tbl"></div>`);draw()})();
onhashchange=()=>location.reload();

/* ---------- dialogs ---------- */
const closeM=()=>$('#m')?.remove();
const M=(t,b,f='<button class="btn" data-act="close">Close</button>')=>{closeM();document.body.insertAdjacentHTML('beforeend',`<div class="modal-bg" id="m"><div class="modal"><h3>${t}</h3><div class="mbody">${b}</div><div class="mfoot">${f}</div></div></div>`)};
const toast=t=>{document.body.insertAdjacentHTML('beforeend',`<div class="toast">${E(t)}</div>`);setTimeout(()=>$('.toast')?.remove(),2200)};
const err=t=>{$('#err').textContent=t};
const OK='<button class="btn" data-act="close">Cancel</button><button class="btn primary" id="ok">';
const confirmM=(t,msg,fn)=>{M(t,`<p>${msg}</p>`,OK+'Confirm</button>');$('#ok').onclick=()=>{closeM();fn();draw()}};
function userForm(x){const isNew=!x;x=x||{};
 const f=(l,k,t='text')=>`<div class="form-field"><label>${l}</label><input name="${k}" type="${t}" value="${E(x[k]||'')}"></div>`;
 M(isNew?'Add user':'Edit user',`<div class="form-grid">${f('Username','u')}${f('Full name','n')}${f('Email','e','email')}${f('Address','a')}${f('Date of birth','b','date')}<div class="form-field"><label>Gender</label><select name="g">${['male','female','other'].map(g=>`<option${x.g===g?' selected':''}>${g}</option>`).join('')}</select></div>${isNew?f('Password','p','password'):''}</div><div class="err" id="err"></div>`,OK+'Save changes</button>');
 $('#ok').onclick=()=>{const v=Object.fromEntries([...$('#m').querySelectorAll('[name]')].map(i=>[i.name,i.value.trim()]));
  const dup=D.users.some(o=>o!==x&&(o.u.toLowerCase()===v.u.toLowerCase()||o.e.toLowerCase()===v.e.toLowerCase()));
  const m=!v.u||!v.n||!v.e?'Username, full name and email are required.':!/^\S+@\S+\.\S+$/.test(v.e)?'Enter a valid email address.':dup?'This username or email is already in use.':isNew&&v.p.length<6?'Password must be at least 6 characters.':v.b>today?'Date of birth cannot be in the future.':'';
  if(m)return err(m);const{p,...rest}=v;
  isNew?D.users.push({id:Date.now(),c:today,s:'Active',...rest}):Object.assign(x,rest);
  closeM();draw();toast(isNew?'User added':'User updated')}}

document.addEventListener('input',e=>{const k=e.target.dataset.k;if(k&&P[path]?.ctl){S[k]=e.target.value;draw()}});
document.addEventListener('change',e=>{if(e.target.matches('[data-year]')){const c=e.target.closest('[data-chart]');c.querySelector('.chart').innerHTML=bars(c.dataset.chart==='a'?CA:CR,e.target.value)}});
document.addEventListener('keydown',e=>e.key==='Escape'&&closeM());
document.addEventListener('click',e=>{const b=e.target.closest('[data-act]');if(!b){if(e.target.id==='m')closeM();return}
 const id=b.dataset.id,x=D.users.find(u=>u.id==id),n=x?E(x.u):'';
 ({close:closeM,add:()=>userForm(),edit:()=>userForm(x),
 lock:()=>confirmM(x.s==='Locked'?'Unlock account':'Lock account',`${x.s==='Locked'?'Unlock':'Lock'} <b>${n}</b>?`,()=>{x.s=x.s==='Locked'?'Active':'Locked';toast('Account '+x.s.toLowerCase())}),
 pwd:()=>{M(`Change password for ${n}`,'<div class="form-field"><label>New password</label><input id="p1" type="password"></div><br><div class="form-field"><label>Confirm password</label><input id="p2" type="password"></div><div class="err" id="err"></div>',OK+'Update password</button>');
  $('#ok').onclick=()=>{const a=$('#p1').value;if(a.length<6)return err('Password must be at least 6 characters.');if(a!==$('#p2').value)return err('Passwords do not match.');closeM();toast('Password updated')}},
 logins:()=>M(`Login history for ${n}`,grid(['Time','Status'],sortRows(D.logins.filter(l=>l.u===x.u),'t:desc').map(l=>[ft(l.t),badge(l.ok?'Successful':'Failed')]))),
 friends:()=>M(`Friends of ${n}`,grid(['Username','Full Name'],friendsOf(x.u).map(f=>[E(f),E(byU(f)?.n)]))),
 del:()=>confirmM('Delete user',`Delete <b>${n}</b>? This also removes their friendships.`,()=>{D.users=D.users.filter(u=>u!==x);D.f=D.f.filter(p=>!p.includes(x.u));toast('User deleted')}),
 spamlock:()=>{const r=D.spam.find(s=>s.id==id);confirmM('Lock account',`Lock <b>${E(r.who)}</b> and mark their reports as reviewed?`,()=>{byU(r.who).s='Locked';D.spam.filter(s=>s.who===r.who).forEach(s=>s.s='Reviewed');toast('Account locked')})},
 tab:()=>{S.tab=id;document.querySelectorAll('.tab').forEach(t=>t.classList.toggle('on',t===b));$('#gd').innerHTML=gdBody(D.groups.find(g=>g.id===location.hash.slice(1)))}
 })[b.dataset.act]?.()});

 