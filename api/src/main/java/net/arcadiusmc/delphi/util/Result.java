package net.arcadiusmc.delphi.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Result<T, E> permits Err, Ok {

  static <T, E> Result<T, E> err(E exc) {
    Objects.requireNonNull(exc, "Null error value");
    return new Err<>(exc);
  }

  static <T1, T2, E> Result<T2, E> err(Result<T1, E> res) {
    Objects.requireNonNull(res, "Null result");

    if (!res.isError()) {
      throw new IllegalArgumentException("result is not an error");
    }

    return (Result<T2, E>) res;
  }

  static <T> Result<T, String> formatted(String string, Object... args) {
    return err(String.format(string, args));
  }

  static <T, E> Result<T, E> ok(T value) {
    Objects.requireNonNull(value, "Null result value");
    return new Ok<>(value);
  }

  /**
   * Gets the value of the result.
   *
   * @return An optional containing the value of this result, if it's not an error, or an empty
   *         optional if this is an erroneous result.
   */
  Optional<T> value();

  /**
   * Gets the result's error.
   *
   * @return An optional containing the error of this result, or an empty optional, if this is a
   *         successful result.
   */
  Optional<E> error();

  <T2> Result<T2, E> map(Function<T, T2> mapper);

  <T2> Result<T2, E> flatMap(Function<T, Result<T2, E>> mapper);

  <E2> Result<T, E2> mapError(Function<E, E2> mapper);

  boolean isError();

  boolean isSuccess();

  Result<T, E> ifError(Consumer<E> consumer);

  Result<T, E> ifSuccess(Consumer<T> consumer);

  T getOrThrow() throws ResultException;

  <X extends Exception> T getOrThrow(Function<E, X> factory) throws X;
}
