(function () {
    function escapeRegExp(value) {
        return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }

    function buildHighlightedContent(text, query) {
        const fragment = document.createDocumentFragment();

        if (!query) {
            fragment.appendChild(document.createTextNode(text));
            return fragment;
        }

        const matcher = new RegExp(escapeRegExp(query), 'gi');
        let lastIndex = 0;
        let match = matcher.exec(text);

        while (match) {
            if (match.index > lastIndex) {
                fragment.appendChild(document.createTextNode(text.slice(lastIndex, match.index)));
            }

            const mark = document.createElement('mark');
            mark.textContent = match[0];
            fragment.appendChild(mark);

            lastIndex = match.index + match[0].length;
            match = matcher.exec(text);
        }

        if (lastIndex < text.length) {
            fragment.appendChild(document.createTextNode(text.slice(lastIndex)));
        }

        if (!fragment.childNodes.length) {
            fragment.appendChild(document.createTextNode(text));
        }

        return fragment;
    }

    function initializeAutocomplete(root, index) {
        const configElement = root.closest('[data-autocomplete-config]');
        const input = root.querySelector('[data-autocomplete-input]');
        const list = root.querySelector('[data-autocomplete-list]');

        if (!configElement || !input || !list) {
            return;
        }

        const autocompleteUrl = configElement.dataset.autocompleteUrl;
        const minLength = Number.parseInt(configElement.dataset.autocompleteMinLength ?? '2', 10);
        const debounceMs = Number.parseInt(configElement.dataset.autocompleteDebounceMs ?? '250', 10);
        let activeIndex = -1;
        let debounceTimer = null;
        let requestSequence = 0;

        function closeList() {
            list.style.display = 'none';
            input.setAttribute('aria-expanded', 'false');
            input.removeAttribute('aria-activedescendant');
            activeIndex = -1;
        }

        function openList() {
            list.style.display = 'block';
            input.setAttribute('aria-expanded', 'true');
        }

        function submitSelection(value) {
            input.value = value;
            closeList();
            input.form?.submit();
        }

        function updateActiveItem(items) {
            items.forEach(function (item, itemIndex) {
                const isActive = itemIndex === activeIndex;
                item.classList.toggle('active', isActive);
                item.setAttribute('aria-selected', String(isActive));
                if (isActive) {
                    input.setAttribute('aria-activedescendant', item.id);
                }
            });
        }

        function renderList(suggestions, query) {
            list.innerHTML = '';
            activeIndex = -1;

            if (!Array.isArray(suggestions) || suggestions.length === 0) {
                closeList();
                return;
            }

            suggestions.forEach(function (suggestion, suggestionIndex) {
                const item = document.createElement('li');
                item.id = 'autocomplete-option-' + index + '-' + suggestionIndex;
                item.setAttribute('role', 'option');
                item.setAttribute('aria-selected', 'false');
                item.appendChild(buildHighlightedContent(String(suggestion), query));
                item.addEventListener('mousedown', function (event) {
                    event.preventDefault();
                    submitSelection(String(suggestion));
                });
                list.appendChild(item);
            });

            openList();
        }

        function fetchSuggestions(query) {
            if (!autocompleteUrl || !query || query.length < minLength) {
                closeList();
                return;
            }

            const currentRequest = ++requestSequence;
            const requestUrl = autocompleteUrl + '?query=' + encodeURIComponent(query);

            fetch(requestUrl, {
                credentials: 'same-origin',
                headers: { 'Accept': 'application/json' }
            })
                .then(function (response) {
                    return response.ok ? response.json() : [];
                })
                .then(function (data) {
                    if (currentRequest === requestSequence) {
                        renderList(data, query);
                    }
                })
                .catch(function () {
                    if (currentRequest === requestSequence) {
                        closeList();
                    }
                });
        }

        input.addEventListener('input', function () {
            window.clearTimeout(debounceTimer);
            const query = input.value.trim();
            debounceTimer = window.setTimeout(function () {
                fetchSuggestions(query);
            }, debounceMs);
        });

        input.addEventListener('keydown', function (event) {
            const items = Array.from(list.querySelectorAll('li'));
            if (!items.length || list.style.display === 'none') {
                return;
            }

            if (event.key === 'ArrowDown') {
                event.preventDefault();
                activeIndex = (activeIndex + 1) % items.length;
                updateActiveItem(items);
                return;
            }

            if (event.key === 'ArrowUp') {
                event.preventDefault();
                activeIndex = (activeIndex - 1 + items.length) % items.length;
                updateActiveItem(items);
                return;
            }

            if (event.key === 'Enter') {
                if (activeIndex >= 0 && items[activeIndex]) {
                    event.preventDefault();
                    submitSelection(items[activeIndex].textContent ?? '');
                }
                return;
            }

            if (event.key === 'Escape') {
                closeList();
            }
        });

        document.addEventListener('click', function (event) {
            if (!root.contains(event.target)) {
                closeList();
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        const roots = document.querySelectorAll('[data-autocomplete-root]');
        roots.forEach(function (root, index) {
            initializeAutocomplete(root, index);
        });
    });
})();

