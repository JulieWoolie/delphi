package com.juliewoolie.delphi.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

final class Err<T, E> implements Result<T, E> {

  private final E error;

  public Err(E error) {
    this.error = error;
  }

  @Override
  public Optional<T> value() {
    return Optional.empty();
  }

  @Override
  public Optional<E> error() {
    return Optional.of(error);
  }

  @Override
  public <T2> Result<T2, E> map(Function<T, T2> mapper) {
    Objects.requireNonNull(mapper, "Null mapping function");
    return (Result<T2, E>) this;
  }

  @Override
  public <T2> Result<T2, E> flatMap(Function<T, Result<T2, E>> mapper) {
    Objects.requireNonNull(mapper, "Null mapping function");
    return (Result<T2, E>) this;
  }

  @Override
  public <E2> Result<T, E2> mapError(Function<E, E2> mapper) {
    Objects.requireNonNull(mapper, "Null mapping function");
    E2 err = mapper.apply(error);
    Objects.requireNonNull(err, "Mapping function returned null value");
    return new Err<>(err);
  }

  @Override
  public boolean isError() {
    return true;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

  @Override
  public Result<T, E> ifError(Consumer<E> consumer) {
    Objects.requireNonNull(consumer, "Null consumer");
    consumer.accept(error);
    return this;
  }

  @Override
  public Result<T, E> ifSuccess(Consumer<T> consumer) {
    Objects.requireNonNull(consumer, "Null consumer");
    return this;
  }

  @Override
  public T getOrThrow() throws ResultException {
    if (error instanceof Throwable throwable) {
      throw new ResultException(throwable);
    }
    if (error instanceof String string) {
      throw new ResultException(string);
    }

    throw new ResultException(error);
  }

  @Override
  public <X extends Exception> T getOrThrow(Function<E, X> factory) throws X {
    throw factory.apply(error);
  }

  @Override
  public T orElseGet(Supplier<T> getter) {
    Objects.requireNonNull(getter, "Null fallback value supplier");
    return getter.get();
  }

  @Override
  public T orElse(T defaultValue) {
    return defaultValue;
  }
}
