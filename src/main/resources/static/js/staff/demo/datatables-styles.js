// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#clientTable').DataTable({
    pageLength: 5,
    lengthMenu: [
      [5, 10, 25, 50, 100],
      [5, 10, 25, 50, 100]
    ],
    order: [[0, 'desc']],
    language: {
      emptyTable: "No client registration records found"
    }
  });

  $('#userTable').DataTable({
    pageLength: 5,
    lengthMenu: [
      [5, 10, 25, 50, 100],
      [5, 10, 25, 50, 100]
    ],
    order: [[0, 'desc']],
    language: {
      emptyTable: "No user registration records found"
    }
  });

  $('#employeeTable').DataTable({
    pageLength: 5,
    lengthMenu: [
      [5, 10, 25, 50, 100],
      [5, 10, 25, 50, 100]
    ],
    order: [[0, 'desc']],
    language: {
      emptyTable: "No employee registration records found"
    }
  });

  $('#initialAssessmentTable').DataTable({
    pageLength: 5,
    lengthMenu: [
      [5, 10, 25, 50, 100],
      [5, 10, 25, 50, 100]
    ],
    order: [[0, 'desc']],
    language: {
      emptyTable: "No initial assessment records found"
    }
  });

  $('#regularAssessmentTable').DataTable({
    pageLength: 5,
    lengthMenu: [
      [5, 10, 25, 50, 100],
      [5, 10, 25, 50, 100]
    ],
    order: [[0, 'desc']],
    language: {
      emptyTable: "No therapy session records found"
    }
  });

  $('#upgradingProgramTable').DataTable({
    pageLength: 5,
    lengthMenu: [
      [5, 10, 25, 50, 100],
      [5, 10, 25, 50, 100]
    ],
    order: [[0, 'desc']],
    language: {
      emptyTable: "No upgrading program records found"
    }
  });

  $('#neurodevAssessmentTable').DataTable({
    pageLength: 5,
    lengthMenu: [
      [5, 10, 25, 50, 100],
      [5, 10, 25, 50, 100]
    ],
    order: [[0, 'desc']],
    language: {
      emptyTable: "No Neurodevelopmental assessment records found"
    }
  });

  // $('#pendingPayoutTable').DataTable({
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   order: [[0, 'desc']],
  //   language: {
  //     emptyTable: "No pending PayMongo payment records found"
  //   }
  // });
  //
  // $('#paidPayoutTable').DataTable({
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   order: [[0, 'desc']],
  //   language: {
  //     emptyTable: "No paid PayMongo payment records found"
  //   }
  // });
  //
  // $('#cancelPayoutTable').DataTable({
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   order: [[0, 'desc']],
  //   language: {
  //     emptyTable: "No cancel PayMongo payout records found"
  //   }
  // });
  //
  // $('#cashSettlementTable').DataTable({
  //   order: [[0, 'desc']],
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   language: {
  //     emptyTable: "No cash settlement records found"
  //   }
  // });
  //
  // $('#cashUnsettledTable').DataTable({
  //   order: [[0, 'desc']],
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   language: {
  //     emptyTable: "No unsettled cash records found"
  //   }
  // });
  //
  // $('#cashSettledTable').DataTable({
  //   order: [[0, 'desc']],
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   language: {
  //     emptyTable: "No settled cash records found"
  //   }
  // });
  //
  // $('#allSettlementsTable').DataTable({
  //   order: [[0, 'desc']],
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   language: {
  //     emptyTable: "No settlement records found"
  //   }
  // });
  //
  // $('#cashSettlementsTable').DataTable({
  //   order: [[0, 'desc']],
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   language: {
  //     emptyTable: "No cash settlement records found"
  //   }
  // });
  //
  // $('#payMongoSettlementsTable').DataTable({
  //   order: [[0, 'desc']],
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   language: {
  //     emptyTable: "No PayMongo settlement records found"
  //   }
  // });
  //
  // $('#payoutDetailsTable').DataTable({
  //   pageLength: 5,
  //   paging: true,
  //   searching: false,   // Hide Search box
  //   lengthChange: false, // Hide "Show entries"
  //   info: true,
  //   ordering: true,
  //   order: [[0, 'desc']],
  //   language: {
  //     emptyTable: "No PayMongo payment records found"
  //   }
  // });
  //
  // $('#couponsTable').DataTable({
  //   pageLength: 5,
  //   lengthMenu: [
  //     [5, 10, 25, 50, 100],
  //     [5, 10, 25, 50, 100]
  //   ],
  //   order: [[0, 'desc']],
  //   language: {
  //     emptyTable: "No coupon records found"
  //   }
  // });
});
