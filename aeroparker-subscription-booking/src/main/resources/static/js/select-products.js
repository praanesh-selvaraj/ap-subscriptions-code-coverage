
$(".subscribe-btn").click(function() {
	var addButton = $(this);
	var id = addButton.attr('data-subscription-addTobasket-button');
	var arr = [];
	arr.push(buildProductJson(parseInt(id)));
	var data = JSON.stringify(arr);
	$("#selectedProductData").val(data);
	sendSelectedProductForm();
})

// create a new json element 
function buildProductJson(id) {
	var startDate = $("#changeEntryDate").val();
	var obj = new Object();
	obj.productId = id;
	obj.startDate = startDate;
	return obj;
}

function sendSelectedProductForm() {	
	$("#selectedProductForm").submit();
}