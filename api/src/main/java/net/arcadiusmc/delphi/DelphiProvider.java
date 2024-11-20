package net.arcadiusmc.delphi;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Delphi service provider interface.
 */
public final class DelphiProvider {
  private DelphiProvider() {}

  /**
   * Get the delphi service
   *
   * @return Delphi
   *
   * @throws NullPointerException If no Delphi service was found
   */
  public static @NotNull Delphi get() {
    return Objects.requireNonNull(
        Bukkit.getServicesManager().load(Delphi.class),
        "Delphi service not found"
    );
  }

  /**
   * Create a new document request.
   * @return Created request
   * @throws NullPointerException If no Delphi service exists yet
   */
  public static @NotNull DocumentViewBuilder newViewBuilder() {
    return get().newViewBuilder();
  }
}
