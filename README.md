#  BigDecimal for Javascript

*BigDecimal for Javascript* is a pure-Javascript implementation of immutable, arbitrary-precision, signed decimal numbers. BigDecimal supports **decimal** math with arbitrary precision.

For a limited time, we will throw in **BigInteger** support at no extra charge!

## Purpose

If this is a problem for you:

    node> 0.1 + 0.2
    0.30000000000000004

Then you need BigDecimal for Javascript. BigDecimal is great for arithmetic of *financial* information, or anything exceeding the Javascript `Number` (IEEE-754 float) type. Decimal did not make the cut in the new ECMAScript standards so it&rsquo;s time we got our act together.

## Usage

BigDecimal works in the browser and in Apache CouchDB. See the simple [BigDecimal Couch app demo][demo] to compare server-side and client-side expression evaluation.

BigDecimal is available in the NPM repository for [NodeJS][node].

    npm install bigdecimal

Now use it normally. It is exactly like the Java 1.5 `BigInteger` and `BigDecimal` API. See the [BigDecimal documentation][java_bd] for information.

```javascript
var bigdecimal = require("bigdecimal");

var i = new bigdecimal.BigInteger("1234567890abcdefghijklmn", 24);
console.log("i is " + i);
// Output: i is 60509751690538858612029415201127

var d = new bigdecimal.BigDecimal(i);
var x = new bigdecimal.BigDecimal("123456.123456789012345678901234567890");
console.log("d * x = " + d.multiply(x));
// Output: d * x = 7470299375046812977089832214047022056.555930270554343863089286012030

var two = new bigdecimal.BigDecimal('2');
console.log("Average = " + d.add(x).divide(two));
// Output: Average = 30254875845269429306014707662291.561728394506172839450617283945

var down = bigdecimal.RoundingMode.DOWN();
console.log("d / x (25 decimal places) = " + d.divide(x, 25, DOWN));
// Output: d / x (25 decimal places) = 490131635404200348624039911.8662623025579331926181155
```

There is also a command-line REPL tool for experimentation, `bigdecimal.js` if you install with the `-g` switch, or `./node_modules/.bin/bigdecimal.js` otherwise. The `bigdecimal` module is already loaded, also available as `BD` for convenience.

    bigdecimal.js
    BigDec> d = new BD.BigDecimal("123456.123456789012345678901234567890")

## Implementation

This code is compiled Javascript, originating from the Google [GWT][gwt] project. GWT version 2.1 supports the Java BigDecimal class. The implementation came from the Apache Harmony project by way of [gwt-java-math][gwt-java-math] which optimized it for the Javascript compiler.

Compiled Javascript is a problem; however that is offset by these benefits:

* The implementation is mature, optimized, and maintained by Apache and Google
* The API is well-known, compatible with the J2SE `BigDecimal` and `BigInteger` class

If you can&rsquo;t stand the idea of running machine-generated code, please implement `BigInteger` and `BigDecimal` in native Javascript; convince the world your implementation is trustworthy, reasonably bug-free, and sure to be maintained for several years and I will glady include it in this project.

## Builds

Ready-to-use Javascript builds are available in the tagged Git revisions. Click the `Download Source` button at the top and choose a `vX.Y` tag. The built code will be in `lib/bigdecimal.js`.

To build BigDecimal yourself from source, follow these steps:

1. Download the [GWT SDK][sdk], version 2.4.0. Extract it in this checkout, as `gwt-2.4.0/`
1. Get [Apache Ant][ant] if you don't have it (try just running `ant` at the command line). I just unpack it in this checkout and add `$PWD/apche-ant-1.8.2/bin` to my `PATH`.
1. `rake clean`
1. `rake`

As with the prebuilt download, the CommonJS module will be in `lib/bigdecimal.js`.

## License

BigDecimal for Javascript is licensed under the Apache License, version 2.0.

[gwt]: http://code.google.com/webtoolkit/
[sdk]: http://code.google.com/webtoolkit/download.html
[ant]: http://ant.apache.org/index.html
[demo]: http://jhs.iriscouch.com/demo/_design/bigdecimal/_show/ui?e=321.4567890*123456789.123456789987654321
[commonjs]: http://commonjs.org/
[gwt-java-math]: http://code.google.com/p/gwt-java-math/
[couchdb]: http://couchdb.apache.org/
[node]: http://nodejs.org/
[java_bd]: http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html
