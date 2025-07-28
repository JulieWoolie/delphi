package com.juliewoolie.delphiplugin.command;

import com.google.common.base.Strings;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.delphi.resource.ResourceModule;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphidom.Loggers;
import com.juliewoolie.delphiplugin.resource.PluginResources;
import org.slf4j.Logger;

@Getter @Setter
public class PathParser<S> implements SuggestionProvider<S> {

  private static final Logger LOGGER = Loggers.getLogger();

  static final TranslatableExceptionType EMPTY_MODULE
      = new TranslatableExceptionType("delphi.paths.emptyModule");

  static final TranslatableExceptionType UNKNOWN_MODULE
      = new TranslatableExceptionType("delphi.paths.unknownModule");

  static final TranslatableExceptionType ILLEGAL_CHARACTER
      = new TranslatableExceptionType("delphi.paths.illegalCharacter");

  static final char QUERY_START = '?';
  static final char QUERY_DELIMITER = '&';
  static final char KEY_VALUE_DELIMITER = '=';
  static final char DIRECTORY_SEPARATOR = '/';
  static final char MODULE_SUFFIX = ':';

  private final StringReader reader;
  private final PluginResources pluginResources;

  private ResourcePath path;
  private ResourcePath suggestionsPath;
  private ResourcePath cwd;
  private ResourceModule module;

  private int suggestionsStart = -1;
  private SuggestionMode suggestionMode = null;

  public PathParser(PluginResources pluginResources, StringReader reader) {
    this.reader = reader;
    this.pluginResources = pluginResources;
  }

  private void suggest(int start, SuggestionMode mode) {
    this.suggestionMode = mode;
    this.suggestionsStart = start;
  }

  public void parse() throws CommandSyntaxException {
    int start = reader.getCursor();

    suggest(start, SuggestionMode.MODULE_NAMES);

    String moduleName = reader.readUnquotedString();

    if (Strings.isNullOrEmpty(moduleName)) {
      reader.setCursor(start);
      throw EMPTY_MODULE.createWithContext(reader);
    }

    if (this.pluginResources != null) {
      this.module = pluginResources.findModule(moduleName)
          .getOrThrow(string -> {
            reader.setCursor(start);
            return UNKNOWN_MODULE.createWithContext(reader, moduleName);
          });
    }

    suggest(reader.getCursor(), SuggestionMode.COLON);

    path = ResourcePath.create(moduleName);
    suggestionsPath = ResourcePath.create(moduleName);

    if (!reader.canRead() || reader.peek() != MODULE_SUFFIX) {
      return;
    }

    reader.skip();
    suggest(reader.getCursor(), SuggestionMode.FILE_PATHS);

    parsePath();

    if (!reader.canRead() || reader.peek() != QUERY_START) {
      return;
    }

    parseQuery();
  }

  private void pushToPath(String str) {
    if (str.equals("..")) {
      if (path.elementCount() > 0) {
        path = path.removeElement(path.elementCount() - 1);
      }

      return;
    }

    if (str.equals(".")) {
      path = path.clearElements();

      if (cwd == null) {
        return;
      }

      path = path.setElements(cwd);
      return;
    }

    path = path.addElement(str);
  }

  public void parsePath() throws CommandSyntaxException {
    reader.skipWhitespace();

    while (reader.canRead()) {
      String str = readPathElement();

      if (!str.isEmpty()) {
        pushToPath(str);
      }

      if (!reader.canRead()) {
        return;
      }

      char ch = reader.peek();

      if (Character.isWhitespace(ch)) {
        // end of argument
        return;
      }

      if (ch == QUERY_START) {
        break;
      }

      if (ch == '\\') {
        reader.skip();
      } else {
        reader.expect(DIRECTORY_SEPARATOR);
      }

      if (suggestionsPath != null) {
        suggestionsPath = suggestionsPath.setElements(path);
      }

      suggest(reader.getCursor(), SuggestionMode.FILE_PATHS);
    }
  }

  private void pushQuery(String key, String val) {
    if (Strings.isNullOrEmpty(key)) {
      return;
    }

    path = path.setQuery(key, val);
  }

  public void parseQuery() throws CommandSyntaxException {
    reader.expect(QUERY_START);

    while (true) {
      String key = reader.readUnquotedString();

      if (!reader.canRead()) {
        pushQuery(key, null);
        break;
      }

      if (reader.peek() == KEY_VALUE_DELIMITER) {
        reader.skip();

        if (reader.canRead() && StringReader.isAllowedInUnquotedString(reader.peek())) {
          String value = reader.readUnquotedString();
          pushQuery(key, value);
        } else {
          pushQuery(key, null);
        }
      } else {
        pushQuery(key, null);
      }

      if (!reader.canRead() || Character.isWhitespace(reader.peek())) {
        break;
      }

      reader.expect(QUERY_DELIMITER);
    }
  }

  private String readPathElement() throws CommandSyntaxException {
    char peek = reader.peek();

    if (peek == '"' || peek == '\'') {
      reader.skip();
      return readQuotedElement(peek);
    }

    return readUnquotedElement();
  }

  private String readUnquotedElement() {
    int start = reader.getCursor();

    while (reader.canRead() && isValidFileChar(reader.peek())) {
      reader.skip();
    }

    return reader.getString().substring(start, reader.getCursor());
  }

  private String readQuotedElement(char quote) throws CommandSyntaxException {
    int start = reader.getCursor();

    while (true) {
      if (!reader.canRead()) {
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
            .readerExpectedEndOfQuote()
            .createWithContext(reader);
      }

      if (reader.peek() == quote) {
        reader.skip();
        break;
      }

      char ch = reader.peek();

      if (isValidFileChar(ch) || ch == ' ') {
        reader.skip();
        continue;
      }

      throw ILLEGAL_CHARACTER.createWithContext(reader, ch);
    }

    return reader.getString().substring(start, reader.getCursor()-1);
  }

  private static boolean isValidFileChar(char ch) {
    return StringReader.isAllowedInUnquotedString(ch) || ch == '$' || ch == '%';
  }

  @Override
  public CompletableFuture<Suggestions> getSuggestions(
      CommandContext<S> context,
      SuggestionsBuilder builder
  ) {
    if (suggestionMode == null) {
      suggestionMode = SuggestionMode.MODULE_NAMES;
    }

    if (suggestionsStart > 0 && suggestionsStart != builder.getStart()) {
      builder = builder.createOffset(suggestionsStart);
    }

    switch (suggestionMode) {
      case MODULE_NAMES -> {
        if (this.pluginResources == null) {
          return builder.buildFuture();
        }

        List<String> modules = pluginResources.getNonHiddenModuleNames();
        return suggest(builder, modules);
      }
      case COLON -> {
        builder.suggest(":");
        return builder.buildFuture();
      }
      case FILE_PATHS -> {
        if (suggestionsPath == null) {
          return builder.buildFuture();
        }

        Collection<String> paths = module.getModulePaths(suggestionsPath);
        return suggest(builder, paths);
      }
    }

    return builder.buildFuture();
  }

  private CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, Iterable<String> strings) {
    String token = builder.getRemainingLowerCase();

    for (String string : strings) {
      if (!string.toLowerCase().startsWith(token)) {
        continue;
      }

      builder.suggest(string);
    }

    return builder.buildFuture();
  }

  enum SuggestionMode {
    MODULE_NAMES,
    COLON,
    FILE_PATHS
  }
}
