var pageSubmitButton;
var clientAutorization;
var threeDSecure;
var is3DS2Enabled;
var amount;
$(document).ready(function(){
	// Get correct submit button.	
	clientAutorization = $("#clientToken").val();
	$('#paymentMethodCard').prop("checked", true);
	is3DS2Enabled = $("#is3DS2Enabled").val()  == 'true';
	amount = $("#amount").val();
	setUpBraintreeForm(clientAutorization);
});

function setUpBraintreeForm(clientAutorizationParameter) {
    braintree.client.create({
        authorization: clientAutorizationParameter
    }, function(clientErr, clientInstance) {
        if (clientErr) {
            return;
        }
		braintreeClient = clientInstance;
		setupHostedFields(braintreeClient); 
    });
    
    if (is3DS2Enabled) {
		braintree.threeDSecure.create({
			authorization : clientAutorizationParameter,
			version : 2
		}, function(threeDSecureErr, threeDSecureInstance) {
			if (threeDSecureErr) {
				// Handle error in 3D Secure component creation
				return;
			}
			threeDSecure = threeDSecureInstance;
		});
	}
}

function setupHostedFields(braintreeClient) {
	var setupOptionsArray = {
		client : braintreeClient,
		  styles: {
                'input': {
                    'font-size': '10pt',

                },
                'input.invalid': {
                    'color': 'red'
                },
                'input.valid': {
                    'color': 'green'
                }
            },
            fields: {
                number: {
                    selector: '#card-number',
                    placeholder: '1111 1111 1111 1111'
                },
                cvv: {
                    selector: '#cvv',
                    placeholder: '111'
                },
                expirationDate: {
                    selector: '#expiration-date',
                    placeholder: 'MM/YY'
                }
            }
	};
  braintree.hostedFields.create(setupOptionsArray, hostedFieldsCallback);
}

function hostedFieldsCallback(hostedFieldsErr, hostedFieldsInstance) {
  if (hostedFieldsErr) {
    return;
  }
  
	hostedFieldsInstance.on('blur', braintreeFieldCheckMethod);
            
	var form = document.querySelector('#PaymentForm');
	var submit = document.querySelector('#PaymentFormSubmit');
	var submitted = false;
    
    submit.removeAttribute('disabled');

    form.addEventListener('submit', function(event) {
        event.preventDefault();
    	validateInputFields();
    	
    	// Validate braintree fields at the same time because we are nice
		var braintreeFormValid = true;
		var state = hostedFieldsInstance.getState();
 		if (!state.fields['number'].isValid || state.fields['number'].isEmpty){
			$('#card-number').next('.error').show();
			braintreeFormValid = false;
		}
 		if (!state.fields['expirationDate'].isValid || state.fields['expirationDate'].isEmpty){
			$('div.conditionalInputFields').next('.error').show();
			braintreeFormValid = false;
		}
 		if (!state.fields['cvv'].isValid || state.fields['cvv'].isEmpty){
			$('#cvv').nextAll('.error').show();
			braintreeFormValid = false;
		}

	 	if (!validForm || !braintreeFormValid){
	 		return false;
	 	}

        hostedFieldsInstance.tokenize(function(tokenizeErr, payload) {
        	if (tokenizeErr) {
				$("#paymentReference").val("");
				if (typeof tokenizeErr.code != 'undefined' && tokenizeErr.code == 'HOSTED_FIELDS_FIELDS_EMPTY') {
					$('#card-number').next('.error').show();
					$('#cvv').nextAll('.error').show();
					$('div.conditionalInputFields').next('.error').show();
				}
				if (typeof tokenizeErr.details != 'undefined' && typeof tokenizeErr.details.invalidFieldKeys != 'undefined') {
					if (tokenizeErr.details.invalidFieldKeys.indexOf('number') > -1) {
						$('#card-number').next('.error').show();
					}
					if (tokenizeErr.details.invalidFieldKeys.indexOf('cvv') > -1) {
						$('#cvv').nextAll('.error').show();
					}
					if (tokenizeErr.details.invalidFieldKeys.indexOf('expirationDate') > -1) {
						$('div.conditionalInputFields').next('.error').show();
					}
				}
				// Handle error in Hosted Fields tokenization
				return;
			} else if (is3DS2Enabled) {
				var threeDSecureParameters = {
					amount: amount,
					nonce: payload.nonce,
					bin: payload.details.bin,
					onLookupComplete: function(data, next) {
						next();
					}
				}
				
				threeDSecure.verifyCard(threeDSecureParameters, function (err, response) {
					if (err || !response.liabilityShifted) {
						$('#threeDSError').show();
					} else {
						$("#paymentReference").val(response.nonce);
						$("#userChosenPayment").val(4);
						$('#paypal-payment').next('.error').hide();
						// Ensure fields are valid before submit
						 form.submit();
					}
				});
			} 	else {
				if (!submitted) {
                    $("#paymentReference").val(payload.nonce);
                    submitted = true;
                    blockUI();
                    form.submit();
                    return true;
                 }
                  return false;
			}
        }); 
    }, false);
}

function braintreeFieldCheckMethod(event) {
    var field = event.fields[event.emittedBy];

    if (event.emittedBy == 'number') {
    	if (field.isEmpty || !field.isValid && !field.isPotentiallyValid) {
    		$('#card-number').next('.error').show();
    	}
    	else{
    		$('#card-number').next('.error').hide();
    	}
    }
    if (event.emittedBy == 'expirationDate') {
    	if (field.isEmpty || !field.isValid && !field.isPotentiallyValid) {
    		$('div.conditionalInputFields').next('.error').show();
    	}
    	else{
    		$('div.conditionalInputFields').next('.error').hide();
    	}
    }
    if (event.emittedBy == 'cvv') {
    	if (field.isEmpty || !field.isValid && !field.isPotentiallyValid) {
	    	  $('#cvv').nextAll('.error').show();
    	}
    	else {
    		$('#cvv').nextAll('.error').hide();
    	}
    }
}