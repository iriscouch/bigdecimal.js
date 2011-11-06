var fs = require('fs')
  , path = require('path')
  , couchapp = require('couchapp')
  ;

var ddoc = module.exports = {};

ddoc._id = '_design/bigdecimal';

ddoc.validate_doc_update = function(newDoc, oldDoc, userCtx, secObj) {
  throw {forbidden:"No changes allowed in the BigDecimal demo"};
}

ddoc.shows = {};
ddoc.shows.ui = function(doc, req) {
  var ddoc = this
    , bd = require("bigdecimal")
    ;

  log(req.query);

  var result;
  var response = {'headers': {}};

  provides('html', function() {
    response.headers['content-type'] = 'text/html;charset=utf-8';

    var match = req.query.e && req.query.e.match(/(-?\d+\.?\d*) *([\+\-\*\/]) *(-?\d+\.?\d*)/);
    if(!match)
      response.body = "Usage: ('e' parameter) must be: A &lt;operand&gt; B" +
                      "<br><br>" +
                      "where A, B are real numbers (initial 0 required), and operand is *, /, -, or +." +
                      "<br><br>" +
                      "(Also, division tends to throw exceptions because I hard-coded unlimited precision for this demo.)";
    else {
      result = run({'a':match[1], 'b':match[3], 'op':match[2]});
      response.body = req.query.e + '<br><br>';
      response.body += '= ' + result;

      if(match[2] == '/')
        response.body += ' (Precision set to 300)';

      response.body += "<br><br><a href='https://github.com/iriscouch/bigdecimal.js'>bigdecimal.js - BigDecimal and BigInteger for Javascript</a>";
    }

    response.body += '\n';
    return response;
  })

  function run(opts) {
    var result;
    var ops = {'*': 'multiply', '/': 'divide', '+': 'add', '-': 'subtract'};

    var a = new bd.BigDecimal("" + opts.a);
    var b = new bd.BigDecimal("" + opts.b);
    var op = ops[ opts.op ];

    if(op == 'divide')
      return a.divide(b, 300, bd.RoundingMode.HALF_UP());
    else
      return a[op].call(a, b);
  }
};

ddoc.bigdecimal = fs.readFileSync(path.join(__dirname, '..', 'lib', 'bigdecimal.js'), 'utf8');

if(require.main === module)
  console.log('ok');
