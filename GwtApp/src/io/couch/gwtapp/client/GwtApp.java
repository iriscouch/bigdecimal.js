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
    ((Exporter) GWT.create(RoundingMode.class)).export();
    ((Exporter) GWT.create(MathContext.class)).export();
    ((Exporter) GWT.create(io.couch.gwtapp.client.BigInteger.class)).export();
    ((Exporter) GWT.create(io.couch.gwtapp.client.BigDecimal.class)).export();
  }
}
