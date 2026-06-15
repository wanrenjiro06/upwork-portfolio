/* =========================================================
   ConversionOS — Sales Funnel — shared interactions
   ========================================================= */
(function () {
  "use strict";

  /* ---- Mobile nav toggle ---- */
  const nav = document.querySelector(".nav");
  const toggle = document.querySelector(".nav__toggle");
  if (toggle && nav) {
    toggle.addEventListener("click", () => nav.classList.toggle("open"));
  }

  /* ---- Scroll reveal ---- */
  const revealEls = document.querySelectorAll(".reveal");
  if ("IntersectionObserver" in window && revealEls.length) {
    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((e) => {
          if (e.isIntersecting) {
            e.target.classList.add("in");
            io.unobserve(e.target);
          }
        });
      },
      { threshold: 0.14 }
    );
    revealEls.forEach((el) => io.observe(el));
  } else {
    revealEls.forEach((el) => el.classList.add("in"));
  }

  /* ---- Count-up stats ---- */
  const counters = document.querySelectorAll("[data-count]");
  if (counters.length && "IntersectionObserver" in window) {
    const cio = new IntersectionObserver(
      (entries) => {
        entries.forEach((e) => {
          if (!e.isIntersecting) return;
          const el = e.target;
          const target = parseFloat(el.dataset.count);
          const prefix = el.dataset.prefix || "";
          const suffix = el.dataset.suffix || "";
          const decimals = (el.dataset.count.split(".")[1] || "").length;
          const dur = 1400;
          const start = performance.now();
          function tick(now) {
            const p = Math.min((now - start) / dur, 1);
            const eased = 1 - Math.pow(1 - p, 3);
            const val = (target * eased).toFixed(decimals);
            el.textContent = prefix + Number(val).toLocaleString() + suffix;
            if (p < 1) requestAnimationFrame(tick);
          }
          requestAnimationFrame(tick);
          cio.unobserve(el);
        });
      },
      { threshold: 0.5 }
    );
    counters.forEach((c) => cio.observe(c));
  }

  /* ---- FAQ accordion ---- */
  document.querySelectorAll(".faq__q").forEach((q) => {
    q.addEventListener("click", () => {
      const item = q.closest(".faq__item");
      const ans = item.querySelector(".faq__a");
      const isOpen = item.classList.contains("open");
      document.querySelectorAll(".faq__item.open").forEach((openItem) => {
        openItem.classList.remove("open");
        openItem.querySelector(".faq__a").style.maxHeight = null;
      });
      if (!isOpen) {
        item.classList.add("open");
        ans.style.maxHeight = ans.scrollHeight + "px";
      }
    });
  });

  /* ---- Pricing monthly / one-time toggle (sales page) ---- */
  const planToggle = document.querySelector("[data-plan-toggle]");
  if (planToggle) {
    planToggle.addEventListener("click", () => {
      planToggle.classList.toggle("on");
      const monthly = planToggle.classList.contains("on");
      document.querySelectorAll("[data-price-now]").forEach((el) => {
        el.textContent = monthly ? el.dataset.monthly : el.dataset.once;
      });
      document.querySelectorAll("[data-price-note]").forEach((el) => {
        el.textContent = monthly ? "per month, billed monthly" : "one-time payment, lifetime access";
      });
    });
  }

  /* ---- Lead capture form → carry email to next step ---- */
  const leadForm = document.querySelector("[data-lead-form]");
  if (leadForm) {
    leadForm.addEventListener("submit", (ev) => {
      ev.preventDefault();
      const emailInput = leadForm.querySelector('input[type="email"]');
      const errBox = leadForm.querySelector(".optin__error");
      const email = (emailInput.value || "").trim();
      const valid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
      if (!valid) {
        if (errBox) errBox.textContent = "Please enter a valid email address.";
        emailInput.focus();
        return;
      }
      if (errBox) errBox.textContent = "";
      try { sessionStorage.setItem("cos_email", email); } catch (e) {}
      const btn = leadForm.querySelector("button[type=submit]");
      if (btn) { btn.disabled = true; btn.textContent = "Sending your blueprint…"; }
      setTimeout(() => { window.location.href = "sales.html"; }, 700);
    });
  }

  /* ---- Greet returning lead by email (sales / upsell / thankyou) ---- */
  const greet = document.querySelector("[data-greet-email]");
  if (greet) {
    let email = "";
    try { email = sessionStorage.getItem("cos_email") || ""; } catch (e) {}
    if (email) greet.textContent = email;
  }

  /* ---- Year stamp ---- */
  document.querySelectorAll("[data-year]").forEach((el) => {
    el.textContent = new Date().getFullYear();
  });

  /* ---- Cursor-reactive aurora parallax ----
     Moves the whole aurora field so per-blob keyframe drift stays intact.
     Self-halting: the rAF loop stops once the cursor settles so the page
     can reach an idle state (and isn't burning frames forever). */
  const aurora = document.querySelector(".aurora");
  const reduceMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
  if (aurora && !reduceMotion && window.matchMedia("(pointer:fine)").matches) {
    let tx = 0, ty = 0, cx = 0, cy = 0, running = false;
    function loop() {
      cx += (tx - cx) * 0.06;
      cy += (ty - cy) * 0.06;
      aurora.style.transform = `translate(${cx * 26}px, ${cy * 26}px)`;
      if (Math.abs(tx - cx) > 0.001 || Math.abs(ty - cy) > 0.001) {
        requestAnimationFrame(loop);
      } else {
        running = false; // settled → go idle
      }
    }
    window.addEventListener("mousemove", (e) => {
      tx = (e.clientX / window.innerWidth - 0.5) * 2;
      ty = (e.clientY / window.innerHeight - 0.5) * 2;
      if (!running) { running = true; requestAnimationFrame(loop); }
    });
  }
})();
