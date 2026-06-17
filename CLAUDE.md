# Claude's Capabilities for UpWork Jobs

## What I CAN do

### Mobile Development
- React Native components, screens, and full apps
- Android XML layouts (fixing constraints, padding, spacing, cards)
- Flutter UI widgets and screens
- Styling, theming, button variants, form inputs
- Password toggles, modals, navigation, lists
- Build & compile APK locally (production release builds)
- Payment integration (Razorpay, PhonePe, Stripe, Google Play IAP)
- Offline databases (SQLite, op-sqlite) with local-first architecture
- State management, deep linking, referral systems

### Web Development
- HTML, CSS, JavaScript, TypeScript
- React, Next.js, Vue
- REST API integration (frontend side)
- Responsive layouts and styling
- Animated gradients, scroll-reveal animations, interactive components
- Static site hosting (Netlify, GitHub Pages)

### Backend / General
- Node.js, Python, Java, C++ (logic and APIs)
- Database queries (SQL)
- Writing proposals and cover letters
- Analyzing job requirements and planning work
- Systems programming (architecture, design, algorithms)

---

## What I CANNOT do

- **Use Figma, Adobe XD, or any design tool** — I write code, not design files
- **Build in visual no-code/low-code platforms** — Bubble.io, Adalo, Glide, FlutterFlow, Webflow, etc. I have no access to these drag-and-drop builders. (I can write raw Flutter/React/Node code, but not build *in* their visual editors.)
- **Access the internet or download client repos** — client must share files directly
- **Jobs requiring a live video call or screen share**
- **Machine learning model training** — inference/integration yes, training no

---

## Before you apply: Job Screening Checklist

**Ask these questions to decide if I can do a job (Claude does 100% of the work, you handle only communication/approvals):**

1. **Is the core work code-based, or is it stuck in a no-code platform?**
   - ✅ Code-based (React, React Native, Flutter, Node.js, Python, vanilla JS/HTML/CSS, native Android/iOS) → I can do it
   - ❌ Locked to visual platform (Bubble, Adalo, Glide, FlutterFlow, Webflow — their visual editors) → I cannot do it
   - ⚠️ No-code tool with API/code escape hatch → Possible, but clarify first

2. **Difficulty level for Claude (what's the actual technical work)?**
   - ⭐ Easy (1–5 hours): Bug fixes, CSS tweaks, simple features, copy updates
   - ⭐⭐ Medium (5–20 hours): Feature implementation, API integration, testing, layout work, performance fixes
   - ⭐⭐⭐ Hard (20–80 hours): Full app builds, complex architecture, ML/real-time systems, DevOps, large refactors
   - ❌ Very hard: Anything requiring specialized hardware (games w/ 3D graphics, AR), specialized cloud infra you don't have credentials for, or custom device drivers
   - **Apply to jobs at any difficulty level, but factor time into budget**

3. **Are there manual clicks that you (the user) must do? (These are OK if they're <5 min)**
   - ✅ "Click Submit on Play Store" (1 min) → I build & prepare, you click Submit
   - ✅ "Test on your physical device" (10 min) → I build APK, you test & verify
   - ❌ "Daily standup sync" or "review my code regularly" → That's ongoing user participation, skip it
   - ⚠️ "I'll send you screenshots of what I want" (requires multiple back-and-forth) → Only if scope is clear

4. **Communication: Chat-only, no live calls?**
   - ❌ Job requires live video calls, screen shares, real-time meetings, or daily standups → Skip it
   - ✅ Async communication only (messages, email, Discord, Upwork chat) → Apply
   - ⚠️ One-time onboarding call (30 min, just to clarify scope)? → Can accept, but note async preference in proposal

5. **Access: Can I get the files and credentials I need?**
   - ❌ "Download our private GitHub" (I can't access internet URLs) → Ask client to share files/zip directly, or invite you as collaborator on a repo they share access to
   - ❌ "You'll need Firebase/AWS access" without client providing credentials → Request credentials from client
   - ✅ Client can share source code, .zip files, credentials via Upwork → Can do it

6. **Does this align with Islamic values?**
   - ❌ Explicitly haram: interest-based lending, alcohol, gambling, deception, scams, unlicensed gambling/lotteries, un-halal food promotion
   - ❌ Work that conflicts with your conscience
   - ❌ **Clients from Israel** — Do NOT accept or proceed with jobs from Israeli clients, even if accidentally applied
   - ✅ Neutral technical work: building tools, apps, APIs for legitimate businesses (even if they serve other religions)
   - ⚠️ Grey area? Use judgment. Building a verification app for Hindu restaurants is technical work. Apps facilitating haram (betting odds, loan calc for interest loans) are haram. If unsure, skip it.

**Decision rule:**
- ✅ **Apply if:** Code-based, doable difficulty, async-only or minimal user clicks, files/access available, ethical
- ❌ **Skip if:** No-code platform, locked scope, requires your active participation beyond communication, haram
- ⚠️ **Ask first if:** Scope unclear, difficulty/hours uncertain, or client needs clarification

---

## How this works: Your role vs. Claude's role

**Claude (me) does — ALL the literal work:**
- All technical work (code, debugging, fixes, architecture, database design, etc.)
- Write the proposal, cover letter, terms, screening answers
- Build, compile, optimize (within my tool limits)
- Test on Android device via adb/USB
- For React Native/Flutter: Write code for iOS + Android, but test Android only
- Draft client replies and communication
- **Portfolio is built on the quality of this work**

**You do — Business + Communication + iOS Testing:**
- Review & approve proposals before I submit them
- Handle client communication (accept/decline offers, send replies via Upwork)
- Accept payment when milestones are complete
- Grant access when needed (GitHub collaborator, adb, etc.)
- Connect Android phone via USB when testing needed
- **For iOS apps: Test iOS yourself OR hire Mac-based dev for iOS testing**
- Final verification/sign-off on deliverables
- End contract & leave reviews

**Key rule:** Only apply to jobs where I can do 100% of the technical work from my end. If a job requires you to do technical work (compile code, test on real device without adb, use tools I can't access), flag it first.

**Portfolio strategy:** Since I do all the technical work, the portfolio grows through quality delivery. Lower pricing now + excellent work = strong portfolio + future higher rates.

---

## Using scrcpy for Android Testing & Demos

**What scrcpy enables:**
- Mirror Android phone screen to your computer
- I can see and interact with the phone display via computer-use
- Watch videos/demos on the phone
- Test apps in real-time
- Verify UI/UX during development

**How to use:**
1. Connect Android phone via USB
2. Enable USB Debugging (if not already)
3. Run `scrcpy` in terminal (it mirrors the phone screen)
4. I use computer-use to view/interact with the mirrored display
5. Use for: Testing, demos, watching videos, UI verification

**When this is useful:**
- Client demos app functionality on their phone → I can see it
- Need to test React Native/Flutter app on real device → I can see results
- Client wants to show a video on their phone → I can watch it via mirror

---

## Clarification: Cross-Platform Apps (React Native / Flutter)

**When a job says "iOS and Android":**
- Often means: App should work on both platforms (not necessarily upload to both stores)
- I CAN write React Native/Flutter code for both iOS + Android ✅
- I CAN test Android (via adb) ✅
- I CANNOT test iOS (no macOS) ❌
- **In proposals:** Clearly state iOS testing requirement upfront. Example: "I'll develop and test Android fully. iOS testing must be done on your end or by hiring a Mac-based developer."

**For jobs requiring iOS App Store upload:**
- I cannot directly upload (no account access, no macOS)
- You can: Upload yourself (I guide you) or hire someone with Mac for that step

## How to use me for UpWork jobs

1. Share the client's job post or screenshots here
2. I screen it against the checklist (can I do it? is it worth it? chat-only? halal?)
3. If ✅ all green, I write a PROPOSAL.md ready to copy-paste
4. You review & submit the proposal to the client
5. Once hired, I do all the technical work; you handle client comms
6. I deliver the code/deliverables in the job folder
7. You submit/verify with the client; I handle any tech questions

---

## Job Screening Workflow (Framer → GoFullPage → Code)

**How you can use design platforms safely:**

1. **Framer design** (you do this)
   - You design in Framer (or use Framer template)
   - You do the UX/design work (user research, wireframes, prototypes)

2. **GoFullPage screenshot** (you do this)
   - Screenshot the Framer design
   - Export as image/PDF

3. **I code it** (I do this)
   - I build HTML/CSS/JavaScript from your screenshots
   - You don't use Framer directly; you just see the output

**This works because:** You do the design work, I do the code work. No platform restrictions violated.

---

## Jobs to TARGET (Portfolio-Building Phase)

✅ **APPLY to these:**
- "Build a React landing page" ($50-200, 2-5 days)
- "Fix this bug in my React app" ($30-75, 1-2 days)
- "Create a contact form with validation" ($40-100, 1-2 days)
- "Build a Node.js REST API endpoint" ($75-150, 2-3 days)
- "Polish existing website" (clear scope, 1-2 weeks, $100-300)
- "Convert this design to HTML/CSS" ($75-250, 3-5 days)
- "Android UI fix" ($30-75, 1-2 days)
- "Build a simple React app" ($100-300, 5-7 days)

---

## Jobs to SKIP (Even if price is high)

❌ **SKIP these (regardless of budget):**
- "Must be built in Webflow" / "Must use FlutterFlow" / "Must use Leadpages" (platform blocker)
- "Figma design required" / "Adobe Illustrator expertise" (design tools blocked)
- "Full website redesign" without scope details (vague = scope creep)
- "Ongoing project" with undefined duration (lock-in risk)
- "Build me an app" with no details (too vague)
- "UI/UX Designer needed" without your having design portfolio (design work, not coding)
- "Show examples of [platform] work you've done" when you don't have them (instant rejection)

---

## Red Flags to Watch For

🚨 **MAJOR (Skip immediately):**
- Budget is extremely low ($50 for "full website") + scope is large
- "Must have published apps" or "Must have [platform] experience" when you don't
- Vague scope + "ongoing" = unclear commitment
- "Visual platform required" (Webflow, FlutterFlow, Framer itself as builder, etc.)
- Design work required ("Create wireframes," "Design UI," "Show design portfolio")
- **Payment method NOT verified** — always check, skip if unverified (risk of non-payment)
- **$X total spent ÷ hires = avg < $15/hire** — client pays nothing, not worth time
- **"Show your best [X] you made in past"** when you have no [X] portfolio — instant skip
- **Client joined < 7 days ago + 0% hire rate** — high ghost risk (check payment verified first)

⚠️ **MEDIUM (Proceed with caution):**
- Budget seems low but scope is clear (negotiate or skip)
- 10+ proposals already (high competition, low win rate)
- "Ongoing project" without defined duration (ask client for specifics)
- Client inactive (last viewed "2 weeks ago" = slow responder)
- Platform recommendation required ("What platform would you use?")

---

## Screening Decision Tree

**For every job, ask:**

1. **Can I do this work?** (Code-based? No design tools? No iOS testing?)
   - NO → Skip
   - YES → Continue

2. **Is scope clear?** (Can I define exactly what "done" looks like?)
   - NO (vague scope) → Skip
   - YES → Continue

3. **Will I likely get a 5-star review?** (Straightforward work? Happy client likely?)
   - NO (high complexity, risky, low trust) → Skip
   - YES → Apply

4. **Is budget realistic for the work?** (Even if low, not poverty wages?)
   - NO (under $20-30 for major scope) → Skip
   - YES → Apply

---

## When to Apply (Even at Low Prices)

✅ **Apply if:**
- Clear, specific scope ("5-page website, contact form, responsive")
- You're confident you can deliver ("polish existing code" not "redesign from scratch")
- Fast timeline (1-2 weeks max = quick 5-star wins)
- 5-star probability is high (straightforward deliverable)
- Client is active (viewed recently, responsive)

❌ **Don't apply if:**
- Scope is vague ("full website", "make it better", "improve it")
- Duration is unclear ("ongoing", no end date)
- Work requires expertise you don't have (design, marketing, iOS testing)
- Client has red flags (inactive, 50+ proposals, platform-specific requirements)

### Cleanup after a job is delivered
Once a job is finished and the milestone is paid, clean up disk space but keep what the client might still ask for.

**Delete (regenerable / obsolete):**
- Temporary standalone build copies made to work around Windows path limits (e.g. C:\m) — these can be huge (10+ GB of node_modules, gradle caches, android build output). The real fix lives in the git repo and CI rebuilds the APK on demand.
- Obsolete one-off scratch files (e.g. support-message drafts for issues already resolved).

**Always keep (in the job folder under C:\UpWork\):**
- The job's PROPOSAL.md
- The final deliverable(s) — e.g. the working APK (save a clearly-named copy like app-release-WORKING.apk before deleting the build folder)
- A short FIX-NOTES.md documenting the problem, root cause(s), exact files/commits changed, and how to rebuild — so the fix can be explained or redone if the client comes back.
- The actual git repo clone (e.g. C:\el) if it has committed, pushed work — it holds the history and is the source of truth.

Always copy/save the keepers FIRST, verify they're saved, then delete the bulk. Deletion is destructive — never delete the git repo or a deliverable that hasn't been backed up.

### Pricing Guide — Exact Rates by Job Type

| Job Type | Rate | Notes |
|---|---|---|
| **Bug fix / small feature** | $25-30/hr | 1-5 hours, straightforward work |
| **React Native / Flutter app** | $35-45/hr | Full app or major feature, API integration |
| **Web app (React/Next.js)** | $30-40/hr | Moderate complexity, includes design/UX |
| **Backend (Node.js/Python)** | $35-50/hr | API design, database work, integrations |
| **Full-stack project** | $40-55/hr | Combined front + back, deployment |
| **Android XML layouts** | $20-25/hr | Layout fixes, constraint work, styling |
| **DevOps / CI-CD** | $50-75/hr | GitHub Actions, Docker, deployment pipelines |
| **Proposal writing / analysis** | $15-20/hr | Job analysis, cover letters, scope planning |

**Pricing rules:**
- Never quote below the minimum for the job type — you're worth it
- Estimate conservatively (add 20% buffer for unknowns), then quote the middle or high-end number
- If client says "this is all I can afford" and it's below the minimum, **decline politely** or propose a smaller scope
- For fixed-price: quote (estimated hours × rate × 1.2) to account for unknowns

### Proposal Format Rules
- Every proposal gets its own folder in C:\UpWork\
- All text must be ready to copy-paste — no editing required
- Keep cover letters short — 4 to 6 lines max
- PROPOSAL.md always includes: cover letter + terms
- Always paste the full cover letter and terms directly in the chat response too (not just in the file), so it can be copied immediately
- **For pricing: use the exact rates from the Pricing Guide above. Quote a specific number, not a range.** Example: "I quote $35/hour for this work" instead of "$30-40/hour"
- Only include these sections IF they apply to the job:

| Section | Include when |
|---|---|
| **Terms** (payment type, amount, duration, boost) | Always |
| **Cover Letter** | Always |
| **Screening Question Answers** | Only if the job has screening questions |
| **Attachments** | Only if you have relevant work samples to share |
| **Profile Highlights** | Only if the client's job is in a category matching your portfolio projects |
| **After Hired** | Only if I need files from the client to do the work |

---

## Proposal Writing Rules

### The First Two Lines Rule
When a client looks at their dashboard, they only see the first two sentences of your cover letter. Those two lines decide whether they click or skip.

**Never start with:**
- "Hi, my name is X and I have 3 years of experience..."
- "I am a skilled developer who..."
- "I read your job post carefully..."

**Always start with the solution:**
- "I can implement that Show/Hide password toggle today using SecureTextEntry and a basic state variable."
- "I know exactly what's causing your APK crash — it's almost always a missing release signing config in Gradle."
- "I can fix the Stock badge overflow in your Android layout by adding a minWidth constraint to the TextView."

The first two sentences must prove you understand the problem and can solve it — before they even click to read more.

### Watch for Hidden Filter Words
Some clients hide a required word/phrase in the job description to filter out copy-paste proposals. Common signals: "Start your proposal with the word X", "Include the phrase Y in your bid", "Comment BEST_PAYWALL". Always scan the full job description for these before writing the proposal — missing it = instant discard.

### Don't Give Away the Solution
Show that you know the answer — but don't explain it in detail. If you spell out the exact fix, the client can just do it themselves and won't need to hire you.

**Wrong:** "Your build fails because EAS detects pnpm from pnpm-workspace.yaml — fix it by adding packageManager to your package.json."
**Right:** "I've fixed this exact EAS + pnpm monorepo conflict before. Share your eas.json and I'll have it working within the hour."

Prove you know it. Don't teach it.

### Timeline Language Rules
- Always add "likely faster" after timeline commitments. Example: "3–4 days, likely faster."
- Never say "Your X-week estimate can be cut to Y days" — it sounds condescending and implies the client is wrong. Say it positively: "This will take 3–4 days — the stack is straightforward and I've built all the pieces before."
- Client saying "2–3 weeks, could be shorter" = they're non-technical and uncertain. Don't correct them; just state your faster timeline as a confident fact.

### Portfolio Links in Proposals
- Never include GitHub links in proposal screening answers — use portfolio website URLs only.
- Use: quillo-ai-writer.netlify.app, lumira-apparel.netlify.app, meridian-creative.netlify.app, estate-landing.netlify.app, useaxion.netlify.app

---

## Advanced Screening Signals

### Client Quality Signals
- **Avg spend per hire** = total spent ÷ hires. If < $15/hire → client doesn't actually pay contractors, skip.
- **Hire rate %** — if < 20%, client mostly browses without hiring. Skip unless other signals are very strong.
- **"Last viewed X days ago" + already interviewing 3** = stale job, skip.
- **Avg hourly rate paid (actual)** on client profile = what they really pay. Ignore posted range; actual tells the truth.
- **Interviewing count** — if already interviewing 3+, you're late and odds are bad. Skip.

### Keyword Findings (what works on Upwork search)
- ✅ **"React developer"** — good results, active jobs
- ✅ **"Next.js developer"** — solid niche results
- ✅ **"Stripe"** — good payment integration jobs
- ✅ **"Supabase"** — good backend/auth jobs
- ✅ **"full stack developer"** — broad but works
- ❌ **"shadcn"** — doesn't exist on Upwork search
- ❌ **"Clerk"** — returns office/admin clerk jobs, not the auth library
- ❌ **"landing page"** — returns marketing/SEO/copywriting jobs, not dev jobs
- ❌ **"Vercel"** alone — too niche, few results

---

## Working Style Rules

### Always give client reply text
Whenever a client (on Upwork, Discord, etc.) sends a message that needs a response, always draft a ready-to-copy-paste reply for the user — don't wait to be asked "what should I reply".

### Just do simple tasks
For straightforward, low-risk tasks (installing tools, cloning repos, running builds, writing code, drafting messages), just do them directly without asking for permission first. Only pause to ask if the action is destructive, costs money, or is ambiguous.

### Recommend model/effort for harder tasks
If a task is complex (large codebase work, big refactors, long builds/debug sessions), recommend which Claude Code model and effort/thinking level to use (e.g. "this is complex enough to use Opus" or "use high effort/thinking mode for this") instead of just proceeding on default settings.

### Don't re-read previous messages unless told to
Context is expensive. Only read old messages if the user explicitly asks you to ("read the previous...", "check what I said about...", "look back at..."), or if you're resuming from a context break and the summary doesn't have the info you need. Otherwise, assume you already have the context you need from the active conversation. This keeps token usage lean.

### The user is Muslim — keep it halal and respectful
- The user is a Muslim. Keep all work, advice, and drafted messages aligned with Islamic values: be honest, no deception, no interest (riba), no haram content, no scams.
- When drafting replies to clients who are also Muslim, a warm, respectful tone is welcome — light, natural use of phrases like "Alhamdulillah" or "Insha'Allah" is fine when the client uses them first or the context fits. Don't force it.
- Always be honest with clients about what was and wasn't done — never overstate a fix or claim something works if it hasn't been verified.
- Be kind, patient, and encouraging in all drafted messages and in working with the user.

---

## Upwork Profile Setup (Completed 2026-06-14)

**Profile Structure & Learnings:**
- **Headline:** "Landing Page & E-commerce Builder | React | Node.js | Payment Integration" (specific WHAT you build, then HOW)
- **About Me:** 350-400 words max (NOT 700+). People skim, don't read full long profiles. Keep it punchy.
- **Content flow:** Opening → What I do → Proof (portfolio) → Reliability promise (response time, delivery) → Call to action
- **Response time:** 12 hours (not 24, more competitive)
- **Delivery promises:** Website builds (2–5 days) | Bug fixes (same-day to 2 days) | Payment integration (2–4 days) | Mobile apps (1–2 weeks)
- **Revisions:** Two rounds included, major scope changes billed separately
- **Communication:** Daily status updates (no ghosting)

**Portfolio Setup:**
- Keep 3 separate portfolio items (NOT combined) — better visibility, appears in more searches
- Format: Title → My role → Description (with ✅ checkmarks) → Skills & deliverables → Live link

**Portfolio Item Skills (Final):**

1. **Quillo (SaaS Landing Page):**
   - Landing Page Design, Web Design, UI/UX Design, HTML5, CSS3, JavaScript, Animation, Responsive Design

2. **Meridian (Creative Agency Portfolio):**
   - Web Design, UI/UX Design, HTML5, CSS3, JavaScript, Animation, Responsive Design, Portfolio Design (or closest match)

3. **Lumira (E-commerce Store):**
   - Ecommerce Website Development, HTML5, CSS3, JavaScript, Responsive Design, UI/UX Design

Note: Use exact skill names from Upwork dropdown. If skill doesn't exist, choose closest match.

**First Application:**
- Applied to Netlify/Node.js developer job ($100, 2-3 hours, posted 6 min ago, <5 proposals)
- Proposal: "I can have this done in 2 hours. I've deployed 50+ Node.js projects to Netlify. Install 2 files, verify build, test endpoints, deploy. Live in 2 hours, test link before payment. Starting now."

**Key Wins:**
- Profile live and searchable
- 3 portfolio items added
- First job applied
- Ready to apply to 2-3 more jobs in next 24 hours

---

## Current Job Status (as of 2026-06-16)

### Active Proposals
1. **DataPivot — Site Revamp** (Egypt client)
   - Budget: $200 fixed
   - Status: ✅ Submitted, waiting for response
   - Folder: `C:\UpWork\DataPivot-SiteRevamp\PROPOSAL.md`

2. **Android Developer — Usage-Time Feasibility Prototype** (Canada)
   - Budget: $500 fixed
   - Client: Payment verified + phone verified, member since Jun 11, 2026
   - Status: ✅ Submitted (11 connects), waiting for response
   - Scope: Kotlin prototype using UsageStatsManager + UsageEvents API, APK + source + findings summary
   - Key tech: PACKAGE_USAGE_STATS special permission, queryEvents() ACTIVITY_RESUMED/PAUSED, AppOpsManager.checkOpNoThrow(), ACTION_USAGE_ACCESS_SETTINGS Intent
   - Folder: `C:\UpWork\AndroidUsageTime-Prototype\PROPOSAL.md`

3. **Next.js Web Application Developer** (Australia, Sydney)
   - Budget: $180 fixed
   - Client: Payment verified + phone verified, member since Jun 16, 2026 (new)
   - Status: ✅ Submitted (9 connects), waiting for response
   - Scope: Next.js + Supabase Auth (email/password + Google OAuth) + Stripe Checkout + Claude API credits + dashboard, deploy to Vercel
   - Folder: `C:\UpWork\NextJS-Claude-Stripe-App\PROPOSAL.md`

4. **Senior Next.js Developer — AI-Powered Web Apps** (Pakistan)
   - Budget: ~$500 fixed (estimated)
   - Status: ✅ Submitted, waiting for response
   - Cover letter hook: "I build exactly this stack — Next.js App Router with TypeScript, Tailwind CSS, SSR/SSG for SEO, MongoDB, and Claude API for AI features."

### Expensify Bounties (GitHub)
- All 20 open Help Wanted issues were already assigned when checked — bounties get grabbed within hours of posting
- Posted proposals on: #93448, #91846, #93568 — waiting for Melvin Bot assignment
- **Rule:** Always check `gh issue list --repo Expensify/App --label "Help Wanted"` first. If assigned → skip, don't write proposal.

### Connect Status
- Current: ~32 connects remaining (as of 2026-06-16)
- Strategy: Be selective — only apply when screening signals are strong

### Portfolio Websites (Web Design Showcase)
**3 live design portfolio pieces deployed to Netlify:**
1. **Quillo – AI Writing Assistant** (https://quillo-ai-writer.netlify.app/)
   - SaaS landing page with animated gradient hero, interactive FAQ accordion, product showcase
   - Shimmer loading effects, scroll-reveal animations
   - ~11,000 lines of HTML/CSS/JavaScript
   - Showcases: UI Design, Animation, Web Design, JavaScript, CSS3, HTML5

2. **Meridian Studio – Creative Agency** (https://meridian-creative.netlify.app/)
   - Minimal creative portfolio with auto-scrolling logo marquee, smooth animations
   - Interactive FAQ with animated icons, gradient text effects
   - ~6,136px scrollable page with glass-morphism styling
   - Showcases: Web Design, Animation, UI Design, CSS3, HTML5

3. **Lumira – Fashion E-commerce** (https://lumira-apparel.netlify.app/)
   - Interactive e-commerce interface with working product filters (category, size, availability)
   - Dynamic banner updates, shopping cart with toast notifications
   - Animated gradient hero, responsive grid layout
   - ~40KB index.html with product filtering logic and state management
   - Showcases: Web Design, Responsive Design, JavaScript, CSS3, HTML5

**Portfolio strategy for these:** These are front-end design showcase pieces (not full business apps). They demonstrate:
- Modern UI/UX capabilities
- Responsive design (mobile 560px, tablet 980px, desktop)
- Interactive features without backend
- Animation and visual design
- Code quality and performance
- Ready to show to clients interested in web design or frontend development

**Repository:** Hosted on GitHub in `Websites Portfolio` folder for version control and easy deployment

---

### Building Reputation Strategy
🎯 **PRIMARY GOAL: Build portfolio NOW → Higher prices LATER**

**Current status: Only 1 client in history. Need 5–10 completed jobs with 5-star reviews FIRST.**

- **Why:** Strong portfolio = can charge 2-3x more in 6-12 months. Clients hire based on proven track record, not just skills.
- **Pricing mindset:** Money is NOT the priority right now. A 5-star review from a verified client is worth more than holding out for $500. Since Claude does all the work, even $25–50 USD = real portfolio win + real Rupiah value.
- **New rule — Easy tasks, any price:** If a job is genuinely easy (UI tweaks, simple bug fixes, basic features that take <1 day), **accept ANY price** to get the 5-star review fast. Example: $20 Flutter UI updates = 1 day work = 5-star review = portfolio win.
- **Current Strategy (Portfolio-First):** Accept ANY job that is:
  - ✅ Clear scope (no vague "build me an app")
  - ✅ Honest deliverables (nothing I can't deliver)
  - ✅ Halal work
  - ✅ Code-based (my skills)
  - ✅ Fast turnaround (1–14 days preferred)
  - ✅ High 5-star review probability (straightforward = happy client)
  - ⚠️ Price is FLEXIBLE (no hard minimums, focus on portfolio diversity)
- **Portfolio-phase pricing (ACCEPT NOW):**
  - Quick bug fix (1–3 days): ANY PRICE if scope is clear
  - Feature addition (3–5 days): $30–200
  - Small app / prototype (5–10 days): $50–500
  - Medium project (10–20 days): $100–1,000
  - **Philosophy:** Money is for later. 5-star reviews + diverse portfolio = 2-3x higher rates in 2-3 months
- **Context:** User is Indonesian. $25–50 USD = Rp400K–800K = significant local value + portfolio investment
- **After 5–10 completed jobs:** Raise rates to market rate ($35–50/hr)
- **Approach:**
  - Complete visible projects quickly (8-30 day turnarounds)
  - Get 5-star reviews and testimonials
  - Build case studies showing Android, React Native, backend, integrations
  - Show consistent, professional delivery
- **Selection criteria (PORTFOLIO-FIRST):** Only code-based work, with preference for:
  - **Clear scope & realistic timeline** (VAGUE SCOPE = NO; clear scope with low price = YES)
  - **Fast turnaround** (1-14 days preferred) for rapid portfolio wins
  - **Different tech stacks** (Android, React Native, Node.js, Python, etc.) to show breadth
  - **No tech blockers** (iOS testing, FlutterFlow visual builder, Figma design) = Can't deliver = Bad portfolio
  - **High 5-star probability** (straightforward projects beat ambitious projects)
  - **Avoid long hourly contracts** (6+ months locks you in, prevents portfolio diversity)
  - **ACCEPT low prices** if scope is clear and doable (portfolio > hourly rate at this stage)
- **Testing capability:** Can connect Android phone via USB only (adb testing + scrcpy mirroring). Can use scrcpy to mirror phone screen, interact with UI, watch videos, test apps in real-time
- **Quality bar:** ALL work must be production-ready and honest with clients (no overstating, no shortcuts). Reputation > quick money. A bad 5-star review is worse than no portfolio.
- **Long-term payoff:** 10-15 solid projects with 5-star reviews in 2-3 months → charge $50-75/hr instead of $25-35/hr → earn 2-3x more per project

### Jobs to Skip / Rejected
**Don't skip based on price.** Skip only if:
- ❌ **Vague scope** (can't define deliverables clearly)
- ❌ **Tech blockers** (iOS testing, Figma design, FlutterFlow, visual no-code platforms)
- ❌ **Impossible deadlines** (can't deliver = bad review)
- ❌ **Dishonest/haram work** (scams, misrepresentation, unethical)
- ❌ **Long hourly contracts** (6+ months locks you in)
- ❌ **Zero review probability** (ambiguous scope = unhappy client = bad review)

**Examples of what to SKIP:**
- Dating app with vague requirements → Unclear scope, scope creep risk
- iOS-specific React Native fix → Can't test iOS (no macOS)
- "Build me an AI app" (no details) → Vague = bad review
- 3-day turnaround for 80-hour project → Can't deliver = bad review
- WordPress visual builder work → Can't do (visual platform)

**Examples of what to ACCEPT (regardless of price):**
- ✅ "Build a simple landing page" ($20, 1 day) → Clear scope, easy win
- ✅ "Fix this bug in my React app" ($30, 2 hours) → Straightforward
- ✅ "Build a to-do app with React" ($50, 1 week) → Clear, doable
- ✅ "Create a Node.js API endpoint" ($40, 3 days) → Standard work

### Pricing Strategy
**PRIMARY STRATEGY: Portfolio-First (Experience Over Earnings)**

🎯 **Core Principle:** Since Claude does 100% of the technical work, pricing is SECONDARY to portfolio building. Focus on accumulating 5-star reviews and diverse project examples.

**Pricing Guidelines (in order of priority):**
1. **Clear scope + doable work** — ACCEPT (regardless of price)
2. **Fast turnaround** (1-7 days) — ACCEPT (quick portfolio wins)
3. **Different tech stack** — PRIORITIZE (build breadth: React, React Native, Node, Python, etc.)
4. **5-star review probability** — ACCEPT (straightforward projects = happy clients = reviews)
5. **Price** — FLEXIBLE (as long as it's not obviously poverty wages like $5)

**Acceptable Price Ranges (for portfolio building):**
- Micro tasks (1-3 hours): $10–50
- Small features (4-8 hours): $30–150
- Medium projects (1-2 weeks): $100–500
- Larger projects (2-4 weeks): $300–1500

**NOT acceptable:**
- ❌ Extremely vague scope (scope creep = bad reviews)
- ❌ Impossible deadlines (can't deliver = bad reviews)
- ❌ Client expects you to do the work (but you do all work through me)
- ❌ Inherently dishonest work (no scams, misrepresentation, haram)

**Strategy:**
- Quote fast, accept quickly
- Deliver early/on-time (beats expectations)
- Get 5-star review + testimonial
- Build next job offer based on previous success
- After 5-10 completed jobs with strong reviews: RAISE RATES to $50-75/hr

**Portfolio Building:** Willing to accept lower rates than typical to build reputation, as long as:
- Work is code-based and clear-scoped
- Price is competitive vs. average bid (not poverty wages)
- Fast turnaround (8–30 days) for quick portfolio wins

### Key Decisions Made Today
1. ✅ Apply to React Native $44K job (strong opportunity, professional client, clear scope)
2. ✅ Cancel/skip low-budget jobs ($200–$600 range) — waste of time and connects
3. ✅ Kept Freelancer Plus membership ($9.99/month for 100 connects/month) — worth the investment
4. ✅ Use Sonnet or Opus for production development (not Haiku) — better reasoning for complex work
5. ✅ Quote conservatively (22–24 weeks) so finishing early looks amazing
6. ✅ Don't renegotiate price after applying — lock in $50/hr, negotiate rate increases after milestone success

### How to Handle App Store Submission
- You code the full app
- Client tests on real devices (or you test on their device via ADB if using Android)
- You prepare App Store/Play Store submission (screenshots, metadata, privacy policy)
- Client (or you guide them) submits to App Store / Google Play
- First rejection is common — you provide fixes, client resubmits
- Budget 1–2 weeks for review cycles

### Next Steps When New Session Starts
1. Check Upwork messages — waiting on responses to 4 active proposals (DataPivot $200, Android UsageTime $500, Next.js Australia $180, Senior Next.js Pakistan ~$500)
2. Message Random Number Generator client — APK is ready to deliver
3. ✅ Estate deployed: https://estate-landing.netlify.app/ — add to Upwork portfolio
   - Deploy Pulse to Netlify → add to Upwork portfolio
4. Next portfolio piece: Restaurant/Food ordering page (need Framer inspo screenshot first)
5. Keep applying: search "React developer" sorted by newest, 7–9am is best timing for fresh jobs
6. Check Expensify bounties: `gh issue list --repo Expensify/App --label "Help Wanted"` — only post if NOT yet assigned

### Jobs Screened & Rejected (2026-06-16)
- **Saudi Stock Market GitHub** ($30) → SKIP: 13% hire rate (175 jobs, 22 hires), $24.60 avg spend per hire, already interviewing 3, last viewed 2 days ago
- **Electron Publishing** ($10-20/hr) → CANCELLED: user cancelled proposal
- **Kingdom ID Stripe** ($250) → CANCELLED: user cancelled proposal
- **WordPress Mobile-Friendly** ($30) → CANCELLED: user cancelled proposal
- **Website Developer Shelby County DSA** ($400) → SKIP: 20-50 proposals already, too much competition
- **Mobile App for Play** ($15-30/hr) → SKIP: payment not verified, vague scope
- **High Converting Paywall** ($75) → SKIP: design work (UI/UX portfolio required, we have none)
- **AI Full-Stack MERN Bug Fixes** ($15 fixed) → SKIP: $9.71 avg spend per hire, poverty wages

### Previous Active Proposals (2026-06-14 to 06-15 — still waiting)
- **React.js / Next.js Small Fix** — $30 fixed · US, 5.0★/190 reviews · `C:\UpWork\React-NextJS-Small-Fix\PROPOSAL.md`
- **Full-Stack React + Node.js — Booking Platform** — $2,500 fixed · Australia · filter word: "deploy" · `C:\UpWork\FullStack-ReactNode-Booking\PROPOSAL.md`
- **React Native Live-Streaming App** — $50/hr × 22–24 weeks (~$44–48K) · Submitted, waiting
- **React Native Refer & Earn + Payment Gateway** — $1,800 fixed · Submitted, waiting

---

## Portfolio Management Workflow

**Repository:** `upwork-portfolio` (GitHub, master branch)

This is your central portfolio repo tracking all shareable website projects. Every portfolio item lives here, with preview servers configured in `launch.json` for local testing.

### Portfolio Structure

```
C:\UpWork\Websites Portfolio\
├── Quillo (AI Writing SaaS landing page)
│   ├── index.html (1,365 lines, animated SaaS landing page)
│   └── Netlify URL: https://quillo-ai-writer.netlify.app/
│
├── Creative Agency Portfolio (Meridian)
│   ├── index.html (animated creative portfolio)
│   └── Netlify URL: https://meridian-creative.netlify.app/
│
├── FashionApparel Store (Lumira)
│   ├── index.html (e-commerce with filters & cart)
│   └── Netlify URL: https://lumira-apparel.netlify.app/
│
├── Nexvolt (Analytics automation SaaS)
│   ├── index.html (premium animated landing page)
│   └── Netlify URL: (to be deployed)
│
├── SalesFunnel (ConversionOS — funnel course)
│   ├── index.html      (Page 1: Lead Magnet opt-in)
│   ├── sales.html      (Page 2: Sales Page — pricing toggle, FAQ)
│   ├── upsell.html     (Page 3: Order Bump / one-time offer)
│   ├── thankyou.html   (Page 4: Confirmation — receipt math)
│   ├── styles.css      (shared design system)
│   ├── script.js       (form carry-through, accordion, count-up)
│   └── Netlify URL: (to be deployed)
│   NOTE: 4-page funnel, "FLUX" design system (unique, NOT a
│   template copy): animated aurora mesh (magenta→violet→indigo
│   →cyan), film-grain, Bricolage Grotesque + JetBrains Mono type,
│   mono [ 01 — ... ] kickers, animated gradient headlines.
│   
│   SIGNATURE FEATURE: "funnel-thread" spine
│   - Gradient line runs top→bottom center
│   - Nodes (01/02/03) sit ON the line at card top-center
│   - Cards are opaque so line only shows in gaps (clean connection)
│   - Cards narrow as you descend: 01 (100%) → 02 (86%) → 03 (72%)
│   - Visual metaphor: "each step narrows the journey"
│   
│   DESIGN UPDATES (fixed):
│   - Replaced all emoji with clean SVG icons (stroke style)
│   - Features: target, staircase, shield-check
│   - Why section: grid, line-chart, bolt
│   - Badges: star, lock
│   - Removed decorative emoji from strips
│   - Professional, coherent visual language
│   
│   EMAIL CARRY-THROUGH: sessionStorage via Page 1 → Page 2+ flow.
│   ORDER BUMP MATH: $197 + $37 = $234 (verified on thank-you).
│   
│   PERF LEARNINGS:
│   - Heavy blur + many backdrop-filters + scale() keyframes → renderer
│     wedge (screenshot timeouts). Fixes: translate-only drift, limit
│     backdrop-filter to nav/optin/pricing, self-halting rAF, prefers
│     -reduced-motion support.
│   - Browser CSS cache can cause stale styles. Solution: restart the
│     preview server to force fresh renderer + clean CSS load.
│
├── RealEstate (Estate — appointment-booking landing)
│   ├── index.html      (single long landing page)
│   ├── styles.css      (monochrome editorial design system)
│   ├── script.js       (nav, reveal, FAQ, booking-form validation)
│   ├── hero.mp4        (background video for hero section, 0.87 MB)
│   └── Netlify URL: https://estate-landing.netlify.app/
│   NOTE: Editorial monochrome aesthetic (VERDANT-inspired —
│   black/white/warm-gray, huge condensed Archivo wordmark, stats
│   band, full-bleed showcases, dark statement sections, marquee).
│   PURPOSE: maximize "Book Appointment" conversions — CTA repeated
│   in nav, hero (×2), about, dark statement, every listing, marquee,
│   + a dedicated booking form (name/phone/email/interest/date/time
│   /message → validation → success state).
│   HERO VIDEO: Muted autoplay background video (hero.mp4) set at 22%
│   opacity with a dark gradient overlay (62%→92% black) on top, so
│   all text remains fully legible. Video loops infinitely, works on
│   mobile + desktop, playsinline for iOS.
│   ALL content is placeholder: styled image placeholders (hatch +
│   "IMAGE · ratio" labels) and lorem/[placeholder] text, so the
│   client drops in their own photos & copy. Replicates the design
│   structure, NOT VERDANT's actual copy (avoids copying their text).
│
├── SaasDashboard (Pulse — Revenue Intelligence dashboard)
│   ├── index.html      (self-contained: inline CSS + JS)
│   ├── (design refs)   (DashfolioNEO Framer screenshots used as inspo)
│   └── Netlify URL: (to be deployed)
│   NOTE: Real SaaS analytics/admin dashboard (HIGH-VALUE piece —
│   separates profile from "landing page only" builders). Built from
│   DashfolioNEO's DNA (grouped dark sidebar shell, premium near-black
│   theme, live clock, stack tile grid) but turned into an actual
│   working analytics dashboard, NOT a portfolio site.
│   PRODUCT: "Pulse" — invented SaaS, revenue intelligence.
│   SIGNATURE (unique, not generic):
│   - Animated EKG "pulse-line" logo (gradient stroke)
│   - Spectral accent gradient: emerald #34d399 → cyan #22d3ee →
│     indigo #818cf8 → fuchsia #e879f9 (distinct from the violet
│     aurora used in SalesFunnel/Nexvolt)
│   - Conic "halo" glow behind the featured Revenue KPI
│   LAYOUT: fixed sidebar (workspace switcher + grouped nav +
│   upgrade card + user), sticky topbar (search ⌘K, live clock,
│   notifications, gradient Invite), 4 KPI cards w/ count-up +
│   sparklines, big Revenue area chart, Traffic donut, goal bars,
│   transactions table, live activity feed, integrations grid.
│   CHARTS: hand-rolled animated SVG (NO Chart.js) — area chart with
│   Catmull-Rom smoothing + stroke-draw + gradient fill + hover
│   tooltip, sparklines, conic-segment donut, progress bars. Shows
│   real JS skill & keeps it self-contained (only ext = Google Fonts).
│   ANIM: count-ups (IntersectionObserver), chart draw, donut sweep,
│   bar fills, drifting bg mesh (translate-only for perf), live clock,
│   shimmer on upgrade card, hover lifts. prefers-reduced-motion honored.
│   NAVIGATION (added 2026-06-15, commit 16e08fc): All 10 sidebar nav
│   items now switch pages with smooth fade animation. Each page has
│   full unique content. Topbar title + breadcrumb update on switch.
│   Pages: Dashboard | Analytics | Revenue | Customers | Reports |
│   Funnels | Integrations | Activity | Settings | Help & Docs.
│   Interactive: customer search+filter chips, activity type filter,
│   settings save toast, analytics chart + heatmap lazy-draw on first
│   visit, download/connect button toasts. No console errors.
│
├── Axion (AI Workflow Automation landing page)
│   ├── index.html      (self-contained: inline CSS + JS + Three.js CDN)
│   └── Netlify URL: https://useaxion.netlify.app/
│   NOTE: FLAGSHIP elite-tier portfolio piece. Three.js WebGL GLSL shader orb,
│   scroll-locked narrative (4 chapters), physics spring micro-animations,
│   cursor-spotlight cards, 3D tilt panels, magnetic buttons, intro preloader,
│   scroll progress bar, kinetic typography, SVG line-draw, particle mesh canvas.
│   Built from Xtract Framer template DNA (purple/black, glowing orb hero).
│
├── Salt & Ember (Bespoke Catering landing page)
│   ├── index.html      (self-contained: inline CSS + JS)
│   └── Netlify URL: https://salt-and-ember-catering.netlify.app/
│   NOTE: Premium editorial catering brand. Warm parchment palette (--bg #F4EEE1),
│   Fraunces serif + DM Sans + JetBrains Mono type system. Showcases: hospitality
│   industry web design, editorial layout, warm luxury aesthetic.
│
└── [Future pieces: Restaurant/Food ordering, Blog/CMS, etc.]
```

### How to Build a New Portfolio Item

**Step 1: Create Folder & File**
```bash
cd "C:\UpWork\Websites Portfolio"
mkdir "FolderName"          # Every first letter capital (e.g., SalesFunnel, BlogTemplate)
cd "FolderName"
# Create index.html with complete self-contained code (inline styles + scripts)
```

**Step 2: Add Preview Server to launch.json**

Edit `C:\UpWork\.claude\launch.json` and add a new configuration:
```json
{
  "name": "kebab-case-name",
  "runtimeExecutable": "C:\\Users\\PC\\AppData\\Local\\Programs\\Python\\Python313\\python.exe",
  "runtimeArgs": ["-m", "http.server", "PORT", "--directory", "FULL_PATH_TO_FOLDER"],
  "port": PORT
}
```

**Example (for Sales Funnel):**
```json
{
  "name": "sales-funnel",
  "runtimeExecutable": "C:\\Users\\PC\\AppData\\Local\\Programs\\Python\\Python313\\python.exe",
  "runtimeArgs": ["-m", "http.server", "3004", "--directory", "C:\\UpWork\\Websites Portfolio\\SalesFunnel"],
  "port": 3004
}
```

**Ports already used:**
- 3000: Quillo (AI Writing Assistant)
- 3001: Meridian (Creative Agency Portfolio)
- 3002: Lumira (FashionApparel Store)
- 3003: Nexvolt (Analytics SaaS)
- 3004+: Available for new items

**Step 3: Test Locally**

```bash
# In Claude Code, click the Server icon and select the preview server name
# Or press F1 and type "Preview: Start Server"
# Then open http://localhost:PORT to verify
```

**Step 4: Deploy to Netlify**

```bash
# Install Netlify CLI (one-time)
npm install -g netlify-cli

# Login to Netlify
netlify login

# Deploy the folder
cd "C:\UpWork\Websites Portfolio\FolderName"
netlify deploy --prod

# Copy the live URL → add to Upwork portfolio item
```

**Step 5: Add to Upwork Portfolio**

1. Go to https://www.upwork.com/freelancers/settings/portfolio
2. Click "Add portfolio item"
3. Paste the Netlify URL
4. Add title, description, skills, and your role
5. Publish

### Quick Reference: Preview Server Ports

| Portfolio Item | Folder Name | Port | Launch.json Entry |
|---|---|---|---|
| Quillo | AI Writing Assistant | 3000 | ai-writing-assistant ✅ |
| Meridian | Creative Agency Portfolio | 3001 | creative-agency ✅ |
| Lumira | FashionApparel Store | 3002 | fashion-store ✅ |
| Nexvolt | Nexvolt | 3003 | nexvolt ✅ |
| ConversionOS (Sales Funnel) | SalesFunnel | 3004 | sales-funnel ✅ |
| Estate (Real Estate landing) | RealEstate | 3005 | real-estate ✅ |
| Pulse (SaaS Dashboard) | SaasDashboard | 3006 | saas-dashboard ✅ |
| Axion (AI Workflow Automation) | Axion | 3008 | axion ✅ |
| Salt & Ember (Catering) | Catering | 3009 | catering ✅ |
| Blog/CMS | BlogTemplate | 3007 | blog-template (to add) |

### Workflow When Building New Portfolio Pieces

1. **Plan** — Decide what to build (Sales Funnel, Blog, Dashboard, etc.)
2. **Code** — Build in `C:\UpWork\Websites Portfolio\[FolderName]\index.html`
3. **Configure** — Add preview server to `launch.json`
4. **Test** — Preview locally via Claude Code server
5. **Deploy** — Push to Netlify via CLI
6. **Portfolio** — Add live URL to Upwork portfolio section
7. **Git** — Commit to master branch: `git add . && git commit -m "Add [Name] portfolio item"`
8. **Done** — Item now live and searchable on Upwork

### High-Quality Design Techniques (Make Portfolio Pieces NON-Generic)

These separate a premium, hireable portfolio piece from a generic template clone.
Layer several on top of any build — they signal real front-end skill and justify
higher rates. Use Opus + high thinking for these flagship builds (complex animation
math — scroll-progress, magnetic physics, canvas particles). Don't be cheap on
flagship pieces.

**Signature "scroll-locked narrative" effect** (user specifically wants this):
- Viewport FREEZES (position: sticky / pinned section) while content animates through
  3–5 "chapters" as the user scrolls. The page doesn't move; the content transforms in
  place (like Apple iPhone product pages). Driven by scroll progress (sticky wrapper +
  scroll listener mapped to a 0→1 progress value).

**Effect toolkit (mix & match, don't use ALL at once):**
1. **Scroll-locked narrative** — pinned section, content morphs by scroll progress
2. **Grain / film-noise overlay** — SVG feTurbulence at low opacity → tactile premium feel
3. **Magnetic buttons** — pull toward cursor on hover, spring back on leave
4. **Kinetic typography** — split headline into spans, staggered weight/scale/y on reveal
5. **SVG line-draw** — diagrams draw themselves via stroke-dashoffset on scroll into view
6. **Glassmorphism cards** — frosted backdrop-blur; LIMIT count (perf)
7. **Particle / mesh background** — subtle dots reacting to mouse; keep count low
8. **Conic / radial glow orbs** — large blurred gradient orbs, TRANSLATE-only drift
9. **Custom cursor** — dot + trailing ring that scales on interactive hover

**PERF GUARDRAILS (learned hard — see SalesFunnel notes):** Heavy blur + many
backdrop-filters + scale() keyframes → renderer wedge / screenshot timeouts. Fixes:
translate-only drift (NEVER animate scale on big blurred elements), limit backdrop-filter
to a few elements, self-halting rAF, ALWAYS add prefers-reduced-motion that disables the
heavy stuff. Restart preview server if CSS looks stale (browser cache).

**Reference build:** Axion (AI Workflow Automation, port 3008) — built from Xtract Framer
template DNA (purple/black, glowing orb hero) but turned original with scroll-locked
narrative, grain overlay, magnetic buttons, kinetic type, SVG line-draw, particle mesh.

**ELITE TIER (push a flagship piece from "great" to "top 1%"):** When a client/piece
justifies maximum effort, layer these on top of the toolkit above:
- **Real 3D / WebGL (Three.js)** — replace CSS orbs/mockups with an actual GLSL shader
  object (distortion sphere, particle field) that reacts to mouse + morphs through scroll
  chapters. THE single biggest "this dev is real" upgrade. Costs one CDN dependency
  (breaks strict self-contained rule — acceptable for flagship pieces; pin the version).
- **Physics-based micro-animations** — spring/inertia/momentum instead of CSS easing.
  Magnetic buttons that spring back, orb with rotational momentum, elastic card lifts.
  Subtle but makes everything *feel* expensive. Can hand-roll a tiny spring (lerp toward
  target with velocity) — no library needed.
- **Scroll-triggered storytelling** — already covered by scroll-locked narrative; deepen
  it by tying the 3D object's shader uniforms (distortion, color, rotation) to scroll
  progress so the visual literally tells the story.

---

## PEAK TECHNIQUES — The Absolute Best of the Best in Web Design

These are the world-class techniques used by studios that win Awwwards SOTD, FWA, and
work for Apple/Nike/Google. No trade-off thinking — pure capability reference.

### #1 WORLD PEAK — SDF Ray Marching (GLSL Fragment Shaders)
The single most advanced visual technique in web design. Used by: Active Theory, Resn,
Unit9, Inigo Quilez (shadertoy.com author). Renders infinite 3D detail with ZERO geometry
— every pixel is computed by solving a ray-distance equation in the GPU fragment shader.
Can produce: volumetric fog, subsurface scattering, soft shadows, ambient occlusion,
infinite procedural worlds — all from pure math, no 3D models needed.

```glsl
// SDF Ray Marching — core loop in fragment shader
float map(vec3 p) {
  return length(p) - 1.0; // sphere SDF — distance to surface
}
vec3 rayMarch(vec3 ro, vec3 rd) {
  float t = 0.0;
  for(int i=0; i<100; i++) {
    vec3 p = ro + rd * t;
    float d = map(p);           // how far to nearest surface
    if(d < 0.001) break;        // hit
    t += d;                     // march forward
    if(t > 100.0) break;        // miss
  }
  return ro + rd * t;
}
```
**Domain warping** (fbm noise warped by fbm) creates organic, fluid, biological patterns.
**Reaction-diffusion** simulated on GPU ping-pong FBO creates living, growing patterns.
**Implementation:** raw WebGL or Three.js ShaderMaterial on a fullscreen quad.

---

### TIER 2 — WebGPU Compute Shaders (WGSL)
Next generation beyond WebGL. Runs physics simulations, fluid dynamics (Navier-Stokes),
million-particle systems, ML inference, all on GPU compute — not just rendering.
Chrome 113+ only (2024). The future standard that replaces WebGL.

```wgsl
@compute @workgroup_size(64)
fn main(@builtin(global_invocation_id) id: vec3u) {
  var p = particles[id.x];
  p.velocity += gravity * uniforms.dt;
  p.position += p.velocity * uniforms.dt;
  particles[id.x] = p;
}
```
**Use for:** million-particle fluid systems, GPU cloth/rope physics, in-browser ML,
real-time audio DSP, procedural terrain generation at scale.

---

### TIER 3 — Advanced CSS (Zero-JS Techniques at Peak Level)
- **CSS Scroll-Driven Animations** (`animation-timeline: scroll()`) — full parallax,
  progress bars, reveal animations with ZERO JavaScript. Pure CSS, GPU-accelerated.
- **CSS Houdini Paint API** — register custom `paint()` worklets, draw arbitrary Canvas
  graphics as CSS backgrounds/borders. Animatable via @property. Fully GPU-composited.
- **@property typed custom properties** — animate gradient angles, colors, numeric values
  smoothly. Example: animated conic-gradient spinning via `@property --angle`.
- **CSS `has()` + container queries** — fully responsive component logic without JS.
- **View Transitions API** — native cross-page/SPA morphing animations (Chrome 111+).
  Elements with matching `view-transition-name` morph between pages automatically.
- **`color-mix()` + `oklch()`** — perceptually uniform color mixing, impossible with hex.

---

### TIER 4 — GSAP (Professional Motion Library)
The industry standard for professional animation. Used on 90% of award-winning sites.
- **GSAP ScrollTrigger** — scrub any animation to scroll position with pixel precision
- **GSAP MorphSVG** — morph between any two SVG paths (impossible in pure CSS)
- **GSAP SplitText** — split text into chars/words/lines with stagger control
- **GSAP Flip** — animate layout changes with WAAPI-backed position interpolation
- **Theatre.js** — keyframe timeline editor embedded in code, used in production films/games
CDN: `https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/gsap.min.js`

---

### TIER 5 — WebGL Advanced Techniques (Beyond Basic Three.js)
- **Framebuffer ping-pong (FBO)** — GPU texture reads/writes for simulation (fluid, smoke,
  reaction-diffusion). Each frame reads last frame's texture, writes new state.
- **PBR lighting (Physically Based Rendering)** — metalness/roughness model that matches
  real-world light behavior. Three.js MeshStandardMaterial uses this.
- **Post-processing passes** — bloom, chromatic aberration, film grain, depth of field,
  SSAO (screen-space ambient occlusion) as full-screen shader passes after main render.
- **Instanced mesh rendering** — render 100,000+ identical objects in one draw call.
- **GPU particle systems** — particle position/velocity stored in textures, updated on GPU.
- **Three.js custom ShaderMaterial** — full GLSL control over vertex + fragment stages.

---

### TIER 6 — In-Browser AI / ML
- **Transformers.js** — run Whisper (speech), BERT (NLP), small LLMs entirely in browser.
  No server. Fully private. CDN: `https://cdn.jsdelivr.net/npm/@xenova/transformers`
- **MediaPipe** — real-time body pose, hand tracking, face landmarks at 60fps on mobile.
  Use case: cursor follows user's hand, hover effects react to face position.
- **ONNX Runtime Web + WebGPU backend** — quantized diffusion models client-side.
- **TensorFlow.js** — full ML model training + inference in browser, WebGL/WebGPU backend.
- **WebNN API** — native browser hardware-accelerated neural network API (emerging standard).

---

### TIER 7 — WebAssembly (Near-Native Speed)
- **Rust → WASM (wasm-pack)** — image codecs, physics engines, encryption, audio DSP
  at near-native speed in the browser. 10-100× faster than JavaScript for compute tasks.
- **WASM SIMD** — vectorized math, 4× throughput for signal processing / ML kernels.
- **WASM threads + SharedArrayBuffer** — true multi-core parallelism in the browser tab.
- **Use case:** real-time video processing, ZIP/PDF generation, game physics, synths.

---

### TIER 8 — Advanced Web Platform APIs
- **WebRTC** — P2P video/audio/data, full video calling with no media server.
- **WebTransport (QUIC)** — ultra-low-latency bidirectional streaming, better than WS for games.
- **Web Audio API** — synthesizers, real-time DSP, spatial 3D audio, FFT visualization.
- **WebXR** — AR/VR in browser, hand tracking, spatial anchors (use ONLY for spatial projects).
- **File System Access API** — read/write local files directly (desktop app replacement).
- **Web Bluetooth** — connect to BLE devices from the browser.
- **Screen Wake Lock** — prevent screen sleep during video/presentation.

---

### ADVANCED CSS TECHNIQUE REFERENCE (copy-paste patterns)

```css
/* CSS Scroll-Driven Animation — zero JS parallax */
@keyframes parallax { from { transform: translateY(0); } to { transform: translateY(-200px); } }
.hero { animation: parallax linear; animation-timeline: scroll(); animation-range: 0 50vh; }

/* @property — animatable gradient angle */
@property --angle { syntax: '<angle>'; inherits: false; initial-value: 0deg; }
.card { background: conic-gradient(from var(--angle), #7c3aed, #06b6d4); transition: --angle 600ms; }
.card:hover { --angle: 360deg; }

/* View Transition API — morph elements between pages */
.hero-img { view-transition-name: hero; }
/* In CSS: */ ::view-transition-old(hero) { animation: fade-out 0.3s; }
            ::view-transition-new(hero) { animation: fade-in 0.3s; }

/* CSS Houdini Paint Worklet */
CSS.paintWorklet.addModule('noise-paint.js');
.card { background: paint(noise); }

/* container queries — component-level responsive */
@container (min-width: 400px) { .card { grid-template-columns: 1fr 1fr; } }
```

---

### REFERENCE: Award-Winning Studio Techniques (What Actually Wins Awwwards)
1. **SDF ray marching** — #1 differentiator, no other technique looks like it
2. **GSAP + ScrollTrigger** — smooth, scrubbed narrative animations
3. **Custom GLSL shaders on 3D objects** — morphing, distortion, color grading
4. **Reaction-diffusion / Turing patterns** on GPU FBO — organic, living visuals
5. **Physics simulations** — cloth, fluid, rope that reacts to mouse
6. **Kinetic typography at extreme scale** — full-viewport text that animates on scroll
7. **Noise-driven procedural color** — no flat colors, always fbm/simplex-driven gradients
8. **Micro-interaction density** — every hover, click, focus has a spring-physics response
9. **Zero lorem ipsum** — real invented brand copy makes the design feel real
10. **prefers-reduced-motion respect** — accessibility signals professionalism

**WHEN NOT TO REACH FOR THESE (be honest with the user, don't gold-plate):**
- **AR/VR (WebXR)** — WRONG tool for a marketing/landing page. Needs a headset or AR
  phone to experience, shows NOTHING in a portfolio screenshot, huge complexity for
  near-zero conversion value. Only use when the project genuinely warrants it (3D product
  viewer, real-estate walkthrough, spatial demo). Tell the client this instead of building
  a gimmick.
- **WebGL perf guardrails (MANDATORY when using Three.js):** pause the render loop when
  the canvas is off-screen (IntersectionObserver), cap `renderer.setPixelRatio(Math.min(
  devicePixelRatio, 2))`, dispose geometry/material on teardown, and ALWAYS provide a
  `prefers-reduced-motion` + WebGL-unavailable fallback to the lightweight CSS orb. WebGL
  can wedge the renderer (same class of bug as the heavy-blur warning above).

### Important Notes

- **Every portfolio item = 1 self-contained HTML file** (inline CSS + JS, only external imports = Google Fonts / Netlify API if needed)
- **No framework complexity** — Vanilla HTML/CSS/JavaScript only (easier to maintain, faster to load)
- **Netlify = free hosting** — Deploy as many as you want
- **Git master = source of truth** — Always commit working portfolio code
- **One item per folder** — Keeps organization clean
- **Responsive design** — Test on mobile (375px), tablet (768px), desktop (1024px+)
