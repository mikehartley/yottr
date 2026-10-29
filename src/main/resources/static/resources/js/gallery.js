// Flips through a listing's pictures in place. Without this, the same buttons and thumbnails are links that load the
// page again showing that picture.
document.querySelectorAll('.yottr-gallery').forEach(gallery => {
    const thumbnails = [...gallery.querySelectorAll('.yottr-gallery-thumbnails a')];
    if (thumbnails.length < 2) {
        return;
    }
    const stage = gallery.querySelector('.yottr-gallery-stage');
    const picture = stage.querySelector('img');
    const counter = gallery.querySelector('.yottr-gallery-counter');
    const strip = gallery.querySelector('.yottr-gallery-thumbnails');
    let current = Math.max(0, thumbnails.findIndex(thumbnail => thumbnail.getAttribute('aria-current') === 'true'));

    const show = index => {
        current = (index + thumbnails.length) % thumbnails.length;
        const thumbnail = thumbnails[current];
        stage.href = thumbnail.dataset.picture;
        picture.src = thumbnail.dataset.picture;
        picture.width = thumbnail.dataset.width;
        picture.height = thumbnail.dataset.height;
        picture.alt = `Picture ${current + 1} of ${thumbnails.length}`;
        counter.textContent = `${current + 1} of ${thumbnails.length}`;
        thumbnails.forEach((other, i) => i === current ? other.setAttribute('aria-current', 'true') : other.removeAttribute('aria-current'));
        // scroll the strip, not the page, to bring the thumbnail into the middle of it
        strip.scrollTo({left: thumbnail.offsetLeft - (strip.clientWidth - thumbnail.offsetWidth) / 2, behavior: 'smooth'});
        // so that reloading the page keeps the picture
        history.replaceState(history.state, '', thumbnail.href.split('#')[0]);
    };

    const onClick = (element, index) => element.addEventListener('click', event => {
        event.preventDefault();
        show(index());
    });
    onClick(gallery.querySelector('.yottr-gallery-previous'), () => current - 1);
    onClick(gallery.querySelector('.yottr-gallery-next'), () => current + 1);
    thumbnails.forEach((thumbnail, i) => onClick(thumbnail, () => i));

    gallery.addEventListener('keydown', event => {
        if (event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
            event.preventDefault();
            show(current + (event.key === 'ArrowLeft' ? -1 : 1));
        }
    });
});
