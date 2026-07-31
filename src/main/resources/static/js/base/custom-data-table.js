$(document).ready(async function () {
    initOrdersTable();
});

function initOrdersTable() {
    $('.custom-data-table').each(function () {
        const $table = $(this);

        if ($.fn.DataTable.isDataTable($table)) {
            $table.DataTable().clear().destroy();
        }

        $table.DataTable({
            pageLength: 5,
            lengthChange: false,
            ordering: false,
            searching: true,
            info: true,
            responsive: true
        });
    });
}