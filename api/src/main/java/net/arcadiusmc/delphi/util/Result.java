package net.arcadiusmc.delphi.util;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.arcadiusmc.delphi.resource.DelphiException;
import org.jetbrains.annotations.Nullable;

/**
 * Monad that can either be an error or a success and holds either a
 * value or an error.
 *
 * @param <T> Value type
 * @param <E> Error type
 */
public sealed interface Result<T, E> permits Err, Ok {

  /**
   * Creates an erroneous result out of the specified {@code err} error.
   *
   * @param err Error value
   * @return Created result
   *
   * @throws NullPointerException If {@code err} is {@code null}
   */
  static <T, E> Result<T, E> err(E err) {
    Objects.requireNonNull(err, "Null error value");
    return new Err<>(err);
  }

  /**
   * Casting function to translate an erroneous result from one value type to another
   *
   * @param res Result to cast
   * @return Casted result
   *
   * @throws IllegalArgumentException If the specified result is non-erroneous
   */
  static <T1, T2, E> Result<T2, E> err(Result<T1, E> res) {
    Objects.requireNonNull(res, "Null result");

    if (!res.isError()) {
      throw new IllegalArgumentException("result is not an error");
    }

    return (Result<T2, E>) res;
  }

  /**
   * Creates a successful result with the specified non-{@code null} value.
   *
   * @param value Value
   * @return Created result
   *
   * @throws NullPointerException If {@code value} is {@code null}
   */
  static <T, E> Result<T, E> ok(T value) {
    Objects.requireNonNull(value, "Null result value");
    return new Ok<>(value);
  }

  /**
   * Wraps an IO error in a result.
   * <br>
   *
   * <table>
   *   <caption>Returned results</caption>
   *   <tr>
   *     <th>Exception Types</th>
   *     <th>Returned error code</th>
   *   </tr>
   *   <tr>
   *     <td>{@link NoSuchFileException}</td>
   *     <td>{@link DelphiException#ERR_NO_FILE}</td>
   *   </tr>
   *   <tr>
   *     <td>{@link AccessDeniedException}</td>
   *     <td>{@link DelphiException#ERR_ACCESS_DENIED}</td>
   *   </tr>
   *   <tr>
   *     <td>Any other IO exception type</td>
   *     <td>{@link DelphiException#ERR_IO_ERROR}</td>
   *   </tr>
   * </table>
   *
   * @param exc Exception to wrap
   * @return Error result
   */
  static <T> Result<T, DelphiException> ioError(IOException exc) {
    if (exc instanceof NoSuchFileException no) {
      return err(new DelphiException(DelphiException.ERR_NO_FILE, no.getFile(), no));
    }
    if (exc instanceof AccessDeniedException acc) {
      return err(new DelphiException(DelphiException.ERR_ACCESS_DENIED, acc));
    }

    return err(new DelphiException(DelphiException.ERR_IO_ERROR, exc));
  }

  /**
   * Get the successful 'nothing' result. A result which is successful but has no return value.
   * @return Empty successful result.
   */
  @SuppressWarnings("unchecked")
  static <E> Result<Nothing, E> nothing() {
    return Ok.NOTHING;
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

  /**
   * If a value is present, returns a new result with the return value of the specified
   * {@code mapper} function, otherwise, if this is an erroneous result, then nothing happens and
   * this result is cast to the mapped type.
   *
   * <p>
   * If the mapper function returns {@code null}, a {@link NullPointerException} is thrown.
   *
   * @param mapper Mapping function
   * @return Mapped result.
   *
   * @throws NullPointerException If {@code mapper} is {@code null}, or if the result of the
   *                              mapper function is {@code null}.
   */
  <T2> Result<T2, E> map(Function<T, T2> mapper);

  /**
   * If this is a successful result, returns the mapper function's result. Otherwise, nothing
   * happens and this result is cast to the mapped type and returned.
   * <p>
   * If the mapper function return {@code null}, a {@link NullPointerException} is thrown.
   *
   * @param mapper Mapping function
   * @return Mapped result
   *
   * @throws NullPointerException If {@code mapper} is {@code null}, or if the result of the
   *                              mapper function is {@code null}.
   */
  <T2> Result<T2, E> flatMap(Function<T, Result<T2, E>> mapper);

  /**
   * If this is an erroneous result, returns the mapper functions result as a new error result.
   * Otherwise, nothing happens and this result is returned.
   * <p>
   * If the mapper function returns {@code null}, a {@link NullPointerException} is thrown.
   *
   * @param mapper Error mapping function
   * @return Error-mapped result
   *
   * @throws NullPointerException If {@code mapper} is {@code null}, or if the result of the
   *                              mapper function is {@code null}.
   */
  <E2> Result<T, E2> mapError(Function<E, E2> mapper);

  /**
   * Tests if this result is erroneous.
   * @return {@code true}, if this result has a present {@link #error()}, {@code false}
   *         otherwise.
   */
  boolean isError();

  /**
   * Tests if this result is successful.
   * @return {@code true}, if this result has a present {@link #value()}. {@code false} otherwise
   */
  boolean isSuccess();

  /**
   * If this is an erroneous result, the specified {@code consumer} function is applied to the
   * result's error value. Otherwise, nothing happens.
   *
   * @param consumer Error consumer function
   *
   * @return {@code this}
   */
  Result<T, E> ifError(Consumer<E> consumer);

  /**
   * If this a successful result,  the specified {@code consumer} function is applied to the
   * result's value. Otherwise, nothing happens.
   *
   * @param consumer Value consumer function
   *
   * @return {@code this}
   */
  Result<T, E> ifSuccess(Consumer<T> consumer);

  /**
   * Gets the result's value or throws a {@link ResultException}.
   * <p>
   * If the result's error is a {@link Throwable} then the thrown {@link ResultException} will have
   * the throwable as its cause (Accessible with {@link Throwable#getCause()}).
   * <br>
   * If the result's error is a string, then it will be used as the {@link ResultException}'s
   * message.
   * <br>
   * Otherwise, a result exception will be thrown with a {@link ResultException#getErrorObject()}
   * of this result's error value.
   *
   * @return The result's value
   *
   * @throws ResultException If the result is an erroneous result.
   */
  T getOrThrow() throws ResultException;

  /**
   * Gets the result's value or throws an exception specified by the {@code factory} function.
   *
   * @param factory Exception factory
   * @return The result's value
   * @throws X If the result is an erroneous result.
   */
  <X extends Exception> T getOrThrow(Function<E, X> factory) throws X;

  /**
   * If the result is an erroneous result, the {@code defaultValue} is returned, otherwise the
   * result's value is returned.
   *
   * @param defaultValue Default value
   *
   * @return Result value, or the specified fallback value, if the result is erroneous.
   */
  T orElse(@Nullable T defaultValue);

  /**
   * If the result is an erroneous result, the {@code getter} function is called to get the return
   * value. Otherwise, the result's value is returned.
   *
   * @param getter Default value supplier
   *
   * @return Result value, or the specified fallback value, if the result is erroneous.
   */
  T orElseGet(Supplier<T> getter);
}
