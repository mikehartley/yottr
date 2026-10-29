// The map of the boats that match the search, on the list of boats. The button shows it; it fetches the matching
// boats' positions from data-map-url with the search form's criteria, and fetches again when htmx changes the
// results. Listings at the same spot (several in the middle of a region, say) share a pin that lists them all.
(function () {
    'use strict';

    var button = document.getElementById('show-map');
    var container = document.getElementById('results-map');
    var form = document.getElementById('search-form');
    if (!button || !container || !form || !window.L) {
        return;
    }

    var map = null;
    var pins = null;
    button.classList.remove('d-none');

    button.addEventListener('click', function () {
        var showing = container.hidden;
        container.hidden = !showing;
        button.setAttribute('aria-expanded', String(showing));
        button.querySelector('span').textContent = showing ? 'Hide the map' : 'Show on a map';
        if (showing) {
            if (!map) {
                create();
            }
            map.invalidateSize();
            load();
        }
    });

    document.body.addEventListener('htmx:afterSwap', function (event) {
        if (map && !container.hidden && event.detail.target && event.detail.target.id === 'results') {
            load();
        }
    });

    function create() {
        L.Icon.Default.imagePath = container.dataset.leafletImages;
        map = L.map(container).setView([54.5, -3], 5);
        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 18,
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(map);
        pins = L.layerGroup().addTo(map);
    }

    function load() {
        var query = new URLSearchParams(new FormData(form));
        fetch(container.dataset.mapUrl + '?' + query.toString(), {headers: {'Accept': 'application/json'}})
            .then(function (response) {
                if (!response.ok) {
                    throw new Error(response.status);
                }
                return response.json();
            })
            .then(show)
            .catch(function () {
                pins.clearLayers();
            });
    }

    function show(listings) {
        pins.clearLayers();
        var spots = {};
        listings.forEach(function (listing) {
            var key = listing.latitude + ',' + listing.longitude;
            (spots[key] = spots[key] || []).push(listing);
        });
        var bounds = [];
        Object.keys(spots).forEach(function (key) {
            var here = spots[key];
            var position = [here[0].latitude, here[0].longitude];
            bounds.push(position);
            L.marker(position, {title: here.length === 1 ? here[0].title : here.length + ' boats'})
                .bindPopup(popup(here))
                .addTo(pins);
        });
        if (bounds.length === 1) {
            map.setView(bounds[0], 9);
        } else if (bounds.length > 1) {
            map.fitBounds(bounds, {padding: [30, 30], maxZoom: 9});
        }
    }

    // built from elements rather than HTML, since the titles are the owners' own words
    function popup(listings) {
        var list = document.createElement('ul');
        list.className = 'list-unstyled mb-0 yottr-map-popup';
        listings.forEach(function (listing) {
            var item = document.createElement('li');
            var link = document.createElement('a');
            link.href = container.dataset.listingUrl + encodeURIComponent(listing.reference);
            link.textContent = listing.title;
            var detail = document.createElement('div');
            detail.className = 'small text-body-secondary';
            detail.textContent = listing.makeAndModel + ', ' + listing.where;
            item.appendChild(link);
            item.appendChild(detail);
            list.appendChild(item);
        });
        return list;
    }
})();
