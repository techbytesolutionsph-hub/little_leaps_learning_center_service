// Set default font family and font color
Chart.defaults.global.defaultFontFamily =
  'Nunito, -apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';

Chart.defaults.global.defaultFontColor = '#858796';


// Doughnut Chart
var ctx = document.getElementById("myPieChart");

var myPieChart = new Chart(ctx, {
  type: 'doughnut',

  data: {
    labels: [
      "Created",
      "Prepared",
      "Shipped",
      "Delivered/Rated",
      "Returned/Refunded",
      "Cancelled"
    ],

    datasets: [{
      data: [20, 15, 25, 30, 5, 5],

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
        top: 0,   // 👈 acts like margin-top
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