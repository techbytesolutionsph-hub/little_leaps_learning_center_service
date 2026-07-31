const USER_ID = 'TEL202500000001D'; // Replace with current user ID

// Fetch notifications from backend
function fetchNotifications() {
    $.getJSON('/api/v1/notifications/get-notification?userId=' + USER_ID, function(notifications) {
        const $notifList = $('#notifList');
        const $notifBadge = $('#notifBadge');

        // Count unread notifications
        const unreadCount = notifications.filter(n => !n.read).length;

        // Update badge
        if (unreadCount > 0) {
            $notifBadge.text(unreadCount).show();
        } else {
            $notifBadge.hide();
        }

        // Clear existing list
        $notifList.empty();

        // Display latest 4 notifications
        const latestNotifications = notifications.slice(0, 4);

        latestNotifications.forEach((notif, index) => {
            const li = $('<li></li>');
            const a = $('<a></a>')
                .addClass('dropdown-item d-flex align-items-start')
                .attr('href', notif.link)
                .attr('data-id', notif.id) // store notification id
                .html(`
                    <i class="${notif.icon} me-2"></i>
                    <div class="flex-grow-1">
                        <span>${notif.message}</span>
                        <small class="text-muted d-block">${notif.time}</small>
                    </div>
                `);
            li.append(a);
            $notifList.append(li);

            if (index < latestNotifications.length - 1) {
                $notifList.append('<li><hr class="dropdown-divider"></li>');
            }
        });

        // Add final divider and "View All Notifications"
        if (notifications.length > 0) {
            $notifList.append('<li><hr class="dropdown-divider"></li>');
            $notifList.append(`
                <li>
                    <a class="dropdown-item text-center d-block" href="/telatak/seller/notifications"
                       style="width:100%; padding:0.5rem 0;">
                       View All Notifications
                    </a>
                </li>
            `);
        }
    });
}

// Initial fetch
fetchNotifications();

// Handle click on individual notifications
$(document).on('click', '#notifList a.dropdown-item', function(e) {
    const notifId = $(this).data('id');
    if (!notifId) return; // Ignore "View All Notifications"

    $.post(`/api/v1/notifications/mark-read/${notifId}`, { userId: USER_ID }, function() {
        // Update badge count
        const $badge = $('#notifBadge');
        let count = parseInt($badge.text());
        count = Math.max(0, count - 1);

        if (count === 0) $badge.hide();
        else $badge.text(count);

        // Refresh notifications to move read message down
        fetchNotifications();
    });
});