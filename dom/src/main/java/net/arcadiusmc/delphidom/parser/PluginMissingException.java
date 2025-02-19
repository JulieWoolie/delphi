package net.arcadiusmc.delphidom.parser;

import java.util.List;
import java.util.StringJoiner;
import lombok.Getter;

public class PluginMissingException extends RuntimeException {

  @Getter
  private final List<String> pluginNames;

  public PluginMissingException(List<String> pluginNames) {
    super(createMessages(pluginNames));
    this.pluginNames = pluginNames;
  }

  private static String createMessages(List<String> names) {
    StringJoiner joiner = new StringJoiner(", ", "Missing required plugins: ", "");
    for (String name : names) {
      joiner.add(name);
    }
    return joiner.toString();
  }
}
