package io.couch.gwtapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.EntryPoint;

import org.timepedia.exporter.client.Exporter;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtApp implements EntryPoint {
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    //((Exporter) GWT.create(Jason.class)).export();
    ((Exporter) GWT.create(BigDec.class)).export();
    ((Exporter) GWT.create(RoundingMode.class)).export();
    onLoadImpl();
  }

  private native void onLoadImpl() /*-{
    if($wnd.console && $wnd.console.log)
      $wnd.console.log("Loaded");
  }-*/;
}
