package net.arcadiusmc.delphidom.parser;

import java.util.List;
import lombok.Getter;

public class PluginMissingException extends RuntimeException {

  @Getter
  private final List<String> pluginNames;

  public PluginMissingException(List<String> pluginNames) {
    this.pluginNames = pluginNames;
  }
}
