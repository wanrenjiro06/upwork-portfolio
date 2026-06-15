/* =========================================================
   Real Estate landing page — interactions
   ========================================================= */
(function () {
  "use strict";

  /* Mobile nav */
  var nav = document.querySelector(".nav");
  var toggle = document.querySelector(".nav__toggle");
  if (toggle && nav) toggle.addEventListener("click", function () { nav.classList.toggle("open"); });

  /* Scroll reveal */
  var revealEls = document.querySelectorAll(".reveal");
  if ("IntersectionObserver" in window && revealEls.length) {
    var io = new IntersectionObserver(function (entries) {
      entries.forEach(function (e) { if (e.isIntersecting) { e.target.classList.add("in"); io.unobserve(e.target); } });
    }, { threshold: 0.12 });
    revealEls.forEach(function (el) { io.observe(el); });
  } else {
    revealEls.forEach(function (el) { el.classList.add("in"); });
  }

  /* FAQ accordion */
  document.querySelectorAll(".faq__q").forEach(function (q) {
    q.addEventListener("click", function () {
      var item = q.closest(".faq__i");
      var ans = item.querySelector(".faq__a");
      var isOpen = item.classList.contains("open");
      document.querySelectorAll(".faq__i.open").forEach(function (o) {
        o.classList.remove("open");
        o.querySelector(".faq__a").style.maxHeight = null;
      });
      if (!isOpen) { item.classList.add("open"); ans.style.maxHeight = ans.scrollHeight + "px"; }
    });
  });

  /* Booking form — client-side validation + placeholder success state */
  var form = document.querySelector("[data-book-form]");
  var ok = document.querySelector("[data-book-ok]");
  if (form) {
    form.addEventListener("submit", function (ev) {
      ev.preventDefault();
      var err = form.querySelector(".form__err");
      var email = (form.querySelector('input[name="email"]').value || "").trim();
      var valid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
      if (!valid) { if (err) err.textContent = "Please enter a valid email so we can confirm your appointment."; return; }
      if (err) err.textContent = "";
      form.style.display = "none";
      if (ok) ok.classList.add("show");
    });
  }

  /* Year */
  document.querySelectorAll("[data-year]").forEach(function (el) { el.textContent = new Date().getFullYear(); });
})();
