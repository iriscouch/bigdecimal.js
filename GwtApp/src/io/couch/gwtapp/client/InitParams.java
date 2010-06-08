package io.couch.gwtapp.client;

import com.google.gwt.core.client.JavaScriptObject;

class InitParams extends JavaScriptObject {
  protected InitParams() {}

  public final native String get(String key) /*-{
    return this[key] ? this[key].toString() : this[key] === false ? "false" : null;
  }-*/;

  public final boolean has(String key) {
    return get(key) != null;
  }

  public final int getInt(String key) {
    return Integer.parseInt(get(key).trim());
  }

  public final long getLong(String key) {
    return Long.parseLong(get(key).trim());
  }

  public final double getDouble(String key) {
    return Double.valueOf(get(key));
  }

  public final double getNum(String key) {
    return getDouble(key);
  }
}
