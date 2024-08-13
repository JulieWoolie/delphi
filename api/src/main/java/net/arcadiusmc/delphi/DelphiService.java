package net.arcadiusmc.delphi;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class DelphiService {
  private DelphiService() {}

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
}
