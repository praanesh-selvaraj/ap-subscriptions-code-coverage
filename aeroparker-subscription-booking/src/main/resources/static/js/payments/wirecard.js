var submitted = false;
var validForm = false;
var pageSubmitButton;

/*
 * If we have a paypal request, and a url to go to, go to it.
 */
if ($('#submitted') == '1' && $('#userChosenPayment') == '2' && $('#paypalUrl').length){
	window.location.replace(decodeURIComponent(data));
}
else{
	$('#userChosenPayment').val('0');
}
  
function processSucceededResult(response) {
	WirecardPaymentPage.seamlessChangeLocale(seamlessLocale);
}
  
function processErrorResult(response) {
    // We don't need to affect anything after form validation errors.
	unblockUI();
    if (response.transaction_state == 'failed'){
    	getDisplayFormRequestData();
   	}
    return false;
}
  
function submitWirecard() {
	if ($('#saved-details').is(':visible')) {
		return submitSavedDetails();
	} else {
		return submitNewDetails();
	}
	return false;
}

function submitSavedDetails() {
	if (validateDetailsForm()) {
		// Prevent interaction with the form.
		blockUI();
		// Submit the form.
		if (!submitted) {
			submitted = true;
	        $("#PaymentForm").unbind('submit').submit();
		}
	}
	return false;
}

function submitNewDetails() {
	if (validateDetailsForm()) {
		return submitSeamlessForm();
	}
	return false;
}

function validateDetailsForm() {
	if (typeof validateForm === "function") {
		if (validateForm(true, true)) {
			return true;	
		}
	} else {
		if (validForm) {
			return true;
		}
	}
	return false;
}

function submitSeamlessForm() {
	WirecardPaymentPage.seamlessSubmitForm({
    	onSuccess : function (response){
    		$('#paymentReference').val(response.transaction_id);
    		// Submit the form.
    		if (!submitted) {
    			submitted = true;
    	        $("#PaymentForm").unbind('submit').submit();
    		}
      	},
      	onError : handleWirecardPaymentError
	});
}
	
function handleWirecardPaymentError(response){
	unblockUI();
	  $("#wirecardError").css("display", "block");
      return processErrorResult(response);
}

function validateSeamlessForm(){
	if ($('#saved-details').is(':visible')) {
		submitWirecard();
	} else {
		WirecardPaymentPage.seamlessValidateForm({
			onValidationResult : function (response){
				if (response.form_validation_result == 'success'){
					submitWirecard();
				}
				else return false;
			}
		});
	}
}

function showSavedPaymentMethods() {
	$("#savePaymentMethod").prop('checked', false);
    $("#new-details").hide();
    $('#saved-details').show();
}

function handleSubmit(seamlessValid) {
	if (typeof validateForm === "function") {
		if (validateForm(true, true) && seamlessValid) {
			return trySubmitForm();
		}
	} else {
		validateInputFields();
		if (validForm && seamlessValid) {
			return trySubmitForm();
		}
	}
}

function trySubmitForm() {
	// Prevent interaction with the form.
	blockUI();
	return submitForm();
}

function showCard() {
	$("#userChosenPayment").val(1);
	$("#card-container").show();
	$('#saveCardDiv').show();
}

function showRedirect(paymentService) {
	$("#userChosenPayment").val(paymentService);
	$("#card-container").hide();
	$('#saveCardDiv').hide();
}

function showNewPaymentMethods() {
	// Always default to card.
	$('#saved-details').hide();
	$("#new-details").show();
	$('#paymentMethodCard').prop('checked', true);
	showCard();
}

function getDisplayFormRequestData(){
	var affiliateId = $("#affiliateId").val();
	var currency = $("#currency").val();
	var amount = $("#amount").val();
	$.ajax({
		type: "POST",
		url: "seamless/generate",
		dataType: "json",
		data: {cmd: "init", affiliateId: affiliateId, currency: currency, amount: amount},
		success: function(data) {
			WirecardPaymentPage.seamlessRenderForm({
				requestData : data,
				wrappingDivId : 'creditcardDataIframe',
				onSuccess : processSucceededResult,
				onError : processErrorResult
			});
		}
	});
}

$(function() {
	// Get correct submit button.
	if ($('#PaymentFormSubmit').length){
		pageSubmitButton = $('#PaymentFormSubmit');
	}
	else{
		pageSubmitButton = document.querySelectorAll('.manage-submit-payment');
	}

	// Override any existing submit.
	$(pageSubmitButton).off("click");
	$(pageSubmitButton).click(function(event){
		var seamlessValid = false;
		event.preventDefault();
		if ($('#paymentMethodCard').prop('checked')){
			WirecardPaymentPage.seamlessValidateForm({
				onValidationResult : function (response){
					if (response.form_validation_result == 'success'){
						seamlessValid = true;
					}
					handleSubmit(seamlessValid);
				}
			});
		}
		else{
			handleSubmit(true);
		}
	});

	$("#PaymentForm").submit(function(e) {
		e.preventDefault();
		e.stopPropagation();

		// Wirecard card payment "seamless" needs to override the submit function,
		// any paypal radio will hide this
		if ($('#creditcardDataIframe').is(':visible')) {
			validateSeamlessForm();
		}
		else{
			// Paypal is the other option, if form is filled, simply resubmit.
			if (!validateDetailsForm()) {
				return false;
			}
			$("#PaymentForm").unbind('submit').submit();
		}
	});

	getDisplayFormRequestData();
	
	$('#PaymentForm').prepend('<input type="hidden" name="amend" value="${amend}">');

	showNewPaymentMethods();
	
	if ('${requestScope.isLoggedIn}'==='true' && '${requestScope.savedCards}'!='[]') {
		$('#savedPaymentMethod').prop('checked', true);
	}
});