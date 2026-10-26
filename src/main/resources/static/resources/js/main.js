jQuery(function($) {

	//goto top
	$('.gototop').on('click', function(event) {
		event.preventDefault();
		$('html, body').animate({
			scrollTop: $("body").offset().top
		}, 500);
	});
});
