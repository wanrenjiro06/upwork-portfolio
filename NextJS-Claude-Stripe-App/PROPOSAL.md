# Next.js Web Application Developer

## Job
$180 fixed price — Next.js + Supabase Auth + Claude API + Stripe credits + dashboard.
Australia (Sydney), payment verified + phone verified. Member since Jun 16, 2026 (new client).
10–15 proposals. Interviewing: 1. 9 connects to apply.

---

## Cover Letter

Your 2–3 week estimate can be cut to 3–4 days — this stack (Next.js + Supabase Auth + Stripe + Claude API) is something I've built combinations of before and there's no guesswork in the architecture.

Here's the exact plan: Supabase handles auth (email/password + Google OAuth via their built-in provider), Stripe Checkout handles one-time credit purchases with a webhook to top up the user's credit balance in Supabase, and each Claude API call deducts 1 credit and logs the query to a history table. The dashboard reads both in real time. Deploy to Vercel. Done.

I'll have it live and ready for your review in 3–4 days. You provide the API keys and the system prompt, I deliver the working app.

---

## Terms

- **Type:** Fixed price — 1 milestone
- **Amount:** $180
- **Timeline:** 3–4 days from project start
- **Milestone:** Fully working app deployed on Vercel — auth, credits, Claude chat, dashboard, all tested

---

## Screening Question Answers

**1. Describe your recent experience with similar projects.**

I've built Next.js apps with Supabase Auth (including Google OAuth), Stripe payment integration, and API integrations. Most recently I built a full SaaS landing page and a multi-screen admin dashboard from scratch. For this specific project — the architecture is: Next.js App Router, Supabase for auth + database (users table + credits column + query_history table), Stripe Checkout for one-time purchases with a `/api/webhook` endpoint to credit the user's balance, and a server-side API route that calls the Anthropic Claude API, deducts 1 credit, and saves the Q&A pair to history. No unnecessary complexity.

**2. Please list any certifications related to this project.**

No formal certifications — my proof is working code. You can see my live projects below (Next.js, JavaScript, responsive design, API integration):
- https://quillo-ai-writer.netlify.app/
- https://lumira-apparel.netlify.app/
- https://meridian-creative.netlify.app/

**3. Include a link to your GitHub profile and/or website.**

GitHub: https://github.com/wanrenjiro
Portfolio sites above show the quality of my frontend and JavaScript work.

**4. What frameworks have you worked with?**

Next.js (App Router + Pages Router), React, Node.js, Supabase (Auth + Database + Storage), Stripe (Checkout, webhooks, billing), Anthropic Claude API, Vercel (deployment + env vars + serverless functions), Tailwind CSS. These are exactly the tools your project needs.

---

## After Hired

- Your Supabase project URL + anon key (or I create a new project and share credentials)
- Your Stripe secret key + webhook secret (I set up the Checkout product)
- Your Anthropic API key
- The system prompt you want Claude to use
- Preferred credit package (e.g. 10 credits for $X) — I wire whatever amounts you decide
