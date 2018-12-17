/**
 * This is a patched varsion of the struts validation.js... which is erronious
 * (uses form name for action to post).
 */
var strutsValidator = new ValidationClient("$!base/validation");
strutsValidator.onErrors = function(input, errors) {

	var form = input.form;

	clearErrorMessages(form);
	clearErrorLabels(form);

    if (errors.fieldErrors) {
        for (var fieldName in errors.fieldErrors) {
            if (form.elements[fieldName].touched) {
                for (var i = 0; i < errors.fieldErrors[fieldName].length; i++) {
                    addError(form.elements[fieldName], errors.fieldErrors[fieldName][i]);
                }
            }
        }
    }
}

function validate(element) {
    // mark the element as touch
    element.touched = true;
    var namespace = element.form.getAttribute('namespace');
    var actionName = element.form.getAttribute('action');

    var start = actionName.lastIndexOf('/');
    start = start === -1 ? 0 : start + 1;

    var end = actionName.lastIndexOf('.action');
    end = end === -1 ? actionName.length : end;

	strutsValidator.validate(element, namespace, actionName.substring(start, end));
}