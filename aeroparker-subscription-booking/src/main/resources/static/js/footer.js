$('#footerListLinks li').each(
		function() {
			$('#footerList').append($(this).clone());
		});