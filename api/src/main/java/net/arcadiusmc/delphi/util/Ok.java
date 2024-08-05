package net.arcadiusmc.delphi.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

final class Ok<T, E> implements Result<T, E> {

  private final T value;

  public Ok(T value) {
    this.value = value;
  }

  @Override
  public Optional<T> value() {
    return Optional.of(value);
  }

  @Override
  public Optional<E> error() {
    return Optional.empty();
  }

  @Override
  public <T2> Result<T2, E> map(Function<T, T2> mapper) {
    Objects.requireNonNull(mapper, "Null mapping function");
    T2 nval = mapper.apply(value);
    Objects.requireNonNull(nval, "Mapping function returned a null value");
    return new Ok<>(nval);
  }

  @Override
  public <T2> Result<T2, E> flatMap(Function<T, Result<T2, E>> mapper) {
    Objects.requireNonNull(mapper, "Null mapping function");
    Result<T2, E> res = mapper.apply(value);
    Objects.requireNonNull(res, "Mapping function returned a null value");
    return res;
  }

  @Override
  public <E2> Result<T, E2> mapError(Function<E, E2> mapper) {
    Objects.requireNonNull(mapper, "Null mapping function");
    return (Result<T, E2>) this;
  }

  @Override
  public boolean isError() {
    return false;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public Result<T, E> ifError(Consumer<E> consumer) {
    Objects.requireNonNull(consumer, "Null consumer");
    return this;
  }

  @Override
  public Result<T, E> ifSuccess(Consumer<T> consumer) {
    Objects.requireNonNull(consumer, "Null consumer");
    consumer.accept(value);
    return this;
  }

  @Override
  public T getOrThrow() throws ResultException {
    return value;
  }

  @Override
  public <X extends Exception> T getOrThrow(Function<E, X> factory) throws X {
    return value;
  }

  @Override
  public T orElse(T defaultValue) {
    return value;
  }

  @Override
  public T orElseGet(Supplier<T> getter) {
    Objects.requireNonNull(getter, "Null fallback value supplier");
    return value;
  }
}
