// function injectSweetAlertStyles() {
//   const style = document.createElement('style');
//
//   style.innerHTML = `
//         /* =========================
//            POPUP (COMPACT CARD)
//         ========================= */
//         .custom-swal-popup {
//             width: 320px !important;
//     height: auto !important;
//     min-height: 250px !important;
//
//             border-radius: 24px !important;
//
//             padding: 1.4rem 1rem 1.2rem 1rem !important;
//
//             background: #ffffff !important;
//
//             box-shadow:
//                 0 10px 40px rgba(0, 0, 0, 0.08),
//                 0 2px 10px rgba(0, 0, 0, 0.04) !important;
//
//             border: 1px solid rgba(0, 0, 0, 0.04);
//
//             font-family: 'Inter', sans-serif;
//
//             display: flex !important;
//             flex-direction: column !important;
//             justify-content: center !important;
//             text-align: center !important;
//
//         }
//
//         .swal2-container {
//     overflow-y: hidden !important;
// }
//
//         /* =========================
//            ICON (CLEAN STYLE)
//         ========================= */
//         .swal2-icon {
//             margin: -0.3rem auto 0.3rem auto !important;
//         }
//
//         .swal2-success-ring {
//             border: 2px solid rgba(249, 191, 41, 0.22) !important;
//         }
//
//         .swal2-success-line-tip,
//         .swal2-success-line-long {
//             background-color: #f9bf29 !important;
//             height: 2.5px !important;
//         }
//
//         .swal2-success-circular-line-left,
//         .swal2-success-circular-line-right,
//         .swal2-success-fix {
//             background: transparent !important;
//         }
//
//         /* =========================
//            TITLE
//         ========================= */
//         .swal2-title {
//             font-size: 1.1rem !important;
//             font-weight: 700 !important;
//             color: #1f2937 !important;
//
//             margin: 0.2rem 0 0.1rem 0 !important;
//             padding: 0 !important;
//
//             line-height: 1.2;
//             text-align: center !important;
//         }
//
//         /* =========================
//            MESSAGE (moved down)
//         ========================= */
//         .swal2-html-container {
//     font-size: 0.85rem !important;
//     color: #6b7280 !important;
//
//     margin: 1rem auto 0 auto !important;
//
//     padding: 0 !important;
//
//     line-height: 1.5 !important;
//     text-align: center !important;
//
//     width: 100% !important;
//     max-width: 260px !important;
//
//     overflow: visible !important;      /* 👈 remove scroll */
//     overflow-y: visible !important;    /* 👈 remove vertical scrollbar */
//
//     word-break: break-word !important;
//     white-space: normal !important;
//
//     display: flex !important;
//     justify-content: center !important;
//     align-items: center !important;
// }
//         /* =========================
//            ACTIONS (button container)
//         ========================= */
//         .swal2-actions {
//             margin-top: 1.3rem !important;  /* 👈 push button downward */
//             margin-bottom: 0 !important;
//
//             display: flex !important;
//             justify-content: center !important;
//         }
//
//         /* =========================
//            BUTTON
//         ========================= */
//         .custom-confirm-button {
//             background: linear-gradient(135deg, #f9bf29, #f59e0b) !important;
//             color: #fff !important;
//
//             border: none !important;
//             padding: 8px 18px !important;
//
//             border-radius: 10px !important;
//
//             font-size: 0.85rem !important;
//             font-weight: 600 !important;
//
//             box-shadow: 0 6px 14px rgba(249, 191, 41, 0.35) !important;
//
//             transition: all 0.2s ease !important;
//         }
//
//         .custom-confirm-button:hover {
//             transform: translateY(-2px);
//             box-shadow: 0 10px 18px rgba(249, 191, 41, 0.45) !important;
//         }
//
//         .custom-confirm-button:active {
//             transform: scale(0.98);
//         }
//
//         /* =========================
//            ANIMATION
//         ========================= */
//         .swal2-show {
//             animation: smoothFadeIn 0.25s ease-out;
//         }
//
//         @keyframes smoothFadeIn {
//             from {
//                 opacity: 0;
//                 transform: translateY(10px) scale(0.97);
//             }
//             to {
//                 opacity: 1;
//                 transform: translateY(0) scale(1);
//             }
//         }
//     `;
//
//   document.head.appendChild(style);
// }
//
// function showAlert(title, text) {
//   Swal.fire({
//     title: title, //Are you sure?
//     text: text, //This action cannot be undone!
//     icon: "warning",
//     showCancelButton: true,
//     confirmButtonText: "Yes",
//     cancelButtonText: "Cancel",
//   });
// }
//
// function showInfo(title, text) {
//   Swal.fire({
//     title: title,
//     text: text,
//     icon: "info",
//     confirmButtonText: "OK",
//     customClass: {
//       popup: "custom-swal-popup",
//       confirmButton: "custom-info-ok-button",
//     },
//     showClass: {
//       popup: "swal2-show",
//     },
//   });
// }
//
// function showError(title, text) {
//   Swal.fire({
//     title: title,
//     text: text,
//     icon: "error",
//     confirmButtonText: "OK",
//     customClass: {
//       popup: "custom-swal-popup",
//       confirmButton: "custom-error-ok-button",
//     },
//     showClass: {
//       popup: "swal2-show",
//     },
//   });
// }
//
// function showConfirmation(title, text) {
//   Swal.fire({
//     title: title,
//     text: text,
//     icon: "warning",
//     showCancelButton: true,
//     confirmButtonText: "Yes",
//     customClass: {
//       popup: "custom-swal-popup",
//       confirmButton: "custom-confirm-yes-button",
//     },
//     showClass: {
//       popup: "swal2-show",
//     },
//     cancelButtonText: "No",
//     customClass: {
//       popup: "custom-swal-popup",
//       cancelButton: "custom-confirm-cancel-button",
//     },
//     showClass: {
//       popup: "swal2-show",
//     },
//   }).then((result) => {
//     if (result.isConfirmed) {
//       $item.remove();
//       updateTotalPayment();
//     }
//   });
// }
//
// // function showSuccess(title, text) {
// //   Swal.fire({
// //     title: title, //Success!
// //     text: text, //Added to cart successfully.
// //     icon: "success",
// //     confirmButtonText: "OK",
// //     confirmButtonColor: "#f9bf29",
// //     customClass: {
// //       popup: "custom-swal-popup",
// //       confirmButton: "custom-confirm-button",
// //     },
// //     showClass: {
// //       popup: "swal2-show",
// //     },
// //     button: {
// //       text: "OK",
// //       className: "swal-btn-success"
// //     }
// //   });
// // }
//
// function showSuccessThenRedirect(title, text, url) {
//   Swal.fire({
//     title: title,
//     text: text,
//     icon: "success",
//     confirmButtonText: "OK",
//     confirmButtonColor: "#f9bf29",
//     customClass: {
//       popup: "custom-swal-popup",
//       confirmButton: "custom-confirm-button",
//     },
//     showClass: {
//       popup: "swal2-show",
//     },
//   }).then(() => {
//     // Redirect only after popup is closed
//     if (url) window.location.href = url;
//   });
// }
//
// function showSuccess(title, text) {
//   Swal.fire({
//     title: title,
//     text: text,
//     icon: "success",
//     confirmButtonText: "OK",
//     confirmButtonColor: "#f9bf29",
//     customClass: {
//       popup: "custom-swal-popup",
//       confirmButton: "custom-confirm-button",
//     },
//     showClass: {
//       popup: "swal2-show",
//     }
//   });
// }
//
// function confirmOrder(title, text, confirmUrl) {
//   Swal.fire({
//     title: title,
//     text: text,
//     icon: "warning",
//     showCancelButton: true,
//     confirmButtonText: "Confirm",
//     cancelButtonText: "Cancel",
//     confirmButtonColor: "#3085d6",
//     cancelButtonColor: "#d33",
//     customClass: {
//       popup: "custom-swal-popup",
//       confirmButton: "custom-confirm-button",
//       cancelButton: "custom-cancel-button"
//     },
//     showClass: { popup: "swal2-show" },
//     didOpen: () => {
//       // Helper to apply styles
//       const applyStyles = (el, styles) => {
//         if (!el) return;
//         Object.entries(styles).forEach(([key, value]) =>
//             el.style.setProperty(key, value, "important")
//         );
//       };
//
//       // Adjust popup height
//       const popup = document.querySelector(".swal2-popup");
//       if (popup) {
//         popup.style.setProperty("height", "260px", "important"); // change as needed
//         popup.style.setProperty("min-height", "250px", "important"); // optional
//       }
//
//       // Title
//       applyStyles(document.querySelector(".swal2-title"), {
//         "font-size": "1.3rem",
//         "font-weight": "bold",
//         color: "#333",
//         "margin-top": "-5px",
//         "margin-bottom": "0.75rem"
//       });
//
//       // Text / Content
//       applyStyles(document.querySelector(".swal2-content"), {
//         "font-size": "14px",
//         color: "#555",
//         "margin-top": "15px"
//       });
//
//       // HTML container
//       applyStyles(document.querySelector(".swal2-html-container"), {
//         "font-size": "0.9rem",
//         "margin-top": "-5px"
//       });
//
//       // Confirm button (separate)
//       const confirmBtn = document.querySelector(".custom-confirm-button");
//       if (confirmBtn) {
//         applyStyles(confirmBtn, {
//           "background-color": "#f9bf29",
//           color: "#fff",
//           "font-weight": "bold",
//           "font-size": "0.85rem",
//           padding: "0.6rem 1.5rem",
//           height: "35px",
//           "border-radius": "7px",
//           border: "none",
//           transition: "all 0.3s ease",
//           "margin-top": "-70px",
//           display: "flex",
//           "justify-content": "center",
//           "align-items": "center"
//         });
//         confirmBtn.addEventListener("mouseenter", () => {
//           confirmBtn.style.setProperty("background-color", "#e0a800", "important");
//           confirmBtn.style.setProperty("transform", "scale(1.05)", "important");
//         });
//         confirmBtn.addEventListener("mouseleave", () => {
//           confirmBtn.style.setProperty("background-color", "#f9bf29", "important");
//           confirmBtn.style.setProperty("transform", "scale(1)", "important");
//         });
//       }
//
//       // Cancel button (separate)
//       const cancelBtn = document.querySelector(".custom-cancel-button");
//       if (cancelBtn) {
//         applyStyles(cancelBtn, {
//           "background-color": "#f9bf29",
//           color: "#fff",
//           "font-weight": "bold",
//           "font-size": "0.85rem",
//           padding: "0.6rem 1.5rem",
//           "border-radius": "7px",
//           border: "none",
//           transition: "all 0.3s ease",
//           display: "flex",
//           "justify-content": "center",
//           "align-items": "center"
//         });
//         cancelBtn.addEventListener("mouseenter", () => {
//           cancelBtn.style.setProperty("background-color", "#e0a800", "important");
//           cancelBtn.style.setProperty("transform", "scale(1.05)", "important");
//         });
//         cancelBtn.addEventListener("mouseleave", () => {
//           cancelBtn.style.setProperty("background-color", "#f9bf29", "important");
//           cancelBtn.style.setProperty("transform", "scale(1)", "important");
//         });
//       }
//     }
//   }).then((result) => {
//     if (result.isConfirmed) {
//       window.location.href = confirmUrl;
//     }
//   });
// }
