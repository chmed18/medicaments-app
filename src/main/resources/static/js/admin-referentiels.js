(function () {
    document.addEventListener("DOMContentLoaded", function () {
        document.querySelectorAll("form[data-confirm-delete]").forEach(function (form) {
            form.addEventListener("submit", function (event) {
                var label = form.getAttribute("data-item-label") || "cet element";
                var confirmed = window.confirm("Confirmer la suppression de \"" + label + "\" ?");
                if (!confirmed) {
                    event.preventDefault();
                }
            });
        });
    });
})();

