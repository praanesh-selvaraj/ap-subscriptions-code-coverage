function resendConfirmationEmail(emailAddress, bookingRef) {
	$.ajax({
		  type: "POST",
		  dataType: "json",
		  url: "resend-confirmation",
		  data: { reference: bookingRef, email: emailAddress }
		}).done(function(result) {
			if (result.sent) {
				$('#resend-confirmation').addClass('is-active');
			}
		});
}