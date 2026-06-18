var publishableKey;
var clientSecret;
var customerSessionClientSecret;
var showCountryEnabled = false;
var cardholderNameEnabled = false;
var paymentIntentId;
var affiliateId;
var stripe;
var renewal;
var basketAmount;
// Store the payment elements in a variable when they are initialised.
let elements;

// Keeps track if form has been submitted
let isSubmit = false;

$(document).ready(function() {
	publishableKey = $("#publishableKey").val();
	clientSecret = $("#clientSecret").val();
	customerSessionClientSecret = $("#customerSessionClientSecret").val();
	showCountryEnabled = $("#showCountryEnabled").val();
	cardholderNameEnabled = $("#cardholderNameEnabled").val();
	paymentIntentId = $("#paymentIntentId").val();
	affiliateId = $("#affiliateId").val();
	renewal = $("#isRenewal").val();
	basketAmount = parseFloat($('#amount').val()) || 0;

	$('#PaymentFormSubmit').off('click');
	$('#PaymentFormSubmit').on('click', handleSubmit);
	$('#cardholderName').parsley();

	stripe = Stripe(publishableKey);
	initialize();
});

function initialize() {
	const appearance = {
		theme: 'stripe',
	};

	function getPaymentElementLayout() {
		return window.matchMedia("(max-width: 768px)").matches ? "accordion" : "tabs";
	}

	const elementsOptions = {
		clientSecret: clientSecret,
		appearance: appearance,
	};

	if (customerSessionClientSecret) {
		elementsOptions.customerSessionClientSecret = customerSessionClientSecret;
	}

	elements = stripe.elements(elementsOptions);

	const paymentElementOptions = {
		layout: getPaymentElementLayout(),
		fields: {
			billingDetails: {
				name: "never",
				email: "never",
				phone: "never",
				address: {
					country: showCountryEnabled === "true" ? "auto" : "never",
					line1: "never",
					line2: "never",
					city: "never",
					state: "never",
					postalCode: showCountryEnabled === "true" ? "auto" : "never"
				}
			}
		}
	};

	const paymentElement = elements.create("payment", paymentElementOptions);
	paymentElement.mount("#payment-element");

	var $cardholderNameField = $('#cardholderNameField').detach();

	// Listener to show or hide cardholderName field
	paymentElement.on('change', function(event) {
		var paymentMethodType = event.value && event.value.type;
		var paymentMethodData = event.value && event.value.payment_method;

		if (paymentMethodType === 'card' && !paymentMethodData) {
			$('#cardholderNameParent').append($cardholderNameField);
			$('#cardholderName').parsley().addConstraint('required');
			$('#nonCardPayment').prop('checked', false);
		} else {
			$cardholderNameField.detach();
			$('#nonCardPayment').prop('checked', true);
		}
	});

	// Listen for screen resize to adjust layout on-the-fly
	window.addEventListener('resize', () => {
		const newLayout = getPaymentElementLayout();
		paymentElement.update({ layout: newLayout });
	});
}

function handleSubmit(event) {
	if (typeof validateForm === "function") {
		if (validateForm(true, true)) {
			return trySubmitForm(event);
		}
	} else {
		if ($("#payment-details-form-section").is(':hidden')) {
			$('#cardholderName').attr('data-parsley-excluded', 'true');
		} else {
			$('#cardholderName').removeAttr('data-parsley-excluded');
		}

		if ($('#PaymentForm').parsley().validate()) {
			return trySubmitForm(event);
		}
	}
}

function trySubmitForm(event) {
	if (isSubmit === false && basketAmount > 0 && !$('#payment-details-form-section').is(':hidden')) {
		blockUI();

		var paymentForm = $('#PaymentForm');
		event.preventDefault();
		var url = renewal == "true" ? "take-renewal-payment" : "booking?cmd=booking";

		isSubmit = true;

		$.ajax({
			type: "GET",
			url: url,
			data: paymentForm.serialize(),
			success: function(data) {
				if (data) {
					$('#initialisationFailureError').hide();
					confirmPayment(data);
				}
				else {
					$('#initialisationFailureError').show();
					unblockUI();
				}
				isSubmit = false;
			},
			error: function(data) {
				$('#initialisationFailureError').show();
				unblockUI();
				isSubmit = false;
			},
		});
	}
}

async function confirmPayment(stripeRedirectUrl) {
	var countryCode = $('#country').find(":selected").attr('data-country-code');
	countryCode = countryCode !== undefined ? countryCode : "GB"
	var firstName = $('#firstName').val();
	firstName = firstName !== undefined ? firstName : "";
	var lastName = $('#lastName').val();
	lastName = lastName !== undefined ? lastName : "";
	var cardholderName = $('#cardholderName').val();
	cardholderName = cardholderName !== undefined ? cardholderName : "";
	if (cardholderNameEnabled === "true") {
		var billingName = cardholderName;
	} else {
		var billingName = firstName + " " + lastName;
	}
	// Confirm the payment, the billing_details need to be populated from the form, all the fields below are
	// required on the request but only the country is required to be populated. THe country has to be the
	// 2 letter country code, e.g. GB.
	// Query string parameters, e.g. reservation guid, can be added to the return_url and they will be preserved.
	const { error } = await stripe.confirmPayment({
		elements,
		confirmParams: {
			return_url: stripeRedirectUrl,
			payment_method_data: {
				billing_details: {
					name: billingName,
					email: "",
					phone: "",
					address: {
						line1: "",
						line2: "",
						city: "",
						state: "",
						postal_code: "",
						country: countryCode != "" ? countryCode : "GB"
					}
				}
			}
		},
	});

	if (error) {
		$('#initialisationFailureError').show();
		$('#initialisationFailureError').text(error.message);
	} else {
		$('#initialisationFailureError').hide();
	}

	unblockUI();
}

function unblockUI() {
	$.unblockUI();
}


function updatePaymentIntent() {
	var customerGuid = $('#customerGuid').val();

	if (!paymentIntentId || !affiliateId || !customerGuid) {
		console.error('Missing required parameters for payment intent update');
		$('#initialisationFailureError').show();
		return;
	}

	blockUI();

	$.ajax({
		type: "POST",
		url: "update-payment-intent",
		dataType: "json",
		data: {
			paymentIntentId: paymentIntentId,
			affiliateId: affiliateId,
			customerGuid: customerGuid
		},
		success: function(data) {
			if (data.Map.success === true) {
				console.log('Payment intent updated successfully');
				$('#initialisationFailureError').hide();
				// Update basketAmount from the current amount field value
				basketAmount = parseFloat($('#amount').val()) || 0;
				// Fetch updated payment details from Stripe
				elements.fetchUpdates();
				unblockUI();
			}
			else {
				console.error('Failed to update payment intent');
				$('#initialisationFailureError').show();
				unblockUI();
			}
		},
		error: function(xhr, status, error) {
			console.error('Error updating payment intent:', error);
			$('#initialisationFailureError').show();
			unblockUI();
		},
	});
}