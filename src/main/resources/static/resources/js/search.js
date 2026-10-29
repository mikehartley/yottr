// The search form on the list of boats. Each multiple choice (Where, Purpose, When, Costs) is a <details> drop-down
// of tick boxes: this keeps its summary saying what's ticked, opens one at a time, and closes them on a click
// elsewhere or Escape. Ticking a whole group of regions ticks and locks its own regions, so a group and some of
// its regions can't be asked for together.
(function () {
    'use strict';

    var form = document.getElementById('search-form');
    if (!form) {
        return;
    }
    var drops = Array.prototype.slice.call(form.querySelectorAll('details.yottr-multi'));

    function summarise(details) {
        var names = [];
        details.querySelectorAll('input[type=checkbox]:checked:not(:disabled)').forEach(function (box) {
            var label = form.querySelector('label[for="' + box.id + '"]');
            names.push(box.classList.contains('yottr-region-group') ? label.textContent.replace(/ \(all\)$/, '') : label.textContent);
        });
        details.querySelector('summary').textContent = names.length ? names.join(', ') : details.dataset.none;
    }

    form.querySelectorAll('input.yottr-region-group').forEach(function (group) {
        // this runs before the change reaches the form, so htmx sends the regions as they're left
        group.addEventListener('change', function () {
            form.querySelectorAll('input[name=region][data-group="' + group.value + '"]').forEach(function (region) {
                region.checked = group.checked;
                region.disabled = group.checked;
            });
        });
    });

    drops.forEach(function (details) {
        details.addEventListener('change', function () {
            summarise(details);
        });
        details.addEventListener('toggle', function () {
            if (details.open) {
                drops.forEach(function (other) {
                    if (other !== details) {
                        other.open = false;
                    }
                });
            }
        });
    });

    document.addEventListener('click', function (event) {
        drops.forEach(function (details) {
            if (details.open && !details.contains(event.target)) {
                details.open = false;
            }
        });
    });
    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            drops.forEach(function (details) {
                if (details.open) {
                    details.open = false;
                    details.querySelector('summary').focus();
                }
            });
        }
    });

})();
