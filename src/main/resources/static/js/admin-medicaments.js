/**
 * admin-medicaments.js
 * Gestion JS de l'interface d'administration des médicaments :
 *  - confirmation de suppression
 *  - ajout / suppression dynamique de lignes de composition
 *  - validation client des prix (>= 0)
 */
(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {

        /* ================================================================
           1. Confirmation de suppression (liste)
        ================================================================ */
        document.querySelectorAll('form[data-confirm-delete]').forEach(function (form) {
            form.addEventListener('submit', function (e) {
                var label = form.getAttribute('data-item-label') || 'cet élément';
                if (!window.confirm('Confirmer la suppression de "' + label + '" ?')) {
                    e.preventDefault();
                }
            });
        });

        /* ================================================================
           2. Gestion dynamique des lignes de composition (formulaire)
        ================================================================ */
        var compositionList = document.getElementById('composition-list');
        if (!compositionList) return; // pas sur la page formulaire → on s'arrête

        var addBtn = document.getElementById('add-composition');
        if (addBtn) {
            addBtn.addEventListener('click', function () {
                addCompositionRow();
            });
        }

        // Délégation de clic sur le bouton "Supprimer" de chaque ligne
        compositionList.addEventListener('click', function (e) {
            var btn = e.target.closest('[data-remove-row]');
            if (btn) {
                var row = btn.closest('.composition-row');
                if (row) removeCompositionRow(row);
            }
        });

        /* ---- helpers ---- */

        function getRowCount() {
            return compositionList.querySelectorAll('.composition-row').length;
        }

        /**
         * Réindexe toutes les lignes après un ajout ou une suppression.
         * Met à jour l'attribut `name` de chaque input/select portant
         * data-comp-field, et le titre de la ligne.
         */
        function reindexRows() {
            var rows = compositionList.querySelectorAll('.composition-row');
            rows.forEach(function (row, idx) {
                row.querySelectorAll('[data-comp-field]').forEach(function (el) {
                    var field = el.getAttribute('data-comp-field');
                    el.name = 'compositions[' + idx + '].' + field;
                    el.id   = 'comp_' + idx + '_' + field;
                });
                var title = row.querySelector('.composition-row-title');
                if (title) title.textContent = 'Composition ' + (idx + 1);
            });
            updateRemoveButtons();
        }

        /** Active / désactive les boutons "Supprimer" selon le nombre de lignes. */
        function updateRemoveButtons() {
            var rows = compositionList.querySelectorAll('.composition-row');
            var canRemove = rows.length > 1;
            rows.forEach(function (row) {
                var btn = row.querySelector('[data-remove-row]');
                if (btn) {
                    btn.disabled = !canRemove;
                    btn.style.visibility = canRemove ? 'visible' : 'hidden';
                }
            });
        }

        /** Clone le <template> et ajoute une nouvelle ligne vide. */
        function addCompositionRow() {
            var template = document.getElementById('composition-row-template');
            if (!template) return;

            var idx   = getRowCount();
            var clone = template.content.cloneNode(true);

            // Mise à jour des attributs name / id
            clone.querySelectorAll('[data-comp-field]').forEach(function (el) {
                var field = el.getAttribute('data-comp-field');
                el.name = 'compositions[' + idx + '].' + field;
                el.id   = 'comp_' + idx + '_' + field;
            });

            // Mise à jour du titre
            var title = clone.querySelector('.composition-row-title');
            if (title) title.textContent = 'Composition ' + (idx + 1);

            compositionList.appendChild(clone);
            updateRemoveButtons();

            // Focus sur le premier champ de la nouvelle ligne
            var firstField = compositionList.querySelector(
                '.composition-row:last-child [data-comp-field]'
            );
            if (firstField) firstField.focus();
        }

        /** Supprime une ligne et réindexe. */
        function removeCompositionRow(row) {
            if (getRowCount() <= 1) return; // on ne supprime pas la dernière
            row.remove();
            reindexRows();
        }

        /* ================================================================
           3. Initialisation : réindexage des lignes existantes + boutons
        ================================================================ */
        reindexRows();

        /* ================================================================
           4. Validation client des prix (>= 0)
        ================================================================ */
        document.querySelectorAll('input[data-min-zero]').forEach(function (input) {
            input.addEventListener('input', function () {
                var val = parseFloat(this.value);
                if (!isNaN(val) && val < 0) {
                    this.setCustomValidity('La valeur doit être supérieure ou égale à 0.');
                } else {
                    this.setCustomValidity('');
                }
            });
        });

    });
})();

