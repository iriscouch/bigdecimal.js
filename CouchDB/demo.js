var fs = require('fs')
  , path = require('path')
  , couchapp = require('couchapp')
  ;

var ddoc = module.exports = {};

ddoc._id = '_design/bigdecimal';
ddoc.shows = {};

ddoc.shows.ui = function(doc, req) {
  var ddoc = this
    , BD = require("bigdecimal")
    ;

  log(req.query);

  var a, b, op, result;
  var response = {headers: {'content-type': 'text/html'}};

  var match = req.query.e && req.query.e.match(/(-?\d+\.?\d*) *([\+\-\*\/]) *(-?\d+\.?\d*)/);
  if(!match)
    response.body = "Usage: ('e' parameter) must be: A &lt;operand&gt; B" +
                    "<br><br>" +
                    "where A, B are real numbers (initial 0 required), and operand is *, /, -, or +." +
                    "<br><br>" +
                    "(Also, division tends to throw exceptions because I hard-coded unlimited precision for this demo.)";
  else {
    a = new bd.BigDecimal(match[1]);
    b = new bd.BigDecimal(match[3]);
    op = {'*': 'multiply', '/': 'divide', '+': 'add', '-': 'subtract'}[match[2]];
    result = (op == 'divide') ? a[op].apply(a, [b, 300, bd.RoundingMode.HALF_UP()]) : a[op].apply(a, [b]);
    response.body = a.toString() + ' ' + match[2] + ' ' + b.toString() + ' = ' + result.toString() +
                    (op == 'divide' ? "<br>(Division precision set to 300)<br>" : "") +
                    "<br><br><a href='http://github.com/jhs/bigdecimal.js'>bigdecimal.js - BigDecimal and BigInteger for Javascript</a>";
  }

  response.body += '\n';
  return response;
};

ddoc.bigdecimal = fs.readFileSync(path.join(__dirname, '..', 'lib', 'bigdecimal.js'), 'utf8');

if(require.main === module)
  console.dir(ddoc);
