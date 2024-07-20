package net.arcadiusmc.delphi;

import java.util.Set;

public interface PagePath {

  String getQuery(String key);

  Set<String> getQueryKeys();

  PagePath setQuery(String key, String value);


}
