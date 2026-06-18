$(document).ready(function() {
    var originalAmount = null;
    var originalTotal = null;
    var originalRightToCancelText = null;

    // Check if promo code is pre-populated (from session) and validate it
    var prePopulatedPromo = $('#promoCodeInput').val().trim();
    if (prePopulatedPromo) {
        console.log('Promo already applied in session, validating: ' + prePopulatedPromo);
        applyPromoCode();
    }

    $('#applyPromoBtn').on('click', function() {
        applyPromoCode();
    });

    // Re-validate promo when email changes (if promo is already applied)
    $('#emailInput').on('blur', function() {
        var promoCode = $('#promoCodeInput').val().trim();
        if (promoCode) {
            console.log('Email changed, re-validating promo code');
            applyPromoCode();
        }
    });

    function applyPromoCode() {
        var promoCode = $('#promoCodeInput').val().trim();
        var subscriptionProductId = parseInt($('#subscriptionProductId').val());
        var customerGuid = $('#customerGuid').val().trim();
        var email = $('#emailInput').val().trim();
        // Store original amount and total if not already stored
        if (originalAmount === null) {
            originalAmount = $('#amount').val();
            // Get original total from data attribute (set by server before any discount)
            originalTotal = $('#summaryTotal').data('original-total') || $('#summaryTotal').html();
        }

        $('#promoErrorMessage').hide();
        $('#promoSuccessMessage').hide();

        $.ajax({
            type: 'POST',
            url: 'validate-promotion?customerGuid=' + encodeURIComponent(customerGuid),
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify({
                subscriptionProductId: subscriptionProductId,
                promoCode: promoCode,
                email: email
            }),
            success: function(response) {
                if (response.Map.valid) {
                    // Update the hidden amount field with discounted price
                    $('#amount').val(response.Map.discountedPrice);

                    // Update the booking summary with formatted prices
                    updateBookingSummary(response);
                    if (response.Map.noPaymentRequired){
                        togglePaymentForm(false);
                    }

                    // Use success message from backend (translated)
                    var successMessage = response.Map.successMessage || 'Promo code applied!';
                    $('#promoSuccessText').html(successMessage);
                    $('#promoSuccessMessage').show();

                    // Disable input and apply button
                    // Update Stripe payment intent if the function exists
                    if (typeof updatePaymentIntent === 'function') {
                        updatePaymentIntent();
                    }
                } else {
                    var errorMessage = response.Map.errorMessage || 'Invalid promo code. Please try again.';
                    $('#promoErrorText').text(errorMessage);
                    $('#promoErrorMessage').show();
                    removePromoCode();
                    if (typeof updatePaymentIntent === 'function') {
                        updatePaymentIntent();
                    }
                    togglePaymentForm(true);
                }
            },
            error: function() {
                $('#promoErrorText').text('An error occurred while validating the promo code. Please try again.');
                $('#promoErrorMessage').show();
                removePromoCode();
                if (typeof updatePaymentIntent === 'function') {
                    updatePaymentIntent();
                }
                togglePaymentForm(true);
            }
        });
    }

    function updateBookingSummary(response) {
        // Store original total if not already stored
        if (originalTotal === null) {
            // Get original total from data attribute (set by server before any discount)
            originalTotal = $('#summaryTotal').data('original-total') || $('#summaryTotal').html();
        }
        // Update and show promo discount line
        $('#promoDiscountAmount').html('-' + response.Map.discountFormatted);
        $('#promoDiscountItem').show();
        // Replace the total with the discounted price
        $('#summaryTotal').html(response.Map.discountedPriceFormatted);

        // Update Right to Cancel checkbox label
        if (response.Map.rightToCancelText) {
            var rightToCancelLabel = $('label[for="rightToCancel"]');
            if (rightToCancelLabel.length > 0) {
                // Store original text if not already stored
                if (originalRightToCancelText === null) {
                    originalRightToCancelText = rightToCancelLabel.html();
                }
                rightToCancelLabel.html(response.Map.rightToCancelText);
            }
        }
    }

    function removePromoCode() {
        // Restore original amount
        if (originalAmount !== null) {
            $('#amount').val(originalAmount);
        }

        // Restore original total
        if (originalTotal !== null) {
            $('#summaryTotal').html(originalTotal);
        }
        $('#promoDiscountItem').hide();

        // Restore original Right to Cancel checkbox text
        if (originalRightToCancelText !== null) {
            var rightToCancelLabel = $('label[for="rightToCancel"]');
            if (rightToCancelLabel.length > 0) {
                rightToCancelLabel.html(originalRightToCancelText);
            }
        }
    }

    function togglePaymentForm(display) {
        if (display) {
            $("#payment-details-form-section").show();
        } else {
            $("#payment-details-form-section").hide();
        }
    }

});
