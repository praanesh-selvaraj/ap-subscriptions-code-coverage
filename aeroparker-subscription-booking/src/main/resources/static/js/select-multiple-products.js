$(document).ready(function() {
	var arr = [];
	
	setUpSelectedProducts(arr);	

	$("[data-subscription-addTobasket-button]").click(function() {
		var addButton = $(this);
		var id = addButton.attr('data-subscription-addTobasket-button');
		var removeButton = $('[data-subscription-removefrombasket-button="' + id + '"]');		
		addButton.hide();
		removeButton.show();	
		arr.push(buildProductJson(parseInt(id)));  // good to use json, so in future we can add more properties to it and pass to controller
		doBasketUpdate(arr);
	});

	$("[data-subscription-removefrombasket]").click(function() {
		var removeLink = $(this);
		var id = removeLink.attr('data-subscription-removefrombasket');
		var removeButton = $('[data-subscription-removefrombasket-button="' + id + '"]');
		var addButton = $('[data-subscription-addTobasket-button="' + id + '"]');
		arr = removeFromBasket(arr, parseInt(id)).filter(x => x);
		addButton.show();
		removeButton.hide();
		doBasketUpdate(arr);
	});
});

function setUpSelectedProducts(arr) {
	var selectedArr = $('div[data-subscription-product-selected="true"]');
	$.each(selectedArr, function(i, val) {
		  var id =  $(this).attr("data-subscription-removefrombasket-button");
		  arr.push(buildProductJson(parseInt(id)));
		});	
	var data = JSON.stringify(arr);
	//Add the data to the form,so when the use clicks on continue we have all the selected products data and we can process in the controller
	$("#selectedProductData").val(data);
}

function doBasketUpdate(arr) {
	var data = JSON.stringify(arr);
	//Add the data to the form,so when the use clicks on continue we have all the selected products data and we can process in the controller
	$("#selectedProductData").val(data);
	$.ajax({
        type: "POST",
        contentType:'application/json',
        url: "basketupdate",
        data:  data ,
        success: function (data) {
        	$('#details-side-bar').empty();
    		$('#details-side-bar').append(data);
        }
    });
}

// create a new json element 
function buildProductJson(id) {
	var startDate = $("#changeEntryDate").val();
	 var obj = new Object();
	 obj.productId = id;
	 obj.startDate = startDate;
	 return obj;
}

// remove from the array the json element of the product id
function removeFromBasket(arr, productId) {
	$.each(arr, function(i, val) {
		   if(val.productId == productId) {
			   delete arr[i];		     
		  }
		});
	return arr;
}

function sendSelectedProductForm() {
	// set the date before submitting the form
	$("#selectedProductForm").submit();
}