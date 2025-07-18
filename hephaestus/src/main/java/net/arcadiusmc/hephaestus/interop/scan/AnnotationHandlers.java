package net.arcadiusmc.hephaestus.interop.scan;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.lang.annotation.Annotation;
import java.util.Map;

public class AnnotationHandlers {

  private final Map<Class<?>, AnnotationHandler<?>> handlers = new Object2ObjectArrayMap<>();

  public AnnotationHandlers() {
    addHandler(new GetPropertyHandler());
    addHandler(new SetPropertyHandler());
    addHandler(new ScriptMethodHandler());
    addHandler(new IndexReadHandler());
    addHandler(new IndexWriteHandler());
    addHandler(new ScriptGetLengthHandler());
  }

  private <T extends Annotation> void addHandler(AnnotationHandler<T> handler) {
    handlers.put(handler.getAnnotationType(), handler);
  }

  public <T extends Annotation> AnnotationHandler<T> getHandler(T annot) {
    for (AnnotationHandler<?> value : handlers.values()) {
      if (value.getAnnotationType().isInstance(annot)) {
        return (AnnotationHandler<T>) value;
      }
    }
    return null;
  }
}
