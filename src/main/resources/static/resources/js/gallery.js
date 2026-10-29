// Flips through a listing's pictures in place, and opens the picture over the whole page. Without this, the buttons
// and thumbnails are links that load the page again showing that picture, and the picture links to itself.
document.querySelectorAll('.yottr-gallery').forEach(gallery => {
    const stage = gallery.querySelector('.yottr-gallery-stage');
    const picture = stage.querySelector('img');
    const counter = gallery.querySelector('.yottr-gallery-counter');
    const strip = gallery.querySelector('.yottr-gallery-thumbnails');
    const thumbnails = [...gallery.querySelectorAll('.yottr-gallery-thumbnails a')];
    const lightbox = gallery.querySelector('.yottr-lightbox');
    const lightboxPicture = lightbox.querySelector('.yottr-lightbox-picture');
    const lightboxCounter = lightbox.querySelector('.yottr-lightbox-counter');
    const modal = bootstrap.Modal.getOrCreateInstance(lightbox);
    // with only one picture there are no thumbnails, and nothing to flip through
    const count = Math.max(1, thumbnails.length);
    let current = Math.max(0, thumbnails.findIndex(thumbnail => thumbnail.getAttribute('aria-current') === 'true'));

    const showInLightbox = () => {
        lightboxPicture.src = stage.href;
        lightboxPicture.alt = picture.alt;
        lightboxCounter.textContent = `${current + 1} of ${count}`;
    };

    const show = index => {
        if (count < 2) {
            return;
        }
        current = (index + count) % count;
        const thumbnail = thumbnails[current];
        stage.href = thumbnail.dataset.picture;
        picture.src = thumbnail.dataset.picture;
        picture.width = thumbnail.dataset.width;
        picture.height = thumbnail.dataset.height;
        picture.alt = `Picture ${current + 1} of ${count}`;
        counter.textContent = `${current + 1} of ${count}`;
        showInLightbox();
        thumbnails.forEach((other, i) => i === current ? other.setAttribute('aria-current', 'true') : other.removeAttribute('aria-current'));
        // scroll the strip, not the page, to bring the thumbnail into the middle of it
        strip.scrollTo({left: thumbnail.offsetLeft - (strip.clientWidth - thumbnail.offsetWidth) / 2, behavior: 'smooth'});
        // so that reloading the page keeps the picture
        history.replaceState(history.state, '', thumbnail.href.split('#')[0]);
    };

    const onClick = (element, action) => element?.addEventListener('click', event => {
        event.preventDefault();
        action();
    });
    onClick(stage, () => {
        showInLightbox();
        modal.show();
    });
    onClick(gallery.querySelector('.yottr-gallery-previous'), () => show(current - 1));
    onClick(gallery.querySelector('.yottr-gallery-next'), () => show(current + 1));
    onClick(lightbox.querySelector('.yottr-lightbox-previous'), () => show(current - 1));
    onClick(lightbox.querySelector('.yottr-lightbox-next'), () => show(current + 1));
    thumbnails.forEach((thumbnail, i) => onClick(thumbnail, () => show(i)));

    // a click on the dark space around the picture closes it
    lightbox.querySelector('.modal-body').addEventListener('click', event => {
        if (event.target === event.currentTarget) {
            modal.hide();
        }
    });

    // Bootstrap closes it with Escape
    gallery.addEventListener('keydown', event => {
        if (event.key === 'ArrowLeft' || event.key === 'ArrowRight') {
            event.preventDefault();
            show(current + (event.key === 'ArrowLeft' ? -1 : 1));
        }
    });

    // swiping across the picture on a phone
    let touchStart = null;
    lightbox.addEventListener('touchstart', event => touchStart = event.changedTouches[0].clientX, {passive: true});
    lightbox.addEventListener('touchend', event => {
        const across = event.changedTouches[0].clientX - touchStart;
        if (touchStart !== null && Math.abs(across) > 50) {
            show(current + (across > 0 ? -1 : 1));
        }
        touchStart = null;
    });
});
