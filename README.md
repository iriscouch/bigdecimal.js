#  BigDecimal for Javascript

*BigDecimal for Javascript* is a pure-Javascript implementation of immutable,
arbitrary-precision, signed decimal numbers. BigDecimal supports **decimal**
math with arbitrary precision.

For a limited time, we will throw in **BigInteger** support at no extra charge!

## Purpose

If this is a problem for you:

    node> 0.1 + 0.2
    0.30000000000000004

Then you need BigDecimal for Javascript. BigDecimal is great for arithmetic of
*financial* information, or anything exceeding the Javascript `Number` (IEEE-754
float) type. Decimal did not make the cut in the new ECMAScript standards so
it&rsquo;s time we got our act together.

## License

BigDecimal for Javascript is licensed under the Apache License, version 2.0.

## Format

This software is a [CommonJS][commonjs] module. It is immediately useful in
[NodeJS][node] and [CouchDB][couchdb] projects.

If you need an additional format (e.g. browser or NPM), let me know and
hopefully I can add it to the release.

## Implementation

This code is compiled Javascript, originating from the Google [GWT][gwt]
project. GWT version 2.1 supports the Java BigDecimal class. The implementation
came from the Apache Harmony project by way of [gwt-java-math][gwt-java-math]
which optimized it for the Javascript compiler.

Compiled Javascript is a problem; however that is offset by these benefits:

* The implementation is mature, optimized, and maintained by Apache and Google
* The API is well-known, compatible with the J2SE `BigDecimal` and `BigInteger`
  class

If you can&rsquo;t stand the idea of running machine-generated code, please
implement `BigInteger` and `BigDecimal` in native Javascript; convince the world
your implementation is trustworthy, reasonably bug-free, and sure to be
maintained for several years and I will glady include it in this project.

  [gwt]: http://code.google.com/webtoolkit/
  [commonjs]: http://commonjs.org/
  [gwt-java-math]: http://code.google.com/p/gwt-java-math/
  [couchdb]: http://couchdb.apache.org/
  [node]: http://nodejs.org/

vim: tw=80
