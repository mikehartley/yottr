/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

jQuery(function($) {

	//goto top
	$('.gototop').on('click', function(event) {
		event.preventDefault();
		$('html, body').animate({
			scrollTop: $("body").offset().top
		}, 500);
	});
});
