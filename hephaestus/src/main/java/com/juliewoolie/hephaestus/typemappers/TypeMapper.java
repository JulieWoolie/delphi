package com.juliewoolie.hephaestus.typemappers;

import java.util.function.Function;
import java.util.function.Predicate;
import org.graalvm.polyglot.HostAccess;

public interface TypeMapper<F, T> extends Predicate<F>, Function<F, T> {

  static <F, T> void addTypeMapper(
      HostAccess.Builder builder,
      Class<F> sourceClass,
      Class<T> targetClass,
      TypeMapper<F, T> mapper
  ) {
    builder.targetTypeMapping(sourceClass, targetClass, mapper, mapper);
  }
}
