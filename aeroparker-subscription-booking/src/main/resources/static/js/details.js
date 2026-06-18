var validForm = false;

function validateInputFields() {
	$('#PaymentForm').parsley().validate();
	if ($('.help-block.filled:visible').length > 0
			|| $('.has-error:not(.fl):visible').length > 0) {
		validForm = false;
	} else {
		validForm = true;
	}
}

$('.show-eye').show();

//password real time feedback validation 
function initRealTimePasswordFeedback() {
    //setup before functions
    var input = $('#createAccountPasssword');
    //on keyup, start the countdown
    input.on('keyup', function() {
        realTimePasswordFeedback();
    });

    function realTimePasswordFeedback() {
        var upperCase = new RegExp('[A-Z]');
        var lowerCase = new RegExp('[a-z]');
        var numbers = new RegExp('[0-9]');
        var passwordInputValue = $('#createAccountPasssword').val();

        if (passwordInputValue.length > 5) {
            $('#numberOfCharactersValidation').addClass("password-helper__success");
            $("#numberOfCharactersValidation").css("color", "black");
        } else {
            $('#numberOfCharactersValidation').removeClass("password-helper__success");
            $("#numberOfCharactersValidation").removeAttr("style");
        }
        if (passwordInputValue.match(upperCase) && passwordInputValue.match(lowerCase)) {
            $('#upperAndLowerContainedValidation').addClass("password-helper__success");
            $("#upperAndLowerContainedValidation").css("color", "black");
        } else {
            $('#upperAndLowerContainedValidation').removeClass("password-helper__success");
            $("#upperAndLowerContainedValidation").removeAttr("style");
        }
        if (passwordInputValue.match(numbers)) {
            $('#numbersContainedValidation').addClass("password-helper__success");
            $("#numbersContainedValidation").css("color", "black");
        } else {
            $('#numbersContainedValidation').removeClass("password-helper__success");
            $("#numbersContainedValidation").removeAttr("style");
        }
    }
}

$(document).ready(function() {
	var $checkbox = $('#receipt_checkbox');
	var $receiptDiv = $('#receipt_div');

	// Only add the event listener if checkbox is in the dom
	// This prevents it from hiding fields when TAX_TECEIPT is mandatory
	if ($checkbox.length) {
		function toggleReceiptDiv() {
			$receiptDiv.toggle($checkbox.is(':checked'));
		}

		$checkbox.on('change', toggleReceiptDiv);

		toggleReceiptDiv();
	}
	
	var $referredByFriendCheckbox = $('#referredByFriendCheckbox');
	var $referrerMembershipIdDiv = $('#referrerMembershipIdDiv');
	var $textInput = $('#referrerMembershipId');

	if ($referredByFriendCheckbox.length) {
		function toggleReferredByByDiv() {
			var isChecked = $referredByFriendCheckbox.is(':checked');
			$referrerMembershipIdDiv.toggle(isChecked);
			$textInput.prop('disabled', !isChecked)
		}

		$referredByFriendCheckbox.on('change', toggleReferredByByDiv);
		
		toggleReferredByByDiv();
	}
	
	// update countryCodeReceipt selection
	$('#countryReceipt').on('change', function() {
		$('#countryCodeReceipt').val($(this).find(':selected').data('country-code') || '');
	});

	const selected = $('#countryReceipt').find(':selected');
	if (selected.length) {
		$('#countryCodeReceipt').val(selected.data('country-code') || '');
	}
});