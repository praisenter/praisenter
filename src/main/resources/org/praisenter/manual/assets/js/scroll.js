function praisenter_scrollLeftNav() {
    var hash = document.location.hash;
    var path = document.location.pathname;
    var match = path + hash;

    var navItems = document.getElementsByClassName("p-man-nav-link");
    for (var i = 0; i < navItems.length; i++) {
        var item = navItems[i];
        item.classList.remove('p-man-nav-link-selected');
        if (item.href.endsWith(match)) {
            // set it as the selected item
            item.classList.add('p-man-nav-link-selected');
            // scroll to it
            if (navigator.userAgent.indexOf('JavaFX') < 0) {
                item.scrollIntoView({ behavior: 'smooth', block: 'center', container: 'nearest' });
            }
        }
    }
}

document.addEventListener("DOMContentLoaded", function() {
    praisenter_scrollLeftNav();
});

window.addEventListener('hashchange', function() {
    praisenter_scrollLeftNav();
});