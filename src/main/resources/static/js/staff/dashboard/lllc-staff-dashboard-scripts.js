$(document).ready(function () {
    $.get("/api/v1/dashboard/order-stats", function (stats) {
        const months = ["Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec"];

        const revenueMap = Array(12).fill(0);

        (stats.monthlyRevenue || stats.revenues || []).forEach((val, i) => {
            revenueMap[i] = val ?? 0;
        });

        Chart.defaults.global.defaultFontFamily =
            'Nunito, -apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';

        Chart.defaults.global.defaultFontColor = '#858796';

        new Chart(document.getElementById("revenueGraphChart"), {
            type: 'line',
            data: {
                labels: months,

                datasets: [{
                    label: "Earnings",
                    lineTension: 0.3,
                    backgroundColor: "rgba(122, 17, 19, 0.10)",
                    borderColor: "#7a1113",
                    pointRadius: 3,
                    pointBackgroundColor: "#7a1113",
                    pointBorderColor: "#7a1113",
                    pointHoverRadius: 4,
                    pointHoverBackgroundColor: "#5c0d0f",
                    pointHoverBorderColor: "#5c0d0f",
                    pointHitRadius: 10,
                    pointBorderWidth: 2,
                    data: revenueMap
                }]
            },

            options: {
                maintainAspectRatio: false,

                scales: {
                    yAxes: [{
                        ticks: {
                            callback: function(value) {
                                return '₱' + value.toLocaleString();
                            }
                        }
                    }]
                },
                legend: {
                    display: false
                }
            }
        });


        /* Order by Status Doughnut Chart */
        const ctx = document.getElementById("orderStatusPieChart");

        const { created, prepared, shipped, delivered, returned, cancelled } = stats;

        const myPieChart = new Chart(ctx, {
            type: 'doughnut',

            data: {
                labels: ["Created", "Prepared", "Shipped", "Delivered/Rated", "Returned/Refunded", "Cancelled"],
                datasets: [{
                    data: [created, prepared, shipped, delivered, returned, cancelled],
                    backgroundColor: [
                        '#7a1113', // maroon
                        '#c1121f', // red
                        '#e63946', // light red
                        '#f77f00', // orange
                        '#fcbf49', // yellow
                        '#5c0d0f'  // dark maroon
                    ],
                    hoverBackgroundColor: [
                        '#5c0d0f',
                        '#a10f17',
                        '#d62828',
                        '#e85d04',
                        '#f4a261',
                        '#3d0a0b'
                    ],
                    hoverBorderColor: "rgba(234, 236, 244, 1)",
                }],
            },

            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutoutPercentage: 75,
                layout: {
                    padding: {
                        top: 0,
                        bottom: 10
                    }
                },
                tooltips: {
                    backgroundColor: "rgb(255,255,255)",
                    bodyFontColor: "#858796",
                    borderColor: '#dddfeb',
                    borderWidth: 1,
                    xPadding: 15,
                    yPadding: 15,
                    displayColors: false,
                    caretPadding: 10,
                },
                legend: {
                    display: true,
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        boxWidth: 10,
                        padding: 10,
                        fontSize: 12
                    }
                }
            }
        });
    });

    $(document).on("click", ".view-order", function (e) {
        e.preventDefault();

        const orderNumber = $(this).data("order");
        const encoded = btoa(orderNumber);
        window.location.href = "/telatak/seller/orders/view-order/" + encoded;
    });
});