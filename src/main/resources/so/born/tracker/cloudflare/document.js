var document = {};

document.getElementById = function(name) {
	var el = {}
	
	el.innerHTML = "<" + name + "/>"
	el.firstChild = { "href": href };
	
	// display
	el.style = {}

	// jschl-answer result
	el.value = "";
	
	// challenge-form submit
	el.submit = function() {
	}

	document[name.replace('-', '_')] = el;
	return el;
};

document.createElement = document.getElementById;

document.addEventListener = function(what, a) {
	document.start = a;
}