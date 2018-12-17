/*  Prototype JavaScript framework, version 1.5.0_pre1
 *  (c) 2005 Sam Stephenson <sam@conio.net>
 *
 *  Prototype is freely distributable under the terms of an MIT-style license.
 *  For details, see the Prototype web site: http://prototype.conio.net/
 *
/*--------------------------------------------------------------------------*/
/*calls function for pop up div window*/
	        var idx = 0;
	        var xidx = 0;
	        var cascade = 0;
	        var cOffset = 23;
	        var cWins = new Array();
			
	        function showWin(strTitle, winWidth, winHeight, url, params) {
				var cOffset_left = (document.documentElement.clientWidth)/2 -winWidth/2 + (document.body.scrollLeft || document.documentElement.scrollLeft);
				
				var cOffset_top = (document.documentElement.clientHeight)/2 - winHeight/2 + (document.body.scrollTop || document.documentElement.scrollTop);

	            if(cWins.length == 0) {
	                w = new Window('dialog'+xidx, {className: "dialog", title: strTitle,
	                                              top:(cOffset_top), left:(cOffset_left),
	                                              width:winWidth, height:winHeight, zIndex:100, opacity:1, scrollbars: 					
												  true, resizable: false, minimizable: false, maximizable: false,
	                                              showEffectOptions: {duration:0.1}
	                                              })
					w.getContent().innerHTML = document.getElementById(url).innerHTML;
	                w.show();
	                cascade++;
	                idx++;
	                xidx++;
	                cWins.push(w);
	           }

        }

        function closeAll() {



            var x = 0;
            var lag=250;
            for(var i=(cWins.length-1);  i>=0;  i--) {
              setTimeout('cWins['+i+'].hide()',(x*lag));
              x++;
            }
            setTimeout('cWins.length=0; idx=0; cascade=0;',x*lag);
        }

/*--------------------------------------------------------------------------*/


var Prototype = {
  Version: '1.5.0_pre1',
  ScriptFragment: '(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)',

  emptyFunction: function() {},
  K: function(x) {return x}
}

var Class = {
  create: function() {
    return function() {
      this.initialize.apply(this, arguments);
    }
  }
}

var Abstract = new Object();

Object.extend = function(destination, source) {
  for (property in source) {
    destination[property] = source[property];
  }
  return destination;
}

Object.inspect = function(object) {
  try {
    if (object == undefined) return 'undefined';
    if (object == null) return 'null';
    return object.inspect ? object.inspect() : object.toString();
  } catch (e) {
    if (e instanceof RangeError) return '...';
    throw e;
  }
}

Function.prototype.bind = function() {
  var __method = this, args = $A(arguments), object = args.shift();
  return function() {
    return __method.apply(object, args.concat($A(arguments)));
  }
}

Function.prototype.bindAsEventListener = function(object) {
  var __method = this;
  return function(event) {
    return __method.call(object, event || window.event);
  }
}

Object.extend(Number.prototype, {
  toColorPart: function() {
    var digits = this.toString(16);
    if (this < 16) return '0' + digits;
    return digits;
  },

  succ: function() {
    return this + 1;
  },

  times: function(iterator) {
    $R(0, this, true).each(iterator);
    return this;
  }
});

var Try = {
  these: function() {
    var returnValue;

    for (var i = 0; i < arguments.length; i++) {
      var lambda = arguments[i];
      try {
        returnValue = lambda();
        break;
      } catch (e) {}
    }

    return returnValue;
  }
}

/*--------------------------------------------------------------------------*/

var PeriodicalExecuter = Class.create();
PeriodicalExecuter.prototype = {
  initialize: function(callback, frequency) {
    this.callback = callback;
    this.frequency = frequency;
    this.currentlyExecuting = false;

    this.registerCallback();
  },

  registerCallback: function() {
    setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
  },

  onTimerEvent: function() {
    if (!this.currentlyExecuting) {
      try {
        this.currentlyExecuting = true;
        this.callback();
      } finally {
        this.currentlyExecuting = false;
      }
    }
  }
}
Object.extend(String.prototype, {
  gsub: function(pattern, replacement) {
    var result = '', source = this, match;
    replacement = arguments.callee.prepareReplacement(replacement);

    while (source.length > 0) {
      if (match = source.match(pattern)) {
        result += source.slice(0, match.index);
        result += (replacement(match) || '').toString();
        source  = source.slice(match.index + match[0].length);
      } else {
        result += source, source = '';
      }
    }
    return result;
  },

  sub: function(pattern, replacement, count) {
    replacement = this.gsub.prepareReplacement(replacement);
    count = count === undefined ? 1 : count;

    return this.gsub(pattern, function(match) {
      if (--count < 0) return match[0];
      return replacement(match);
    });
  },

  scan: function(pattern, iterator) {
    this.gsub(pattern, iterator);
    return this;
  },

  truncate: function(length, truncation) {
    length = length || 30;
    truncation = truncation === undefined ? '...' : truncation;
    return this.length > length ?
      this.slice(0, length - truncation.length) + truncation : this;
  },

  strip: function() {
    return this.replace(/^\s+/, '').replace(/\s+$/, '');
  },

  stripTags: function() {
    return this.replace(/<\/?[^>]+>/gi, '');
  },

  stripScripts: function() {
    return this.replace(new RegExp(Prototype.ScriptFragment, 'img'), '');
  },

  extractScripts: function() {
    var matchAll = new RegExp(Prototype.ScriptFragment, 'img');
    var matchOne = new RegExp(Prototype.ScriptFragment, 'im');
    return (this.match(matchAll) || []).map(function(scriptTag) {
      return (scriptTag.match(matchOne) || ['', ''])[1];
    });
  },

  evalScripts: function() {
    return this.extractScripts().map(eval);
  },

  escapeHTML: function() {
    var div = document.createElement('div');
    var text = document.createTextNode(this);
    div.appendChild(text);
    return div.innerHTML;
  },

  unescapeHTML: function() {
    var div = document.createElement('div');
    div.innerHTML = this.stripTags();
    return div.childNodes[0] ? div.childNodes[0].nodeValue : '';
  },

  toQueryParams: function() {
    var pairs = this.match(/^\??(.*)$/)[1].split('&');
    return pairs.inject({}, function(params, pairString) {
      var pair = pairString.split('=');
      params[pair[0]] = pair[1];
      return params;
    });
  },

  toArray: function() {
    return this.split('');
  },

  camelize: function() {
    var oStringList = this.split('-');
    if (oStringList.length == 1) return oStringList[0];

    var camelizedString = this.indexOf('-') == 0
      ? oStringList[0].charAt(0).toUpperCase() + oStringList[0].substring(1)
      : oStringList[0];

    for (var i = 1, len = oStringList.length; i < len; i++) {
      var s = oStringList[i];
      camelizedString += s.charAt(0).toUpperCase() + s.substring(1);
    }

    return camelizedString;
  },

  inspect: function() {
    return "'" + this.replace(/\\/g, '\\\\').replace(/'/g, '\\\'') + "'";
  }
});

String.prototype.gsub.prepareReplacement = function(replacement) {
  if (typeof replacement == 'function') return replacement;
  var template = new Template(replacement);
  return function(match) { return template.evaluate(match) };
}

String.prototype.parseQuery = String.prototype.toQueryParams;

var Template = Class.create();
Template.Pattern = /(^|.|\r|\n)(#\{(.*?)\})/;
Template.prototype = {
  initialize: function(template, pattern) {
    this.template = template.toString();
    this.pattern  = pattern || Template.Pattern;
  },

  evaluate: function(object) {
    return this.template.gsub(this.pattern, function(match) {
      var before = match[1];
      if (before == '\\') return match[2];
      return before + (object[match[3]] || '').toString();
    });
  }
}

var $break    = new Object();
var $continue = new Object();

var Enumerable = {
  each: function(iterator) {
    var index = 0;
    try {
      this._each(function(value) {
        try {
          iterator(value, index++);
        } catch (e) {
          if (e != $continue) throw e;
        }
      });
    } catch (e) {
      if (e != $break) throw e;
    }
  },

  all: function(iterator) {
    var result = true;
    this.each(function(value, index) {
      result = result && !!(iterator || Prototype.K)(value, index);
      if (!result) throw $break;
    });
    return result;
  },

  any: function(iterator) {
    var result = true;
    this.each(function(value, index) {
      if (result = !!(iterator || Prototype.K)(value, index))
        throw $break;
    });
    return result;
  },

  collect: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      results.push(iterator(value, index));
    });
    return results;
  },

  detect: function (iterator) {
    var result;
    this.each(function(value, index) {
      if (iterator(value, index)) {
        result = value;
        throw $break;
      }
    });
    return result;
  },

  findAll: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      if (iterator(value, index))
        results.push(value);
    });
    return results;
  },

  grep: function(pattern, iterator) {
    var results = [];
    this.each(function(value, index) {
      var stringValue = value.toString();
      if (stringValue.match(pattern))
        results.push((iterator || Prototype.K)(value, index));
    })
    return results;
  },

  include: function(object) {
    var found = false;
    this.each(function(value) {
      if (value == object) {
        found = true;
        throw $break;
      }
    });
    return found;
  },

  inject: function(memo, iterator) {
    this.each(function(value, index) {
      memo = iterator(memo, value, index);
    });
    return memo;
  },

  invoke: function(method) {
    var args = $A(arguments).slice(1);
    return this.collect(function(value) {
      return value[method].apply(value, args);
    });
  },

  max: function(iterator) {
    var result;
    this.each(function(value, index) {
      value = (iterator || Prototype.K)(value, index);
      if (value >= (result || value))
        result = value;
    });
    return result;
  },

  min: function(iterator) {
    var result;
    this.each(function(value, index) {
      value = (iterator || Prototype.K)(value, index);
      if (value <= (result || value))
        result = value;
    });
    return result;
  },

  partition: function(iterator) {
    var trues = [], falses = [];
    this.each(function(value, index) {
      ((iterator || Prototype.K)(value, index) ?
        trues : falses).push(value);
    });
    return [trues, falses];
  },

  pluck: function(property) {
    var results = [];
    this.each(function(value, index) {
      results.push(value[property]);
    });
    return results;
  },

  reject: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      if (!iterator(value, index))
        results.push(value);
    });
    return results;
  },

  sortBy: function(iterator) {
    return this.collect(function(value, index) {
      return {value: value, criteria: iterator(value, index)};
    }).sort(function(left, right) {
      var a = left.criteria, b = right.criteria;
      return a < b ? -1 : a > b ? 1 : 0;
    }).pluck('value');
  },

  toArray: function() {
    return this.collect(Prototype.K);
  },

  zip: function() {
    var iterator = Prototype.K, args = $A(arguments);
    if (typeof args.last() == 'function')
      iterator = args.pop();

    var collections = [this].concat(args).map($A);
    return this.map(function(value, index) {
      return iterator(collections.pluck(index));
    });
  },

  inspect: function() {
    return '#<Enumerable:' + this.toArray().inspect() + '>';
  }
}

Object.extend(Enumerable, {
  map:     Enumerable.collect,
  find:    Enumerable.detect,
  select:  Enumerable.findAll,
  member:  Enumerable.include,
  entries: Enumerable.toArray
});
var $A = Array.from = function(iterable) {
  if (!iterable) return [];
  if (iterable.toArray) {
    return iterable.toArray();
  } else {
    var results = [];
    for (var i = 0; i < iterable.length; i++)
      results.push(iterable[i]);
    return results;
  }
}

Object.extend(Array.prototype, Enumerable);

Array.prototype._reverse = Array.prototype.reverse;

Object.extend(Array.prototype, {
  _each: function(iterator) {
    for (var i = 0; i < this.length; i++)
      iterator(this[i]);
  },

  clear: function() {
    this.length = 0;
    return this;
  },

  first: function() {
    return this[0];
  },

  last: function() {
    return this[this.length - 1];
  },

  compact: function() {
    return this.select(function(value) {
      return value != undefined || value != null;
    });
  },

  flatten: function() {
    return this.inject([], function(array, value) {
      return array.concat(value.constructor == Array ?
        value.flatten() : [value]);
    });
  },

  without: function() {
    var values = $A(arguments);
    return this.select(function(value) {
      return !values.include(value);
    });
  },

  indexOf: function(object) {
    for (var i = 0; i < this.length; i++)
      if (this[i] == object) return i;
    return -1;
  },

  reverse: function(inline) {
    return (inline !== false ? this : this.toArray())._reverse();
  },

  shift: function() {
    var result = this[0];
    for (var i = 0; i < this.length - 1; i++)
      this[i] = this[i + 1];
    this.length--;
    return result;
  },

  inspect: function() {
    return '[' + this.map(Object.inspect).join(', ') + ']';
  }
});
var Hash = {
  _each: function(iterator) {
    for (key in this) {
      var value = this[key];
      if (typeof value == 'function') continue;

      var pair = [key, value];
      pair.key = key;
      pair.value = value;
      iterator(pair);
    }
  },

  keys: function() {
    return this.pluck('key');
  },

  values: function() {
    return this.pluck('value');
  },

  merge: function(hash) {
    return $H(hash).inject($H(this), function(mergedHash, pair) {
      mergedHash[pair.key] = pair.value;
      return mergedHash;
    });
  },

  toQueryString: function() {
    return this.map(function(pair) {
      return pair.map(encodeURIComponent).join('=');
    }).join('&');
  },

  inspect: function() {
    return '#<Hash:{' + this.map(function(pair) {
      return pair.map(Object.inspect).join(': ');
    }).join(', ') + '}>';
  }
}

function $H(object) {
  var hash = Object.extend({}, object || {});
  Object.extend(hash, Enumerable);
  Object.extend(hash, Hash);
  return hash;
}
ObjectRange = Class.create();
Object.extend(ObjectRange.prototype, Enumerable);
Object.extend(ObjectRange.prototype, {
  initialize: function(start, end, exclusive) {
    this.start = start;
    this.end = end;
    this.exclusive = exclusive;
  },

  _each: function(iterator) {
    var value = this.start;
    do {
      iterator(value);
      value = value.succ();
    } while (this.include(value));
  },

  include: function(value) {
    if (value < this.start)
      return false;
    if (this.exclusive)
      return value < this.end;
    return value <= this.end;
  }
});

var $R = function(start, end, exclusive) {
  return new ObjectRange(start, end, exclusive);
}

var Ajax = {
  getTransport: function() {
    return Try.these(
      function() {return new ActiveXObject('Msxml2.XMLHTTP')},
      function() {return new ActiveXObject('Microsoft.XMLHTTP')},
      function() {return new XMLHttpRequest()}
    ) || false;
  },

  activeRequestCount: 0
}

Ajax.Responders = {
  responders: [],

  _each: function(iterator) {
    this.responders._each(iterator);
  },

  register: function(responderToAdd) {
    if (!this.include(responderToAdd))
      this.responders.push(responderToAdd);
  },

  unregister: function(responderToRemove) {
    this.responders = this.responders.without(responderToRemove);
  },

  dispatch: function(callback, request, transport, json) {
    this.each(function(responder) {
      if (responder[callback] && typeof responder[callback] == 'function') {
        try {
          responder[callback].apply(responder, [request, transport, json]);
        } catch (e) {}
      }
    });
  }
};

Object.extend(Ajax.Responders, Enumerable);

Ajax.Responders.register({
  onCreate: function() {
    Ajax.activeRequestCount++;
  },

  onComplete: function() {
    Ajax.activeRequestCount--;
  }
});

Ajax.Base = function() {};
Ajax.Base.prototype = {
  setOptions: function(options) {
    this.options = {
      method:       'post',
      asynchronous: true,
      parameters:   ''
    }
    Object.extend(this.options, options || {});
  },

  responseIsSuccess: function() {
    return this.transport.status == undefined
        || this.transport.status == 0
        || (this.transport.status >= 200 && this.transport.status < 300);
  },

  responseIsFailure: function() {
    return !this.responseIsSuccess();
  }
}

Ajax.Request = Class.create();
Ajax.Request.Events =
  ['Uninitialized', 'Loading', 'Loaded', 'Interactive', 'Complete'];

Ajax.Request.prototype = Object.extend(new Ajax.Base(), {
  initialize: function(url, options) {
    this.transport = Ajax.getTransport();
    this.setOptions(options);
    this.request(url);
  },

  request: function(url) {
    var parameters = this.options.parameters || '';
    if (parameters.length > 0) parameters += '&_=';

    try {
      this.url = url;
      if (this.options.method == 'get' && parameters.length > 0)
        this.url += (this.url.match(/\?/) ? '&' : '?') + parameters;

      Ajax.Responders.dispatch('onCreate', this, this.transport);

      this.transport.open(this.options.method, this.url,
        this.options.asynchronous);

      if (this.options.asynchronous) {
        this.transport.onreadystatechange = this.onStateChange.bind(this);
        setTimeout((function() {this.respondToReadyState(1)}).bind(this), 10);
      }

      this.setRequestHeaders();

      var body = this.options.postBody ? this.options.postBody : parameters;
      this.transport.send(this.options.method == 'post' ? body : null);

    } catch (e) {
      this.dispatchException(e);
    }
  },

  setRequestHeaders: function() {
    var requestHeaders =
      ['X-Requested-With', 'XMLHttpRequest',
       'X-Prototype-Version', Prototype.Version,
       'Accept', 'text/javascript, text/html, application/xml, text/xml, */*'];

    if (this.options.method == 'post') {
      requestHeaders.push('Content-type',
        'application/x-www-form-urlencoded');

      /* Force "Connection: close" for Mozilla browsers to work around
       * a bug where XMLHttpReqeuest sends an incorrect Content-length
       * header. See Mozilla Bugzilla #246651.
       */
      if (this.transport.overrideMimeType)
        requestHeaders.push('Connection', 'close');
    }

    if (this.options.requestHeaders)
      requestHeaders.push.apply(requestHeaders, this.options.requestHeaders);

    for (var i = 0; i < requestHeaders.length; i += 2)
      this.transport.setRequestHeader(requestHeaders[i], requestHeaders[i+1]);
  },

  onStateChange: function() {
    var readyState = this.transport.readyState;
    if (readyState != 1)
      this.respondToReadyState(this.transport.readyState);
  },

  header: function(name) {
    try {
      return this.transport.getResponseHeader(name);
    } catch (e) {}
  },

  evalJSON: function() {
    try {
      return eval(this.header('X-JSON'));
    } catch (e) {}
  },

  evalResponse: function() {
    try {
      return eval(this.transport.responseText);
    } catch (e) {
      this.dispatchException(e);
    }
  },

  respondToReadyState: function(readyState) {
    var event = Ajax.Request.Events[readyState];
    var transport = this.transport, json = this.evalJSON();

    if (event == 'Complete') {
      try {
        (this.options['on' + this.transport.status]
         || this.options['on' + (this.responseIsSuccess() ? 'Success' : 'Failure')]
         || Prototype.emptyFunction)(transport, json);
      } catch (e) {
        this.dispatchException(e);
      }

      if ((this.header('Content-type') || '').match(/^text\/javascript/i))
        this.evalResponse();
    }

    try {
      (this.options['on' + event] || Prototype.emptyFunction)(transport, json);
      Ajax.Responders.dispatch('on' + event, this, transport, json);
    } catch (e) {
      this.dispatchException(e);
    }

    /* Avoid memory leak in MSIE: clean up the oncomplete event handler */
    if (event == 'Complete')
      this.transport.onreadystatechange = Prototype.emptyFunction;
  },

  dispatchException: function(exception) {
    (this.options.onException || Prototype.emptyFunction)(this, exception);
    Ajax.Responders.dispatch('onException', this, exception);
  }
});

Ajax.Updater = Class.create();

Object.extend(Object.extend(Ajax.Updater.prototype, Ajax.Request.prototype), {
  initialize: function(container, url, options) {
    this.containers = {
      success: container.success ? $(container.success) : $(container),
      failure: container.failure ? $(container.failure) :
        (container.success ? null : $(container))
    }

    this.transport = Ajax.getTransport();
    this.setOptions(options);

    var onComplete = this.options.onComplete || Prototype.emptyFunction;
    this.options.onComplete = (function(transport, object) {
      this.updateContent();
      onComplete(transport, object);
    }).bind(this);

    this.request(url);
  },

  updateContent: function() {
    var receiver = this.responseIsSuccess() ?
      this.containers.success : this.containers.failure;
    var response = this.transport.responseText;

    if (!this.options.evalScripts)
      response = response.stripScripts();

    if (receiver) {
      if (this.options.insertion) {
        new this.options.insertion(receiver, response);
      } else {
        Element.update(receiver, response);
      }
    }

    if (this.responseIsSuccess()) {
      if (this.onComplete)
        setTimeout(this.onComplete.bind(this), 10);
    }
  }
});

Ajax.PeriodicalUpdater = Class.create();
Ajax.PeriodicalUpdater.prototype = Object.extend(new Ajax.Base(), {
  initialize: function(container, url, options) {
    this.setOptions(options);
    this.onComplete = this.options.onComplete;

    this.frequency = (this.options.frequency || 2);
    this.decay = (this.options.decay || 1);

    this.updater = {};
    this.container = container;
    this.url = url;

    this.start();
  },

  start: function() {
    this.options.onComplete = this.updateComplete.bind(this);
    this.onTimerEvent();
  },

  stop: function() {
    this.updater.onComplete = undefined;
    clearTimeout(this.timer);
    (this.onComplete || Prototype.emptyFunction).apply(this, arguments);
  },

  updateComplete: function(request) {
    if (this.options.decay) {
      this.decay = (request.responseText == this.lastText ?
        this.decay * this.options.decay : 1);

      this.lastText = request.responseText;
    }
    this.timer = setTimeout(this.onTimerEvent.bind(this),
      this.decay * this.frequency * 1000);
  },

  onTimerEvent: function() {
    this.updater = new Ajax.Updater(this.container, this.url, this.options);
  }
});
function $() {
  var results = [], element;
  for (var i = 0; i < arguments.length; i++) {
    element = arguments[i];
    if (typeof element == 'string')
      element = document.getElementById(element);
    results.push(Element.extend(element));
  }
  return results.length < 2 ? results[0] : results;
}

document.getElementsByClassName = function(className, parentElement) {
  var children = ($(parentElement) || document.body).getElementsByTagName('*');
  return $A(children).inject([], function(elements, child) {
    if (child.className.match(new RegExp("(^|\\s)" + className + "(\\s|$)")))
      elements.push(Element.extend(child));
    return elements;
  });
}

/*--------------------------------------------------------------------------*/

if (!window.Element)
  var Element = new Object();

Element.extend = function(element) {
  if (!element) return;

  if (!element._extended && element.tagName && element != window) {
    var methods = Element.Methods;
    for (property in methods) {
      var value = methods[property];
      if (typeof value == 'function')
        element[property] = value.bind(null, element);
    }
  }

  element._extended = true;
  return element;
}

Element.Methods = {
  visible: function(element) {
    return $(element).style.display != 'none';
  },

  toggle: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      Element[Element.visible(element) ? 'hide' : 'show'](element);
    }
  },

  hide: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      element.style.display = 'none';
    }
  },

  show: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      element.style.display = '';
    }
  },

  remove: function(element) {
    element = $(element);
    element.parentNode.removeChild(element);
  },

  update: function(element, html) {
    $(element).innerHTML = html.stripScripts();
    setTimeout(function() {html.evalScripts()}, 10);
  },

  replace: function(element, html) {
    element = $(element);
    if (element.outerHTML) {
      element.outerHTML = html.stripScripts();
    } else {
      var range = element.ownerDocument.createRange();
      range.selectNodeContents(element);
      element.parentNode.replaceChild(
        range.createContextualFragment(html.stripScripts()), element);
    }
    setTimeout(function() {html.evalScripts()}, 10);
  },

  getHeight: function(element) {
    element = $(element);
    return element.offsetHeight;
  },

  classNames: function(element) {
    return new Element.ClassNames(element);
  },

  hasClassName: function(element, className) {
    if (!(element = $(element))) return;
    return Element.classNames(element).include(className);
  },

  addClassName: function(element, className) {
    if (!(element = $(element))) return;
    return Element.classNames(element).add(className);
  },

  removeClassName: function(element, className) {
    if (!(element = $(element))) return;
    return Element.classNames(element).remove(className);
  },

  // removes whitespace-only text node children
  cleanWhitespace: function(element) {
    element = $(element);
    for (var i = 0; i < element.childNodes.length; i++) {
      var node = element.childNodes[i];
      if (node.nodeType == 3 && !/\S/.test(node.nodeValue))
        Element.remove(node);
    }
  },

  empty: function(element) {
    return $(element).innerHTML.match(/^\s*$/);
  },

  childOf: function(element, ancestor) {
    element = $(element), ancestor = $(ancestor);
    while (element = element.parentNode)
      if (element == ancestor) return true;
    return false;
  },

  scrollTo: function(element) {
    element = $(element);
    var x = element.x ? element.x : element.offsetLeft,
        y = element.y ? element.y : element.offsetTop;
    window.scrollTo(x, y);
  },

  getStyle: function(element, style) {
    element = $(element);
    var value = element.style[style.camelize()];
    if (!value) {
      if (document.defaultView && document.defaultView.getComputedStyle) {
        var css = document.defaultView.getComputedStyle(element, null);
        value = css ? css.getPropertyValue(style) : null;
      } else if (element.currentStyle) {
        value = element.currentStyle[style.camelize()];
      }
    }

    if (window.opera && ['left', 'top', 'right', 'bottom'].include(style))
      if (Element.getStyle(element, 'position') == 'static') value = 'auto';

    return value == 'auto' ? null : value;
  },

  setStyle: function(element, style) {
    element = $(element);
    for (name in style)
      element.style[name.camelize()] = style[name];
  },

  getDimensions: function(element) {
    element = $(element);
    if (Element.getStyle(element, 'display') != 'none')
      return {width: element.offsetWidth, height: element.offsetHeight};

    // All *Width and *Height properties give 0 on elements with display none,
    // so enable the element temporarily
    var els = element.style;
    var originalVisibility = els.visibility;
    var originalPosition = els.position;
    els.visibility = 'hidden';
    els.position = 'absolute';
    els.display = '';
    var originalWidth = element.clientWidth;
    var originalHeight = element.clientHeight;
    els.display = 'none';
    els.position = originalPosition;
    els.visibility = originalVisibility;
    return {width: originalWidth, height: originalHeight};
  },

  makePositioned: function(element) {
    element = $(element);
    var pos = Element.getStyle(element, 'position');
    if (pos == 'static' || !pos) {
      element._madePositioned = true;
      element.style.position = 'relative';
      // Opera returns the offset relative to the positioning context, when an
      // element is position relative but top and left have not been defined
      if (window.opera) {
        element.style.top = 0;
        element.style.left = 0;
      }
    }
  },

  undoPositioned: function(element) {
    element = $(element);
    if (element._madePositioned) {
      element._madePositioned = undefined;
      element.style.position =
        element.style.top =
        element.style.left =
        element.style.bottom =
        element.style.right = '';
    }
  },

  makeClipping: function(element) {
    element = $(element);
    if (element._overflow) return;
    element._overflow = element.style.overflow;
    if ((Element.getStyle(element, 'overflow') || 'visible') != 'hidden')
      element.style.overflow = 'hidden';
  },

  undoClipping: function(element) {
    element = $(element);
    if (element._overflow) return;
    element.style.overflow = element._overflow;
    element._overflow = undefined;
  }
}

Object.extend(Element, Element.Methods);

var Toggle = new Object();
Toggle.display = Element.toggle;

/*--------------------------------------------------------------------------*/

Abstract.Insertion = function(adjacency) {
  this.adjacency = adjacency;
}

Abstract.Insertion.prototype = {
  initialize: function(element, content) {
    this.element = $(element);
    this.content = content.stripScripts();

    if (this.adjacency && this.element.insertAdjacentHTML) {
      try {
        this.element.insertAdjacentHTML(this.adjacency, this.content);
      } catch (e) {
        if (this.element.tagName.toLowerCase() == 'tbody') {
          this.insertContent(this.contentFromAnonymousTable());
        } else {
          throw e;
        }
      }
    } else {
      this.range = this.element.ownerDocument.createRange();
      if (this.initializeRange) this.initializeRange();
      this.insertContent([this.range.createContextualFragment(this.content)]);
    }

    setTimeout(function() {content.evalScripts()}, 10);
  },

  contentFromAnonymousTable: function() {
    var div = document.createElement('div');
    div.innerHTML = '<table><tbody>' + this.content + '</tbody></table>';
    return $A(div.childNodes[0].childNodes[0].childNodes);
  }
}

var Insertion = new Object();

Insertion.Before = Class.create();
Insertion.Before.prototype = Object.extend(new Abstract.Insertion('beforeBegin'), {
  initializeRange: function() {
    this.range.setStartBefore(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.parentNode.insertBefore(fragment, this.element);
    }).bind(this));
  }
});

Insertion.Top = Class.create();
Insertion.Top.prototype = Object.extend(new Abstract.Insertion('afterBegin'), {
  initializeRange: function() {
    this.range.selectNodeContents(this.element);
    this.range.collapse(true);
  },

  insertContent: function(fragments) {
    fragments.reverse(false).each((function(fragment) {
      this.element.insertBefore(fragment, this.element.firstChild);
    }).bind(this));
  }
});

Insertion.Bottom = Class.create();
Insertion.Bottom.prototype = Object.extend(new Abstract.Insertion('beforeEnd'), {
  initializeRange: function() {
    this.range.selectNodeContents(this.element);
    this.range.collapse(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.appendChild(fragment);
    }).bind(this));
  }
});

Insertion.After = Class.create();
Insertion.After.prototype = Object.extend(new Abstract.Insertion('afterEnd'), {
  initializeRange: function() {
    this.range.setStartAfter(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.parentNode.insertBefore(fragment,
        this.element.nextSibling);
    }).bind(this));
  }
});

/*--------------------------------------------------------------------------*/

Element.ClassNames = Class.create();
Element.ClassNames.prototype = {
  initialize: function(element) {
    this.element = $(element);
  },

  _each: function(iterator) {
    this.element.className.split(/\s+/).select(function(name) {
      return name.length > 0;
    })._each(iterator);
  },

  set: function(className) {
    this.element.className = className;
  },

  add: function(classNameToAdd) {
    if (this.include(classNameToAdd)) return;
    this.set(this.toArray().concat(classNameToAdd).join(' '));
  },

  remove: function(classNameToRemove) {
    if (!this.include(classNameToRemove)) return;
    this.set(this.select(function(className) {
      return className != classNameToRemove;
    }).join(' '));
  },

  toString: function() {
    return this.toArray().join(' ');
  }
}

Object.extend(Element.ClassNames.prototype, Enumerable);
var Selector = Class.create();
Selector.prototype = {
  initialize: function(expression) {
    this.params = {classNames: []};
    this.expression = expression.toString().strip();
    this.parseExpression();
    this.compileMatcher();
  },

  parseExpression: function() {
    function abort(message) { throw 'Parse error in selector: ' + message; }

    if (this.expression == '')  abort('empty expression');

    var params = this.params, expr = this.expression, match, modifier, clause, rest;
    while (match = expr.match(/^(.*)\[([a-z0-9_:-]+?)(?:([~\|!]?=)(?:"([^"]*)"|([^\]\s]*)))?\]$/i)) {
      params.attributes = params.attributes || [];
      params.attributes.push({name: match[2], operator: match[3], value: match[4] || match[5] || ''});
      expr = match[1];
    }

    if (expr == '*') return this.params.wildcard = true;

    while (match = expr.match(/^([^a-z0-9_-])?([a-z0-9_-]+)(.*)/i)) {
      modifier = match[1], clause = match[2], rest = match[3];
      switch (modifier) {
        case '#':       params.id = clause; break;
        case '.':       params.classNames.push(clause); break;
        case '':
        case undefined: params.tagName = clause.toUpperCase(); break;
        default:        abort(expr.inspect());
      }
      expr = rest;
    }

    if (expr.length > 0) abort(expr.inspect());
  },

  buildMatchExpression: function() {
    var params = this.params, conditions = [], clause;

    if (params.wildcard)
      conditions.push('true');
    if (clause = params.id)
      conditions.push('element.id == ' + clause.inspect());
    if (clause = params.tagName)
      conditions.push('element.tagName.toUpperCase() == ' + clause.inspect());
    if ((clause = params.classNames).length > 0)
      for (var i = 0; i < clause.length; i++)
        conditions.push('Element.hasClassName(element, ' + clause[i].inspect() + ')');
    if (clause = params.attributes) {
      clause.each(function(attribute) {
        var value = 'element.getAttribute(' + attribute.name.inspect() + ')';
        var splitValueBy = function(delimiter) {
          return value + ' && ' + value + '.split(' + delimiter.inspect() + ')';
        }

        switch (attribute.operator) {
          case '=':       conditions.push(value + ' == ' + attribute.value.inspect()); break;
          case '~=':      conditions.push(splitValueBy(' ') + '.include(' + attribute.value.inspect() + ')'); break;
          case '|=':      conditions.push(
                            splitValueBy('-') + '.first().toUpperCase() == ' + attribute.value.toUpperCase().inspect()
                          ); break;
          case '!=':      conditions.push(value + ' != ' + attribute.value.inspect()); break;
          case '':
          case undefined: conditions.push(value + ' != null'); break;
          default:        throw 'Unknown operator ' + attribute.operator + ' in selector';
        }
      });
    }

    return conditions.join(' && ');
  },

  compileMatcher: function() {
    this.match = new Function('element', 'if (!element.tagName) return false; \
      return ' + this.buildMatchExpression());
  },

  findElements: function(scope) {
    var element;

    if (element = $(this.params.id))
      if (this.match(element))
        if (!scope || Element.childOf(element, scope))
          return [element];

    scope = (scope || document).getElementsByTagName(this.params.tagName || '*');

    var results = [];
    for (var i = 0; i < scope.length; i++)
      if (this.match(element = scope[i]))
        results.push(Element.extend(element));

    return results;
  },

  toString: function() {
    return this.expression;
  }
}

function $$() {
  return $A(arguments).map(function(expression) {
    return expression.strip().split(/\s+/).inject([null], function(results, expr) {
      var selector = new Selector(expr);
      return results.map(selector.findElements.bind(selector)).flatten();
    });
  }).flatten();
}
var Field = {
  clear: function() {
    for (var i = 0; i < arguments.length; i++)
      $(arguments[i]).value = '';
  },

  focus: function(element) {
    $(element).focus();
  },

  present: function() {
    for (var i = 0; i < arguments.length; i++)
      if ($(arguments[i]).value == '') return false;
    return true;
  },

  select: function(element) {
    $(element).select();
  },

  activate: function(element) {
    element = $(element);
    element.focus();
    if (element.select)
      element.select();
  }
}

/*--------------------------------------------------------------------------*/

var Form = {
  serialize: function(form) {
    var elements = Form.getElements($(form));
    var queryComponents = new Array();

    for (var i = 0; i < elements.length; i++) {
      var queryComponent = Form.Element.serialize(elements[i]);
      if (queryComponent)
        queryComponents.push(queryComponent);
    }

    return queryComponents.join('&');
  },

  getElements: function(form) {
    form = $(form);
    var elements = new Array();

    for (tagName in Form.Element.Serializers) {
      var tagElements = form.getElementsByTagName(tagName);
      for (var j = 0; j < tagElements.length; j++)
        elements.push(tagElements[j]);
    }
    return elements;
  },

  getInputs: function(form, typeName, name) {
    form = $(form);
    var inputs = form.getElementsByTagName('input');

    if (!typeName && !name)
      return inputs;

    var matchingInputs = new Array();
    for (var i = 0; i < inputs.length; i++) {
      var input = inputs[i];
      if ((typeName && input.type != typeName) ||
          (name && input.name != name))
        continue;
      matchingInputs.push(input);
    }

    return matchingInputs;
  },

  disable: function(form) {
    var elements = Form.getElements(form);
    for (var i = 0; i < elements.length; i++) {
      var element = elements[i];
      element.blur();
      element.disabled = 'true';
    }
  },

  enable: function(form) {
    var elements = Form.getElements(form);
    for (var i = 0; i < elements.length; i++) {
      var element = elements[i];
      element.disabled = '';
    }
  },

  findFirstElement: function(form) {
    return Form.getElements(form).find(function(element) {
      return element.type != 'hidden' && !element.disabled &&
        ['input', 'select', 'textarea'].include(element.tagName.toLowerCase());
    });
  },

  focusFirstElement: function(form) {
    Field.activate(Form.findFirstElement(form));
  },

  reset: function(form) {
    $(form).reset();
  }
}

Form.Element = {
  serialize: function(element) {
    element = $(element);
    var method = element.tagName.toLowerCase();
    var parameter = Form.Element.Serializers[method](element);

    if (parameter) {
      var key = encodeURIComponent(parameter[0]);
      if (key.length == 0) return;

      if (parameter[1].constructor != Array)
        parameter[1] = [parameter[1]];

      return parameter[1].map(function(value) {
        return key + '=' + encodeURIComponent(value);
      }).join('&');
    }
  },

  getValue: function(element) {
    element = $(element);
    var method = element.tagName.toLowerCase();
    var parameter = Form.Element.Serializers[method](element);

    if (parameter)
      return parameter[1];
  }
}

Form.Element.Serializers = {
  input: function(element) {
    switch (element.type.toLowerCase()) {
      case 'submit':
      case 'hidden':
      case 'password':
      case 'text':
        return Form.Element.Serializers.textarea(element);
      case 'checkbox':
      case 'radio':
        return Form.Element.Serializers.inputSelector(element);
    }
    return false;
  },

  inputSelector: function(element) {
    if (element.checked)
      return [element.name, element.value];
  },

  textarea: function(element) {
    return [element.name, element.value];
  },

  select: function(element) {
    return Form.Element.Serializers[element.type == 'select-one' ?
      'selectOne' : 'selectMany'](element);
  },

  selectOne: function(element) {
    var value = '', opt, index = element.selectedIndex;
    if (index >= 0) {
      opt = element.options[index];
      value = opt.value;
      if (!value && !('value' in opt))
        value = opt.text;
    }
    return [element.name, value];
  },

  selectMany: function(element) {
    var value = new Array();
    for (var i = 0; i < element.length; i++) {
      var opt = element.options[i];
      if (opt.selected) {
        var optValue = opt.value;
        if (!optValue && !('value' in opt))
          optValue = opt.text;
        value.push(optValue);
      }
    }
    return [element.name, value];
  }
}

/*--------------------------------------------------------------------------*/

var $F = Form.Element.getValue;

/*--------------------------------------------------------------------------*/

Abstract.TimedObserver = function() {}
Abstract.TimedObserver.prototype = {
  initialize: function(element, frequency, callback) {
    this.frequency = frequency;
    this.element   = $(element);
    this.callback  = callback;

    this.lastValue = this.getValue();
    this.registerCallback();
  },

  registerCallback: function() {
    setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
  },

  onTimerEvent: function() {
    var value = this.getValue();
    if (this.lastValue != value) {
      this.callback(this.element, value);
      this.lastValue = value;
    }
  }
}

Form.Element.Observer = Class.create();
Form.Element.Observer.prototype = Object.extend(new Abstract.TimedObserver(), {
  getValue: function() {
    return Form.Element.getValue(this.element);
  }
});

Form.Observer = Class.create();
Form.Observer.prototype = Object.extend(new Abstract.TimedObserver(), {
  getValue: function() {
    return Form.serialize(this.element);
  }
});

/*--------------------------------------------------------------------------*/

Abstract.EventObserver = function() {}
Abstract.EventObserver.prototype = {
  initialize: function(element, callback) {
    this.element  = $(element);
    this.callback = callback;

    this.lastValue = this.getValue();
    if (this.element.tagName.toLowerCase() == 'form')
      this.registerFormCallbacks();
    else
      this.registerCallback(this.element);
  },

  onElementEvent: function() {
    var value = this.getValue();
    if (this.lastValue != value) {
      this.callback(this.element, value);
      this.lastValue = value;
    }
  },

  registerFormCallbacks: function() {
    var elements = Form.getElements(this.element);
    for (var i = 0; i < elements.length; i++)
      this.registerCallback(elements[i]);
  },

  registerCallback: function(element) {
    if (element.type) {
      switch (element.type.toLowerCase()) {
        case 'checkbox':
        case 'radio':
          Event.observe(element, 'click', this.onElementEvent.bind(this));
          break;
        case 'password':
        case 'text':
        case 'textarea':
        case 'select-one':
        case 'select-multiple':
          Event.observe(element, 'change', this.onElementEvent.bind(this));
          break;
      }
    }
  }
}

Form.Element.EventObserver = Class.create();
Form.Element.EventObserver.prototype = Object.extend(new Abstract.EventObserver(), {
  getValue: function() {
    return Form.Element.getValue(this.element);
  }
});

Form.EventObserver = Class.create();
Form.EventObserver.prototype = Object.extend(new Abstract.EventObserver(), {
  getValue: function() {
    return Form.serialize(this.element);
  }
});
if (!window.Event) {
  var Event = new Object();
}

Object.extend(Event, {
  KEY_BACKSPACE: 8,
  KEY_TAB:       9,
  KEY_RETURN:   13,
  KEY_ESC:      27,
  KEY_LEFT:     37,
  KEY_UP:       38,
  KEY_RIGHT:    39,
  KEY_DOWN:     40,
  KEY_DELETE:   46,

  element: function(event) {
    return event.target || event.srcElement;
  },

  isLeftClick: function(event) {
    return (((event.which) && (event.which == 1)) ||
            ((event.button) && (event.button == 1)));
  },

  pointerX: function(event) {
    return event.pageX || (event.clientX +
      (document.documentElement.scrollLeft || document.body.scrollLeft));
  },

  pointerY: function(event) {
    return event.pageY || (event.clientY +
      (document.documentElement.scrollTop || document.body.scrollTop));
  },

  stop: function(event) {
    if (event.preventDefault) {
      event.preventDefault();
      event.stopPropagation();
    } else {
      event.returnValue = false;
      event.cancelBubble = true;
    }
  },

  // find the first node with the given tagName, starting from the
  // node the event was triggered on; traverses the DOM upwards
  findElement: function(event, tagName) {
    var element = Event.element(event);
    while (element.parentNode && (!element.tagName ||
        (element.tagName.toUpperCase() != tagName.toUpperCase())))
      element = element.parentNode;
    return element;
  },

  observers: false,

  _observeAndCache: function(element, name, observer, useCapture) {
    if (!this.observers) this.observers = [];
    if (element.addEventListener) {
      this.observers.push([element, name, observer, useCapture]);
      element.addEventListener(name, observer, useCapture);
    } else if (element.attachEvent) {
      this.observers.push([element, name, observer, useCapture]);
      element.attachEvent('on' + name, observer);
    }
  },

  unloadCache: function() {
    if (!Event.observers) return;
    for (var i = 0; i < Event.observers.length; i++) {
      Event.stopObserving.apply(this, Event.observers[i]);
      Event.observers[i][0] = null;
    }
    Event.observers = false;
  },

  observe: function(element, name, observer, useCapture) {
    var element = $(element);
    useCapture = useCapture || false;

    if (name == 'keypress' &&
        (navigator.appVersion.match(/Konqueror|Safari|KHTML/)
        || element.attachEvent))
      name = 'keydown';

    this._observeAndCache(element, name, observer, useCapture);
  },

  stopObserving: function(element, name, observer, useCapture) {
    var element = $(element);
    useCapture = useCapture || false;

    if (name == 'keypress' &&
        (navigator.appVersion.match(/Konqueror|Safari|KHTML/)
        || element.detachEvent))
      name = 'keydown';

    if (element.removeEventListener) {
      element.removeEventListener(name, observer, useCapture);
    } else if (element.detachEvent) {
      element.detachEvent('on' + name, observer);
    }
  }
});

/* prevent memory leaks in IE */
Event.observe(window, 'unload', Event.unloadCache, false);
var Position = {
  // set to true if needed, warning: firefox performance problems
  // NOT neeeded for page scrolling, only if draggable contained in
  // scrollable elements
  includeScrollOffsets: false,

  // must be called before calling withinIncludingScrolloffset, every time the
  // page is scrolled
  prepare: function() {
    this.deltaX =  window.pageXOffset
                || document.documentElement.scrollLeft
                || document.body.scrollLeft
                || 0;
    this.deltaY =  window.pageYOffset
                || document.documentElement.scrollTop
                || document.body.scrollTop
                || 0;
  },

  realOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.scrollTop  || 0;
      valueL += element.scrollLeft || 0;
      element = element.parentNode;
    } while (element);
    return [valueL, valueT];
  },

  cumulativeOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      element = element.offsetParent;
    } while (element);
    return [valueL, valueT];
  },

  positionedOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      element = element.offsetParent;
      if (element) {
        p = Element.getStyle(element, 'position');
        if (p == 'relative' || p == 'absolute') break;
      }
    } while (element);
    return [valueL, valueT];
  },

  offsetParent: function(element) {
    if (element.offsetParent) return element.offsetParent;
    if (element == document.body) return element;

    while ((element = element.parentNode) && element != document.body)
      if (Element.getStyle(element, 'position') != 'static')
        return element;

    return document.body;
  },

  // caches x/y coordinate pair to use with overlap
  within: function(element, x, y) {
    if (this.includeScrollOffsets)
      return this.withinIncludingScrolloffsets(element, x, y);
    this.xcomp = x;
    this.ycomp = y;
    this.offset = this.cumulativeOffset(element);

    return (y >= this.offset[1] &&
            y <  this.offset[1] + element.offsetHeight &&
            x >= this.offset[0] &&
            x <  this.offset[0] + element.offsetWidth);
  },

  withinIncludingScrolloffsets: function(element, x, y) {
    var offsetcache = this.realOffset(element);

    this.xcomp = x + offsetcache[0] - this.deltaX;
    this.ycomp = y + offsetcache[1] - this.deltaY;
    this.offset = this.cumulativeOffset(element);

    return (this.ycomp >= this.offset[1] &&
            this.ycomp <  this.offset[1] + element.offsetHeight &&
            this.xcomp >= this.offset[0] &&
            this.xcomp <  this.offset[0] + element.offsetWidth);
  },

  // within must be called directly before
  overlap: function(mode, element) {
    if (!mode) return 0;
    if (mode == 'vertical')
      return ((this.offset[1] + element.offsetHeight) - this.ycomp) /
        element.offsetHeight;
    if (mode == 'horizontal')
      return ((this.offset[0] + element.offsetWidth) - this.xcomp) /
        element.offsetWidth;
  },

  clone: function(source, target) {
    source = $(source);
    target = $(target);
    target.style.position = 'absolute';
    var offsets = this.cumulativeOffset(source);
    target.style.top    = offsets[1] + 'px';
    target.style.left   = offsets[0] + 'px';
    target.style.width  = source.offsetWidth + 'px';
    target.style.height = source.offsetHeight + 'px';
  },

  page: function(forElement) {
    var valueT = 0, valueL = 0;

    var element = forElement;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;

      // Safari fix
      if (element.offsetParent==document.body)
        if (Element.getStyle(element,'position')=='absolute') break;

    } while (element = element.offsetParent);

    element = forElement;
    do {
      valueT -= element.scrollTop  || 0;
      valueL -= element.scrollLeft || 0;
    } while (element = element.parentNode);

    return [valueL, valueT];
  },

  clone: function(source, target) {
    var options = Object.extend({
      setLeft:    true,
      setTop:     true,
      setWidth:   true,
      setHeight:  true,
      offsetTop:  0,
      offsetLeft: 0
    }, arguments[2] || {})

    // find page position of source
    source = $(source);
    var p = Position.page(source);

    // find coordinate system to use
    target = $(target);
    var delta = [0, 0];
    var parent = null;
    // delta [0,0] will do fine with position: fixed elements,
    // position:absolute needs offsetParent deltas
    if (Element.getStyle(target,'position') == 'absolute') {
      parent = Position.offsetParent(target);
      delta = Position.page(parent);
    }

    // correct by body offsets (fixes Safari)
    if (parent == document.body) {
      delta[0] -= document.body.offsetLeft;
      delta[1] -= document.body.offsetTop;
    }

    // set position
    if(options.setLeft)   target.style.left  = (p[0] - delta[0] + options.offsetLeft) + 'px';
    if(options.setTop)    target.style.top   = (p[1] - delta[1] + options.offsetTop) + 'px';
    if(options.setWidth)  target.style.width = source.offsetWidth + 'px';
    if(options.setHeight) target.style.height = source.offsetHeight + 'px';
  },

  absolutize: function(element) {
    element = $(element);
    if (element.style.position == 'absolute') return;
    Position.prepare();

    var offsets = Position.positionedOffset(element);
    var top     = offsets[1];
    var left    = offsets[0];
    var width   = element.clientWidth;
    var height  = element.clientHeight;

    element._originalLeft   = left - parseFloat(element.style.left  || 0);
    element._originalTop    = top  - parseFloat(element.style.top || 0);
    element._originalWidth  = element.style.width;
    element._originalHeight = element.style.height;

    element.style.position = 'absolute';
    element.style.top    = top + 'px';;
    element.style.left   = left + 'px';;
    element.style.width  = width + 'px';;
    element.style.height = height + 'px';;
  },

  relativize: function(element) {
    element = $(element);
    if (element.style.position == 'relative') return;
    Position.prepare();

    element.style.position = 'relative';
    var top  = parseFloat(element.style.top  || 0) - (element._originalTop || 0);
    var left = parseFloat(element.style.left || 0) - (element._originalLeft || 0);

    element.style.top    = top + 'px';
    element.style.left   = left + 'px';
    element.style.height = element._originalHeight;
    element.style.width  = element._originalWidth;
  }
}

// Safari returns margins on body which is incorrect if the child is absolutely
// positioned.  For performance reasons, redefine Position.cumulativeOffset for
// KHTML/WebKit only.
if (/Konqueror|Safari|KHTML/.test(navigator.userAgent)) {
  Position.cumulativeOffset = function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      if (element.offsetParent == document.body)
        if (Element.getStyle(element, 'position') == 'absolute') break;

      element = element.offsetParent;
    } while (element);

    return [valueL, valueT];
  }
}


// Copyright (c) 2006 Sébastien Gruhier (http://xilinus.com, http://itseb.com)
// 
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
// 
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// VERSION 0.75

var Window = Class.create();
Window.prototype = {
	// Constructor
	// Available parameters : className, title, minWidth, minHeight, maxWidth, maxHeight, width, height, top, left, bottom, right, resizable, zIndex, opacity, 
	//                        hideEffect, showEffect, showEffectOptions, hideEffectOptions, effectOptions, url, draggable, closable, minimizable, maximizable, parent
	initialize: function(id, parameters) {
		this.hasEffectLib = String.prototype.parseColor != null;
		this.minWidth = parameters.minWidth || 100;
		this.minHeight = parameters.minHeight || 20;
		this.maxWidth = parameters.maxWidth;
		this.maxHeight = parameters.maxHeight;
		this.showEffect = parameters.showEffect || (this.hasEffectLib ? Effect.Appear : Element.show);
		this.hideEffect = parameters.hideEffect || (this.hasEffectLib ? Effect.Fade : Element.hide);
		
		this.showEffectOptions = parameters.showEffectOptions || parameters.effectOptions;
		this.hideEffectOptions = parameters.hideEffectOptions || parameters.effectOptions;
		this.draggable = parameters.draggable != null ? parameters.draggable : true;
				  
		var resizable = parameters.resizable != null ? parameters.resizable : true;
		var closable = parameters.closable != null ? parameters.closable : true;
		var minimizable = parameters.minimizable != null ? parameters.minimizable : true;
		var maximizable = parameters.maximizable != null ? parameters.maximizable : true;
		var className = parameters.className != null ? parameters.className : "dialog";
		this.className = className;
		
		var parent = parameters.parent || document.getElementsByTagName("body").item(0);
		this.element = this.createWindow(id, className, parent, resizable, closable, minimizable, maximizable, parameters.title, parameters.url);
		this.isIFrame = parameters.url != null;
		
		// Bind event listener
    this.eventMouseDown = this.initDrag.bindAsEventListener(this);
  	this.eventMouseUp   = this.endDrag.bindAsEventListener(this);
  	this.eventMouseMove = this.updateDrag.bindAsEventListener(this);
  	this.eventKeyPress = this.keyPress.bindAsEventListener(this);
  	this.eventOnLoad = this._getWindowBorderSize.bindAsEventListener(this);

		this.topbar = $(this.element.id + "_top");
		this.bottombar = $(this.element.id + "_bottom");
		
		Event.observe(this.topbar, "mousedown", this.eventMouseDown);
		Event.observe(this.bottombar, "mousedown", this.eventMouseDown);
		Event.observe(window, "load", this.eventOnLoad);
		
		if (this.draggable)  {
			this.bottombar.addClassName("bottom_draggable");
			this.topbar.addClassName("top_draggable");
    }		
    
		var offset = [0,0];
		if (resizable) {
			this.sizer = $(this.element.id + "_sizer");
    	Event.observe(this.sizer, "mousedown", this.eventMouseDown);
    }	
		var width = parseFloat(parameters.width) || 200;
		var height = parseFloat(parameters.height) || 200;

		if (parameters.left != null) {
			this.element.setStyle({left: parseFloat(parameters.left) + offset[0] + 'px'});
			this.useLeft = true;
		}

		if (parameters.right != null) {
			this.element.setStyle({right: parseFloat(parameters.right) + 'px'});
			this.useLeft = false;
		}

		if (parameters.top != null) {
			this.element.setStyle({top: parseFloat(parameters.top) + 'px'});
			this.useTop = true;
		}

		if (parameters.bottom != null) {
			this.element.setStyle({bottom: parseFloat(parameters.bottom) + 'px'});			
			this.useTop = false;
		}

    this.storedLocation = null;
    
		if (parameters.opacity)
			this.setOpacity(parameters.opacity);
		if (parameters.zIndex)
			this.setZIndex(parameters.zIndex)

		this.destroyOnClose = false;

    this._getWindowBorderSize();
		this.setSize(width, height);
		Windows.register(this);	    
  },
  
	// Destructor
 	destroy: function() {
		Windows.notify("onDestroy", this);
  	Event.stopObserving(this.topbar, "mousedown", this.eventMouseDown);
  	Event.stopObserving(this.bottombar, "mousedown", this.eventMouseDown);
		Event.stopObserving(window, "load", this.eventOnLoad);

		if (this.sizer)
    		Event.stopObserving(this.sizer, "mousedown", this.eventMouseDown);

	 	if(this.iefix) 
			Element.hide(this.iefix);

		var objBody = document.getElementsByTagName("body").item(0);
		objBody.removeChild(this.element);

		Windows.unregister(this);	    
	},
  	
	// Sets window deleagte, should have functions: "canClose(window)" 
	setDelegate: function(delegate) {
		this.delegate = delegate
	},
	
	// Gets current window delegate
	getDelegate: function() {
		return this.delegate;
	},
	
	// Gets window content
	getContent: function () {
		return $(this.element.id + "_content");
	},
	
	// Sets the content with an element id
	setContent: function(id, autoresize, autoposition) {
		var d = null;
		var p = null;

		if (autoresize) 
			d = Element.getDimensions(id);
		if (autoposition) 
			p = Position.cumulativeOffset($(id));

		var content = this.getContent()
		content.appendChild($(id));
		
		if (autoresize) 
			this.setSize(d.width, d.height);
		if (autoposition) 
		  this.setLocation(p[1] - this.heightN, p[0] - this.widthW);	  
	},
	
	// Stores position/size in a cookie, by default named with window id
	setCookie: function(name, expires, path, domain, secure) {
		name = name || this.element.id;
		this.cookie = [name, expires, path, domain, secure];
		
		// Get cookie
		var value = WindowUtilities.getCookie(name)
		// If exists
		if (value) {
			var values = value.split(',');
			var x = values[0].split(':');
			var y = values[1].split(':');

			this.setSize(parseFloat(values[2]), parseFloat(values[3]));

			this.useLeft = x[0] == "l";
			this.useTop = y[0] == "t";

			this.element.setStyle(this.useLeft ? {left: x[1]} : {right: x[1]});
			this.element.setStyle(this.useTop ? {top: y[1]} : {bottom: y[1]});
		}
	},
	
	// Gets window ID
	getId: function() {
		return this.element.id;
	},
	
	// Detroys itself when closing 
	setDestroyOnClose: function() {
		this.destroyOnClose = true;
	},
	
	// initDrag event
	initDrag: function(event) {
		// Get pointer X,Y
  	this.pointer = [Event.pointerX(event), Event.pointerY(event)];
		this.doResize = false;
		
		// Check if click on close button, 
		var closeButton = $(this.getId() + '_close');
		if (closeButton && Position.within(closeButton, this.pointer[0], this.pointer[1])) {
			return;
		}
		this.toFront();

		// Check if click on sizer
		if (this.sizer && Position.within(this.sizer, this.pointer[0], this.pointer[1])) {
			this.doResize = true;
    	this.widthOrg = this.width;
    	this.heightOrg = this.height;
    	this.bottomOrg = parseFloat(this.element.getStyle('bottom'));
    	this.rightOrg = parseFloat(this.element.getStyle('right'));
			Windows.notify("onStartResize", this);
		}
		else {
  		if (! this.draggable) 
  		  return;
			Windows.notify("onStartMove", this);
		}
  	
		// Register global event to capture mouseUp and mouseMove
		Event.observe(document, "mouseup", this.eventMouseUp);
  	Event.observe(document, "mousemove", this.eventMouseMove);
		
		// Add an invisible div to keep catching mouse event over the iframe
		if (this.isIFrame) {
			var objBody = document.getElementsByTagName("body").item(0);
			var div = document.createElement("div");
			div.style.position = "absolute";
			div.style.top = this.heightN + "px";
			div.style.left = this.widthW + "px";
			div.style.zIndex = Windows.maxZIndex;
			div.style.height = this.height + "px";
			div.style.width = this.width + "px";
			this.element.appendChild(div);
			this.tmpDiv = div;			
		}
  	Event.stop(event);
  },

  // updateDrag event
	updateDrag: function(event) {
   	var pointer = [Event.pointerX(event), Event.pointerY(event)];    
		var dx = pointer[0] - this.pointer[0];
		var dy = pointer[1] - this.pointer[1];
		
		// Resize case, update width/height
		if (this.doResize) {
			this.setSize(this.widthOrg + dx , this.heightOrg + dy);
			
      dx = this.width - this.widthOrg
      dy = this.height - this.heightOrg
			
		  // Check if it's a right position, update it to keep upper-left corner at the same position
			if (! this.useLeft) 
				this.element.setStyle({right: (this.rightOrg -dx) + 'px'});
			// Check if it's a bottom position, update it to keep upper-left corner at the same position
			if (! this.useTop) 
				this.element.setStyle({bottom: (this.bottomOrg -dy) + 'px'});
				
			if (this.tmpDiv)
			  this.tmpDiv.setStyle({width: this.width + "px", height: this.height + "px"})
		}
		// Move case, update top/left
		else {
		  this.pointer = pointer;
  		
			if (this.useLeft) 
				this.element.setStyle({left: parseFloat(this.element.getStyle('left')) + dx + 'px'});
			else 
				this.element.setStyle({right: parseFloat(this.element.getStyle('right')) - dx + 'px'});
			
			if (this.useTop) 
				this.element.setStyle({top: parseFloat(this.element.getStyle('top')) + dy + 'px'});
		  else 
				this.element.setStyle({bottom: parseFloat(this.element.getStyle('bottom')) - dy + 'px'});
		}
		if (this.iefix) 
			this._fixIEOverlapping(); 
			
		this._removeStoreLocation();
    Event.stop(event);
	},

	 // endDrag callback
 	endDrag: function(event) {
		if (this.doResize)
			Windows.notify("onEndResize", this);
		else
			Windows.notify("onEndMove", this);
		
		// Release event observing
		Event.stopObserving(document, "mouseup", this.eventMouseUp);
    Event.stopObserving(document, "mousemove", this.eventMouseMove);

		// Remove temporary div
		if (this.isIFrame) {
			this.tmpDiv.parentNode.removeChild(this.tmpDiv);
			this.tmpDiv = null;
		}
		// Store new location/size
		if (this.cookie) {
			var value = "";
			if (this.useLeft)
				value += "l:" + this.element.getStyle('left')
			else
				value += "r:" + this.element.getStyle('right')
			if (this.useTop)
				value += ",t:" + this.element.getStyle('top')
			else
				value += ",b:" + this.element.getStyle('bottom')
			value += "," + this.width;
			value += "," + this.height;
			WindowUtilities.setCookie(value, this.cookie)
		}
    Event.stop(event);
  },

	keyPress: function(event) {
		//Dialog.cancelCallback();
	},
	
	// Creates HTML window code
	createWindow: function(id, className, parent, resizable, closable, minimizable, maximizable, title, url) {
		win = document.createElement("div");
		win.setAttribute('id', id);
		win.className = "dialog";
	 	if (!title)
			title = "&nbsp;";

		var content;
		if (url)
			content= "<IFRAME name=\"" + id + "_content\"  id=\"" + id + "_content\" SRC=\"" + url + "\" > </IFRAME>";
		else
			content ="<DIV id=\"" + id + "_content\" class=\"" +className + "_content\"> </DIV>";

		win.innerHTML = "\
		<div class='"+ className +"_close' id='"+ id +"_close' onclick='Windows.close(\""+ id +"\");closeAll()'> </div>\
		<div class='"+ className +"_minimize' id='"+ id +"_minimize' onclick='Windows.minimize(\""+ id +"\")'> </div>\
		<div class='"+ className +"_maximize' id='"+ id +"_maximize' onclick='Windows.maximize(\""+ id +"\")'> </div>\
		<table id='"+ id +"_header' class='"+ className +"_header'>\
			<tr id='"+ id +"_row1'>\
				<td>\
					<table>\
						<tr>\
							<td id='"+ id +"_nw' class='"+ className +"_nw'><div class='"+ className +"_nw'> </div></td>\
							<td class='"+ className +"_n'  valign='middle'><div id='"+ id +"_top' class='"+ className +"_title'>"+ title +"</div></td>\
							<td class='"+ className +"_ne'> <div class='"+ className +"_ne'></div></td>\
						</tr>\
					</table>\
				</td>\
			</tr>\
			<tr id='"+ id +"_row2'>\
				<td>\
					<table >\
						<tr>\
							<td class='"+ className +"_w'><div class='"+ className +"_w'> </div></td>\
							<td class='"+ className +"_content'>"+ content +"</td>\
							<td class='"+ className +"_e'><div class='"+ className +"_e'> </div></td>\
						</tr>\
					</table>\
				</td>\
			</tr>\
			<tr id='"+ id +"_row3'>\
				<td>\
					<table>\
						<tr>\
							<td class='"+ className +"_sw' id='"+ id +"_sw'><div class='"+ className +"_sw'></div> </td>\
							<td class='"+ className +"_s'><div  id='"+ id +"_bottom' class='"+ className +"_s'> </div></td>\
							<td class='"+ className +"_se'>"+ (resizable  ? "<div id='"+ id + "_sizer' class='"+ className +"_sizer'></div>" : "<div class='"+ className +"_se'></div>") +"</td>\
						</tr>\
					</table>\
				</td>\
			</tr>\
		</table>\
		";
		Element.hide(win);
		parent.insertBefore(win, parent.firstChild);
		
		if (!closable)
		  Element.hide(id +"_close")
  	if (!minimizable)
  	  Element.hide(id +"_minimize")
  	if (!maximizable)
  	  Element.hide(id +"_maximize")

		return win;
	},
	
	// Sets window location
	setLocation: function(top, left) {
	  if (top < 0)
	    top = 0;
    if (left < 0)
      left= 0
		this.element.setStyle({top: top + 'px'});
		this.element.setStyle({left: left + 'px'});
		this.useLeft = true;
		this.useTop = true;
	},
		
	// Sets window size
	setSize: function(width, height) {    
		// Check min and max size
		if (width < this.minWidth)
			width = this.minWidth;

		if (height < this.minHeight)
			height = this.minHeight;
			
		if (this.maxHeight && height > this.maxHeight)
			height = this.maxHeight;

		if (this.maxWidth && width > this.maxWidth)
			width = this.maxWidth;

  	this.width = width;
		this.height = height;
		this.element.setStyle({width: width + this.widthW + this.widthE + "px"})
		this.element.setStyle({height: height + this.heightN + this.heightS + "px"})

		// Update content height
		var content = $(this.element.id + '_content')
		content.setStyle({height: height  + 'px'});
		content.setStyle({width: width  + 'px'});
	},
	
	// Brings window to front
	toFront: function() {
    this.setZIndex(Windows.maxZIndex + 20);
	},
	
	// Displays window modal state or not
	show: function(modal) {
		if (modal) {
			WindowUtilities.disableScreen(this.className);
			this.modal = true;			
			this.setZIndex(Windows.maxZIndex + 20);
			Windows.unsetOverflow(this);
			Event.observe(document, "keypress", this.eventKeyPress);	      	
		}
			
		this.setSize(this.width, this.height);
		if (this.showEffect != Element.show && this.showEffectOptions )
			this.showEffect(this.element, this.showEffectOptions);	
		else
			this.showEffect(this.element);	
			
  this._checkIEOverlapping();
	},
	
	// Displays window modal state or not at the center of the page
	showCenter: function(modal) {
		this.setSize(this.width, this.height);
		this.center();
		
		this.show(modal);
	},
	
	center: function() {
		var windowScroll = WindowUtilities.getWindowScroll();    
		var pageSize = WindowUtilities.getPageSize();    

    this.setLocation(windowScroll.top + (pageSize.windowHeight - (this.height + this.heightN + this.heightS))/2, 
                     windowScroll.left + (pageSize.windowWidth - (this.width + this.widthW + this.widthE))/2);
    this.toFront();
	},
	
	// Hides window
	hide: function() {
		if (this.modal) {
			WindowUtilities.enableScreen();
			Windows.resetOverflow();
			Event.stopObserving(document, "keypress", this.eventKeyPress);
			
		}
		// To avoid bug on scrolling bar
		this.getContent().setStyle({overflow: "hidden"});
	
		if (this.hideEffectOptions)
			this.hideEffect(this.element, this.hideEffectOptions);	
		else
			this.hideEffect(this.element);	
	 	if(this.iefix) 
			this.iefix.hide();
	},

  minimize: function() {
    var r2 = $(this.getId() + "_row2");
    var r3 = $(this.getId() + "_row3");
    if (r2.visible()) {
      r2.hide();
      r3.hide();
    } else {
      r2.show();
      r3.show();
    }
  },
  
  maximize: function() {
    if (this.storedLocation != null) {
      this._restoreLocation();
      if(this.iefix) 
  			this.iefix.hide();
    }
    else {
      this._storeLocation();
      Windows.unsetOverflow(this);
      
      var windowScroll = WindowUtilities.getWindowScroll();
  		var pageSize = WindowUtilities.getPageSize();    

			this.element.setStyle(this.useLeft ? {left: windowScroll.left} : {right: windowScroll.left});
  		this.element.setStyle(this.useTop ? {top: windowScroll.top} : {bottom: windowScroll.top});
      
      this.setSize(pageSize.windowWidth - this.widthW - this.widthE, pageSize.windowHeight - this.heightN - this.heightS)
      this.toFront();
      if (this.iefix) 
  			this._fixIEOverlapping(); 
    }
  },
  
	setOpacity: function(opacity) {
		if (Element.setOpacity)
			Element.setOpacity(this.element, opacity);
	},
	
	setZIndex: function(zindex) {
		this.element.setStyle({zIndex: zindex});
		Windows.updateZindex(zindex, this);
	},

  setTitle: function(newTitle) {
  	if (!newTitle) 
  	  newTitle = "&nbsp;";
  	Element.update(this.element.id + '_top', newTitle);
  },

	_checkIEOverlapping: function() {
    if(!this.iefix && (navigator.appVersion.indexOf('MSIE')>0) && (navigator.userAgent.indexOf('Opera')<0) && (this.element.getStyle('position')=='absolute')) {
        new Insertion.After(this.element.id, '<iframe id="' + this.element.id + '_iefix" '+ 'style="display:none;position:absolute;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);" ' + 'src="javascript:false;" frameborder="2" scrolling="no"></iframe>');
        this.iefix = $(this.element.id+'_iefix');
    }
    if(this.iefix) 
			setTimeout(this._fixIEOverlapping.bind(this), 50);
	},

	_fixIEOverlapping: function() {
	    Position.clone(this.element, this.iefix);
	    this.iefix.style.zIndex = this.element.style.zIndex - 1;
	    this.iefix.show();
	},
	
	_getWindowBorderSize: function(event) {
    // Hack to get real window border size!!
    var div = this._createHiddenDiv(this.className + "_n")
		this.heightN = Element.getDimensions(div).height;		
		div.parentNode.removeChild(div)

    var div = this._createHiddenDiv(this.className + "_s")
		this.heightS = Element.getDimensions(div).height;		
		div.parentNode.removeChild(div)

    var div = this._createHiddenDiv(this.className + "_e")
		this.widthE = Element.getDimensions(div).width;		
		div.parentNode.removeChild(div)

    var div = this._createHiddenDiv(this.className + "_w")
		this.widthW = Element.getDimensions(div).width;
		div.parentNode.removeChild(div);
  },
 
  _createHiddenDiv: function(className) {
    var objBody = document.getElementsByTagName("body").item(0);
    var win = document.createElement("div");
		win.setAttribute('id', this.element.id+ "_tmp");
		win.className = className;
		win.style.display = 'none'
		win.innerHTML = ''
		objBody.insertBefore(win, objBody.firstChild)   
		return win
  },
  
	_storeLocation: function() {
	  if (this.storedLocation == null) {
	    this.storedLocation = {useTop: this.useTop, useLeft: this.useLeft, 
	                           top: this.element.getStyle('top'), bottom: this.element.getStyle('bottom'),
	                           left: this.element.getStyle('left'), right: this.element.getStyle('right'),
	                           width: this.width, height: this.height };
	  }
	},
	
  _restoreLocation: function() {
    if (this.storedLocation != null) {
      this.useLeft = this.storedLocation.useLeft;
      this.useTop = this.storedLocation.useTop;
      
      this.element.setStyle(this.useLeft ? {left: this.storedLocation.left} : {right: this.storedLocation.right});
  		this.element.setStyle(this.useTop ? {top: this.storedLocation.top} : {bottom: this.storedLocation.bottom});
		  this.setSize(this.storedLocation.width, this.storedLocation.height);
      
		  Windows.resetOverflow();
		  this._removeStoreLocation();
    }
  },
  
  _removeStoreLocation: function() {
    this.storedLocation = null;
  }
};

// Windows containers, register all page windows
var Windows = {
  windows: [],
  observers: [],
  focusedWindow: null,
  maxZIndex: 0,

  addObserver: function(observer) {
    this.observers.push(observer);
  },
  
  removeObserver: function(observer) {  
    this.observers = this.observers.reject( function(o) { return o==observer });
  },
  
  notify: function(eventName, win) {  //  onStartResize(), onEndResize(), onStartMove(), onEndMove(), onClose(), onDestroy()
    this.observers.each( function(o) {if(o[eventName]) o[eventName](eventName, win);});
  },

  // Gets window from its id
  getWindow: function(id) {
	  return this.windows.detect(function(d) { return d.getId() ==id });
  },

  // Registers a new window (called by Windows constructor)
  register: function(win) {
    this.windows.push(win);
  },
  
  // Unregisters a window (called by Windows destructor)
  unregister: function(win) {
    this.windows = this.windows.reject(function(d) { return d==win });
  }, 

  // Closes a window with its id
  close: function(id) {
  	win = this.getWindow(id);
  	// Asks delegate if exists
    if (win) {
	  	if (win.getDelegate() && ! win.getDelegate().canClose(win)) 
	  		return;
	
  			this.notify("onClose", win);
  			win.hide();
  			if (win.destroyOnClose)  
    			win.destroy();
  	}
  },
  
  // Closes all windows
  closeAll: function() {  
    this.windows.each( function(w) {Windows.close(w.getId())} );
  },
  
  // Minimizes a window with its id
  minimize: function(id) {
  	win = this.getWindow(id);
  	win.minimize();
  },
  
  // Maximizes a window with its id
  maximize: function(id) {
  	win = this.getWindow(id);
  	win.maximize();
  },
  
  unsetOverflow: function(except) {		
  	this.windows.each(function(d) { d.oldOverflow = d.getContent().getStyle("overflow") || "auto" ; d.getContent().setStyle({overflow: "hidden"}) });
  	if (except && except.oldOverflow)
  		except.getContent().setStyle({overflow: except.oldOverflow});
  },

  resetOverflow: function() {
	  this.windows.each(function(d) { if (d.oldOverflow) d.getContent().setStyle({overflow: d.oldOverflow}) });
  },

  updateZindex: function(zindex, win) {
  	if (zindex > this.maxZIndex)
  		this.maxZIndex = zindex;
    this.focusedWindow = win;
  }
};

var Dialog = {
 	win: null,

	confirm: function(message, parameters) {
		var okLabel = parameters && parameters.okLabel ? parameters.okLabel : "Ok";
		var cancelLabel = parameters && parameters.cancelLabel ? parameters.cancelLabel : "Cancel";
		var windowParam = parameters  ? parameters.windowParameters : {};
		windowParam.className = windowParam.className || "alert";

		var content = "\
			<div class='" + windowParam.className + "_message'>" + message  + "</div>\
				<div class='" + windowParam.className + "_buttons'>\
					<input type='button' value='" + okLabel + "' onclick='Dialog.okCallback()'/>\
					<input type='button' value='" + cancelLabel + "' onclick='Dialog.cancelCallback()'/>\
				</div>\
		";
		return this.openDialog(content, parameters)
	},
	
	alert: function(message, parameters) {
		var okLabel = parameters && parameters.okLabel ? parameters.okLabel : "Ok";

		var windowParam = parameters ? parameters.windowParameters : {};
		windowParam.className = windowParam.className || "alert";

		var content = "\
			<div class='" + windowParam.className + "_message'>" + message  + "</div>\
				<div class='" + windowParam.className + "_buttons'>\
					<input type='button' value='" + okLabel + "' onclick='Dialog.okCallback()'/>\
				</div>";
		return this.openDialog(content, parameters)
	},
	
	info: function(message, parameters) {    
	  parameters = parameters || {};
	  parameters.windowParameters = parameters.windowParameters || {};
	  
		var className = parameters.windowParameters.className || "alert";

		var content = "<div id='modal_dialog_message' class='" + className + "_message'>" + message  + "</div>";
		if (parameters && parameters.showProgress)
		  content += "<div id='modal_dialog_progress' class='" + className + "_progress'>	</div>";

		parameters.windowParameters.ok = null;
		parameters.windowParameters.cancel = null;
    parameters.windowParameters.className = className;
		
		return this.openDialog(content, parameters)
	},
	
	setInfoMessage: function(message) {
		$('modal_dialog_message').update(message);
	},
	
	closeInfo: function() {
		Windows.close('modal_dialog');
	},
	
	openDialog: function(content, parameters) {
		// remove old dialog
		if (this.win) 
			this.win.destroy();

		var windowParam = parameters && parameters.windowParameters ? parameters.windowParameters : {};
		windowParam.resizable = windowParam.resizable || false;
		
		windowParam.effectOptions = windowParam.effectOptions || {duration: 1};

		this.win = new Window('modal_dialog', windowParam);
		this.win.getContent().innerHTML = content;
		this.win.showCenter(true);	
		
		this.win.cancelCallback = parameters.cancel;
		this.win.okCallback = parameters.ok;
		
		if (! this.eventResize)
		  this.eventResize = this.recenter.bindAsEventListener(this);
		  
  	Event.observe(window, "resize", this.eventResize);
  	Event.observe(window, "scroll", this.eventResize);

		return this.win;		
	},
	
	okCallback: function() {
		this.win.hide();
		Event.stopObserving(window, "resize", this.eventResize);
		Event.stopObserving(window, "scroll", this.eventResize);
      	
		if (this.win.okCallback)
			this.win.okCallback(this.win);
	},

	cancelCallback: function() {
		this.win.hide();
		Event.stopObserving(window, "resize", this.eventResize);
		Event.stopObserving(window, "scroll", this.eventResize);
		
		if (this.win.cancelCallback)
			this.win.cancelCallback(this.win);
	},

	recenter: function(event) {
		var pageSize = WindowUtilities.getPageSize();
		// set height of Overlay to take up whole page and show
		if ($('overlay_modal'))
		  $('overlay_modal').style.height = (pageSize.pageHeight + 'px');
		
		this.win.center();
	}
}
/*
	Based on Lightbox JS: Fullsize Image Overlays 
	by Lokesh Dhakar - http://www.huddletogether.com

	For more information on this script, visit:
	http://huddletogether.com/projects/lightbox/

	Licensed under the Creative Commons Attribution 2.5 License - http://creativecommons.org/licenses/by/2.5/
	(basically, do anything you want, just leave my name and link)
*/

var isIE = navigator.appVersion.match(/MSIE/) == "MSIE";

var WindowUtilities = {
  // From script.aculo.us
  getWindowScroll: function() {
    var w = window;
      var T, L, W, H;
      with (w.document) {
        if (w.document.documentElement && documentElement.scrollTop) {
          T = documentElement.scrollTop;
          L = documentElement.scrollLeft;
        } else if (w.document.body) {
          T = body.scrollTop;
          L = body.scrollLeft;
        }
        if (w.innerWidth) {
          W = w.innerWidth;
          H = w.innerHeight;
        } else if (w.document.documentElement && documentElement.clientWidth) {
          W = documentElement.clientWidth;
          H = documentElement.clientHeight;
        } else {
          W = body.offsetWidth;
          H = body.offsetHeight
        }
      }
      return { top: T, left: L, width: W, height: H };
    
  }, 
  //
  // getPageSize()
  // Returns array with page width, height and window width, height
  // Core code from - quirksmode.org
  // Edit for Firefox by pHaez
  //
  getPageSize: function(){
  	var xScroll, yScroll;

  	if (window.innerHeight && window.scrollMaxY) {	
  		xScroll = document.body.scrollWidth;
  		yScroll = window.innerHeight + window.scrollMaxY;
  	} else if (document.body.scrollHeight > document.body.offsetHeight){ // all but Explorer Mac
  		xScroll = document.body.scrollWidth;
  		yScroll = document.body.scrollHeight;
  	} else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
  		xScroll = document.body.offsetWidth;
  		yScroll = document.body.offsetHeight;
  	}

  	var windowWidth, windowHeight;

  	if (self.innerHeight) {	// all except Explorer
  		windowWidth = self.innerWidth;
  		windowHeight = self.innerHeight;
  	} else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
  		windowWidth = document.documentElement.clientWidth;
  		windowHeight = document.documentElement.clientHeight;
  	} else if (document.body) { // other Explorers
  		windowWidth = document.body.clientWidth;
  		windowHeight = document.body.clientHeight;
  	}	
  	var pageHeight, pageWidth;

  	// for small pages with total height less then height of the viewport
  	if(yScroll < windowHeight){
  		pageHeight = windowHeight;
  	} else { 
  		pageHeight = yScroll;
  	}

  	// for small pages with total width less then width of the viewport
  	if(xScroll < windowWidth){	
  		pageWidth = windowWidth;
  	} else {
  		pageWidth = xScroll;
  	}

  	return {pageWidth: pageWidth ,pageHeight: pageHeight , windowWidth: windowWidth, windowHeight: windowHeight};
  },

 	disableScreen: function(className) {
		WindowUtilities.initLightbox(className);
		var objBody = document.getElementsByTagName("body").item(0);

		// prep objects
	 	var objOverlay = $('overlay_modal');

		var pageSize = WindowUtilities.getPageSize();

		// Hide select boxes as they will 'peek' through the image in IE
		if (isIE) {
			selects = document.getElementsByTagName("select");
		    for (var i = 0; i != selects.length; i++) {
		    	selects[i].style.visibility = "hidden";
		    }
		}	
	
		// set height of Overlay to take up whole page and show
		objOverlay.style.height = (pageSize.pageHeight + 'px');
		objOverlay.style.display = 'block';	
	},

 	enableScreen: function() {
	 	var objOverlay = $('overlay_modal');
		if (objOverlay) {
			// hide lightbox and overlay
			objOverlay.style.display = 'none';

			// make select boxes visible
			if (isIE) {
				selects = document.getElementsByTagName("select");
			    for (var i = 0; i != selects.length; i++) {
					selects[i].style.visibility = "visible";
				}
			}
			objOverlay.parentNode.removeChild(objOverlay);
		}
	},

	// initLightbox()
	// Function runs on window load, going through link tags looking for rel="lightbox".
	// These links receive onclick events that enable the lightbox display for their targets.
	// The function also inserts html markup at the top of the page which will be used as a
	// container for the overlay pattern and the inline image.
	initLightbox: function(className) {
		// Already done, just update zIndex
		if ($('overlay_modal')) {
			Element.setStyle('overlay_modal', {zIndex: Windows.maxZIndex + 10});
		}
		// create overlay div and hardcode some functional styles (aesthetic styles are in CSS file)
		else {
			var objBody = document.getElementsByTagName("body").item(0);
			var objOverlay = document.createElement("div");
			objOverlay.setAttribute('id', 'overlay_modal');
			objOverlay.className = "overlay_" + className
			objOverlay.style.display = 'none';
			objOverlay.style.position = 'absolute';
			objOverlay.style.top = '0';
			objOverlay.style.left = '0';
			objOverlay.style.zIndex = Windows.maxZIndex + 10;
		 	objOverlay.style.width = '100%';

			objBody.insertBefore(objOverlay, objBody.firstChild);
		}
	},
	
	setCookie: function(value, parameters) {
    document.cookie= parameters[0] + "=" + escape(value) +
      ((parameters[1]) ? "; expires=" + parameters[1].toGMTString() : "") +
      ((parameters[2]) ? "; path=" + parameters[2] : "") +
      ((parameters[3]) ? "; domain=" + parameters[3] : "") +
      ((parameters[4]) ? "; secure" : "");
  },

  getCookie: function(name) {
    var dc = document.cookie;
    var prefix = name + "=";
    var begin = dc.indexOf("; " + prefix);
    if (begin == -1) {
      begin = dc.indexOf(prefix);
      if (begin != 0) return null;
    } else {
      begin += 2;
    }
    var end = document.cookie.indexOf(";", begin);
    if (end == -1) {
      end = dc.length;
    }
    return unescape(dc.substring(begin + prefix.length, end));
  }
}


