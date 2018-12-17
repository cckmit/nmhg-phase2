strutsValidator.onErrors = function(input, errors) {
    var form = input.form;
    if (form == null) return;

    if (errors.fieldErrors) {
        var tables = form.getElementsByTagName("table");
        for (var i = 0; i < tables.length; i++) {
            var table = tables[i];
            if (table.getAttribute("type") == "repeat") {
                errors.fieldErrors = _fixRepeatIndices(table, errors.fieldErrors);
            }
        }
    }

    clearErrorMessages(form);
    clearErrorLabels(form);

    if (errors.fieldErrors) {
        for (var fieldName in errors.fieldErrors) {
            console.debug("error on field " + fieldName);
            if (form.elements[fieldName] == null) {
                console.debug("can't find field " + fieldName + "; ignoring");
                continue;
            }
            if (form.elements[fieldName].touched) {
                for (var i = 0; i < errors.fieldErrors[fieldName].length; i++) {
                    addError(form.elements[fieldName], errors.fieldErrors[fieldName][i]);
                    console.debug("added error to field " + fieldName);
                }
            } else {
                console.debug(fieldName + " is not touched; so ignoring");
            }
        }
    }
}

function _fixRepeatIndices(table, errors) {
    var tbody = table.getElementsByTagName("tbody")[0];
    var collection = tbody.getAttribute("collection");
    var trs = tbody.getElementsByTagName("tr");

    var _errors = {};
    for (var fieldName in errors) {
        if (fieldName.indexOf(collection) == 0) {
            var rest = fieldName.substring(collection.length);
            /\[(\d+)\].*/.test(rest);
            // TODO: gaurd with a null check here; what if RegExp.$1 is null or empty
            var formIndex = RegExp.$1;
            var row = trs[formIndex];
            var realIndex = row.getAttribute("index");
            var realName = collection + "[" + realIndex  + "]" + rest.substring(("[" + formIndex + "]").length);
            _errors[realName] = errors[fieldName];
            delete rest, row, formIndex, realIndex, realName;
        } else {
            _errors[fieldName] = errors[fieldName];
        }
    }
    delete tbody, collection, trs;
    return _errors;
}


function clearErrorMessages(form) {
    var msgs = form.getElementsByTagName("img");
    for (var i = 0; i < msgs.length; i++) {
        if (msgs[i].getAttribute!=undefined&&msgs[i].getAttribute("type") == "error") {
            dojo.dom.removeNode(msgs[i]);
        }
    }
    console.debug("cleared error messages from " + form + " " + form.id);
}

function clearErrorLabels(form) {
    var elems = form.elements;
    for (var i in elems) {
        if (dojo.hasClass(elems[i], "erroneousField")) {
            dojo.removeClass(elems[i], "erroneousField");
        }
    }
    console.debug("cleared error labels from " + form + " " + form.id);
}

function addError(e, errorText) {
    console.debug("adding error message [" + errorText + "] to " + e + " " + e.id);
    // HACK: if the element passed in is a hidden inputfield of a ComboBox, we should
    // apply the erroneousField css class not to the hidden inputfield itself, but to
    // the visible inputfield of the ComboBox
    if (e.getAttribute("dojoattachpoint") == "comboBoxValue") {
        if (!dojo.html.isShowing(e.parentNode)) return; // no point setting the error, if the widget is hidden
        var siblings = e.parentNode.getElementsByTagName("input");
        for (var i = 0; i < siblings.length; i++) {
            if (siblings[i].getAttribute("dojoattachpoint") == "textInputNode") {
                dojo.addClass(siblings[i], "erroneousField");
                break;
            }
        }
    } else {
        if (!dojo.html.isShowing(e)) return; // no point setting the error, if the input is hidden
        dojo.addClass(e, "erroneousField");
    }
    var p = getExpectedParent(e, "td");
    var msgs = p.getElementsByTagName("img");
    var img = null;
    for (var i in msgs) {
        if (msgs[i].getAttribute!=undefined&&msgs[i].getAttribute("type") == "error") {
            img = msgs[i];
        }
    }
    if (img == null) {
        img = document.createElement("img");
        img.src = __validationErrorIcon;
        dojo.addClass(img, "errormsg");
        img.setAttribute("type", "error");
        img.setAttribute("title", "");
        img.setAttribute("for", e.getAttribute("name"));
        p.appendChild(img);
    }
    img.title = img.title + " " + errorText;
    img.alt = errorText;

    delete p, img, msgs;
}
