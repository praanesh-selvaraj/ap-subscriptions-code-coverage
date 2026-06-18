var unrecognisedPlateValidation = false;
var shouldShowVehicleLookup = false;
var lookupMode = false;
var vehicleLookupAffiliateId = 0;

function doVehicleLookup(affiliateId) {
	$('#regLookupError').hide();
	var reg = $('#carReg').val();
	// Make sure the carReg number is filled in before we attempt to do a vehicle look up.
	if (reg.length < 1) {
		// Tell the user that they need to fill the field in.
		$('#carReg').parsley().validate();
		return;
	}

	$.ajax({
		type: 'POST',
		url: "vehicle-lookup",
		data: {affiliateId: vehicleLookupAffiliateId, reg: reg},
		error: function() {
			lookupError();
		},
		success: function(json) {
			var vehilceDetails = JSON.parse(json);
			if (typeof(vehilceDetails) === "undefined" || typeof(vehilceDetails.make) === "undefined"){
				lookupError();
			}
			else{
				$('#regLookupError').hide();
				updateVehicleDetails(vehilceDetails);
				$("#lookupHelp").hide();
				vehicleDetailCheck();
			}
		}
	});
}

function updateVehicleDetails(json) {
	$("#carReg").val(json.reg)
	$("#carmake").val(json.make);
	$("#carmodel").val(json.model); 
	$("#carcol").val(json.colour);
	if(unrecognisedPlateValidation == "true") {
		makeFieldsReadonly(true);
	}
	$('#manualVehicleDetails').show();
}

function lookupError() {
	if (unrecognisedPlateValidation == "true") {
		$('#regLookupError').show();
		cleanFields();
		$('#manualVehicleDetails').show();
	} else {
		cleanFields();
		// If the user changes a correct reg plate to an incorrect one and
		// the checkbox is not checked, hide the vehicle lookup fields
		if ($('#manualVehicleDetails').is(":visible")) {
			$('#manualVehicleDetails').hide();
		}
	}
}

// Make the car model, make and color readonly so the user can't edit them
function makeFieldsReadonly(bool) {
	$("#carmodel").prop("readonly", bool); 
	$("#carmake").prop("readonly", bool); 
	$("#carcol").prop("readonly", bool);
}

function cleanFields() {
	makeFieldsReadonly(false);
	$("#carmake").val("");
	$("#carmodel").val("");
	$("#carcol").val("");
}

function enableManualVehicleDetailsEntry() {
	cleanFields();
	$('#manualVehicleDetails').show();
	$("#findVehicleError").hide();
}

function vehicleDetailCheck() {
	var error = false;
	if ($('#carmake').prop('required') && $('#carmake').val().length == 0) {
		error = true;
	}
	if ($('#carmodel').prop('required') && $('#carmodel').val().length == 0) {
		error = true;
	}
	if ($('#carcol').prop('required') && $('#carcol').val().length == 0) {
		error = true;
	}
	if (!error) {
		$('#regLookupError').hide();
	}
}

$(function() {
	unrecognisedPlateValidation = $("#vehicleLookupAttributes").attr('unrecognisedPlateValidation');
	shouldShowVehicleLookup = $("#vehicleLookupAttributes").attr('shouldShowVehicleLookup');
	lookupMode = $("#vehicleLookupAttributes").attr('lookupMode');
	vehicleLookupAffiliateId = $("#vehicleLookupAttributes").attr('affiliateId');
	
	if (shouldShowVehicleLookup == "true") {
		$('#manualVehicleDetails').hide();
		if (lookupMode == 'AUTO') {
			$('#carReg').on('change blur', function() { doVehicleLookup(vehicleLookupAffiliateId); });
		}
	}
	$('#regLookupError').hide();
	$('#carReg').on('change blur', function() {
		$('#regLookupError').hide();
	});
	$('#carmake').on('change keyup paste mouseup', vehicleDetailCheck);
	$('#carmodel').on('change keyup paste mouseup', vehicleDetailCheck);
});