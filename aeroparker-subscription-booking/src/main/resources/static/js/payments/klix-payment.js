$(document).ready(function() {
	resetRadio();
});

function newPayment() {
	$('#paymentMethods').show();
	$('[name = "isSeamlessPayment"]').val('0');
}

function resetRadio() {
	var selectedPaymentMethod = $("input[name='paymentMethod']:checked").val();
	if (selectedPaymentMethod != null) {
		newPayment();
	}
}