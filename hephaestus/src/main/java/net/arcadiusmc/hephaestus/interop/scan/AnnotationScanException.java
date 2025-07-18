package net.arcadiusmc.hephaestus.interop.scan;

public class AnnotationScanException extends Exception {

  public AnnotationScanException() {
  }

  public AnnotationScanException(String message) {
    super(message);
  }

  public static AnnotationScanException of(String format, Object... args) {
    return new AnnotationScanException(String.format(format, args));
  }
}
