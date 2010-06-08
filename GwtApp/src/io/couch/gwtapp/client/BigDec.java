package io.couch.gwtapp.client;

import java.math.BigDecimal;

import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.NoExport;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.ExportPackage;

@ExportPackage("j")

@Export
public class BigDec extends BigDecimal implements Exportable {
  public BigDec() {
    super("0");
  }

  public String toString() {
    return super.toString();
  }

  public String Smith() {
    return "This works";
  }
}
