/*
 * author: Rohit Narayanan
 */
function evaluateAnd() {
	var result = true;
	for (var i = 0; i < arguments.length; i++) {
		result = result && arguments[i];
		if (!result) {
			return false;
		}
	}

	return result;
}
