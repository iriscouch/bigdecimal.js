package com.iriscouch.gwtapp.client;

import com.google.gwt.core.client.JavaScriptObject;

class JsArgs extends JavaScriptObject {
  protected JsArgs() {}

  public static final native JsArgs Fresh() /*-{
    return [];
  }-*/;

  public final native String getString(int key) /*-{
    return this[key].toString();
  }-*/;

  public final native int getInt(int key) /*-{
    return this[key];
  }-*/;

  public final native double getDouble(int key) /*-{
    return this[key];
  }-*/;

  public final java.math.BigInteger getBigInteger(int key) {
    return new java.math.BigInteger(getString(key));
  };

  public final java.math.BigDecimal getBigDecimal(int key) {
    return new java.math.BigDecimal(getString(key));
  }

  public final java.math.RoundingMode getRoundingMode(int key) {
    return java.math.RoundingMode.valueOf(getString(key));
  }

  public final java.math.MathContext getMathContext(int key) {
    return new java.math.MathContext(getString(key));
  }

  public final char[] getCharArray(int key) {
    return getString(key).toCharArray();
  }

  public static final native String signature(JsArgs self) /*-{
    var result = [];
    for (var i=0; i<self.length; i++) {
      var type = typeof(self[i]);
      if(type != 'object')
        result[result.length] = type;
      else if(self[i] instanceof Array)
        result[result.length] = 'array';
      else {
        // An object, but possibly an instance of a known type.
        if($wnd && $wnd.bigdecimal && $wnd.bigdecimal.BigInteger && (self[i] instanceof $wnd.bigdecimal.BigInteger))
          result[result.length] = 'BigInteger';
        else if($wnd && $wnd.bigdecimal && $wnd.bigdecimal.BigDecimal && (self[i] instanceof $wnd.bigdecimal.BigDecimal))
          result[result.length] = 'BigDecimal';
        else if($wnd && $wnd.bigdecimal && $wnd.bigdecimal.RoundingMode && (self[i] instanceof $wnd.bigdecimal.RoundingMode))
          result[result.length] = 'RoundingMode';
        else if($wnd && $wnd.bigdecimal && $wnd.bigdecimal.MathContext && (self[i] instanceof $wnd.bigdecimal.MathContext))
          result[result.length] = 'MathContext';
        else
          result[result.length] = 'object';
      }
    }
    return result.join(' ');
  }-*/;
  
  public final native void push(JavaScriptObject obj) /*-{
    this[this.length] = obj;
  }-*/;
}
