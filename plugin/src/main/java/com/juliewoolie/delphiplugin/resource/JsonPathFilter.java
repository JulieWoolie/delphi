package com.juliewoolie.delphiplugin.resource;

import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Path;

public enum JsonPathFilter implements Filter<Path> {
  FILTER;

  @Override
  public boolean accept(Path entry) {
    String fpath = entry.toString();
    return fpath.endsWith(".json");
  }
}
