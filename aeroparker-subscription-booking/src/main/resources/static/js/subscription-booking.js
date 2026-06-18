$(document).ready(function () {
	$('#emailInput').on('blur', function () {
		var email = $('#emailInput').val().trim();
		
		var currentUrl = window.location.pathname;
		var affCode = currentUrl.split("/")[2];
		
		if (email && affCode) {
			
			$.ajax({
				type: 'POST',
				url: '/subscriptions/' + affCode + '/validate-subscription-renewal',
				contentType: 'application/json',
				dataType: 'json',
				data: JSON.stringify({
					email: email,
					customerGuid: $('#customerGuid').val()
				}),
				success: function (response) {
					if (response.newStartDate && response.newEndDate && response.grandTotal) {
						$('#cpEntryDate').text(response.newStartDate);
						$('#cpExitDate').text(response.newEndDate);
						$('#totalPrice').text(response.grandTotal);
						$('.booking-summary__item__val--total').html(response.priceIncludePennyPlaceholdersHtml);
						$('#emailValidationError').hide();
						$('#startDate').val(response.newStartDate);
						$('#amount').val(response.grandTotal);
					} else if (response.error) {
						$('#emailValidationError').text(response.error).show();
					} else if (response.message) {
						$('#emailValidationError').text(response.message).show();
					}
				},
				error: function () {
					$('#emailValidationError').text('An error occurred. Please try again later.').show();
				}
			});
	}
	});
});