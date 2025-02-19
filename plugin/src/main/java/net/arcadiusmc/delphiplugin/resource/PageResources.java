package net.arcadiusmc.delphiplugin.resource;

import static net.arcadiusmc.delphi.resource.DelphiException.ERR_ACCESS_DENIED;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_API_MODULE;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_DOC_PARSE;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_INVALID_PATH;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_IO_ERROR;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MISSING_PLUGINS;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_MODULE_ERROR;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_NO_FILE;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_SAX_PARSER_INIT;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_SCHEMA_ERROR;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_SYNTAX;
import static net.arcadiusmc.delphi.resource.DelphiException.ERR_UNKNOWN;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.chimera.ChimeraSheetBuilder;
import net.arcadiusmc.chimera.ChimeraStylesheet;
import net.arcadiusmc.chimera.parse.Chimera;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.PlayerSet;
import net.arcadiusmc.delphi.resource.ApiModule;
import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.DocumentContext;
import net.arcadiusmc.delphi.resource.IoModule;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.parser.DelphiSaxParser;
import net.arcadiusmc.delphidom.parser.PluginMissingException;
import net.arcadiusmc.delphiplugin.PageView;
import net.arcadiusmc.delphiplugin.command.PathParser;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.style.Stylesheet;
import net.arcadiusmc.dom.style.StylesheetBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PageResources implements ViewResources {

  private static final Logger LOGGER = Loggers.getDocumentLogger();

  @Getter
  private final String moduleName;

  private final PluginResources pluginResources;

  @Getter
  private final ResourceModule module;

  @Setter
  private ResourcePath cwd;

  @Setter @Getter
  private PageView view;

  public PageResources(PluginResources pluginResources, String moduleName, ResourceModule module) {
    this.pluginResources = pluginResources;
    this.moduleName = moduleName;
    this.module = module;
  }

  public Result<ResourcePath, DelphiException> resourcePath(String uri) {
    ResourcePath path;

    try {
      StringReader reader = new StringReader(uri);
      PathParser<?> parser = new PathParser<>(pluginResources, reader);

      parser.setModule(module);
      parser.setPath(ResourcePath.create(moduleName));
      parser.setCwd(cwd);

      parser.parsePath();
      path = parser.getPath();
    } catch (CommandSyntaxException exc) {
      return Result.err(new DelphiException(ERR_INVALID_PATH, exc.getMessage()));
    }

    return Result.ok(path);
  }

  @Override
  public Result<ItemStack, DelphiException> loadItemStack(String uri) {
    return resourcePath(uri).flatMap(this::loadItemStack);
  }

  private Result<ItemStack, DelphiException> loadItemStack(ResourcePath path) {
    if (module instanceof ApiModule) {
      return Result.err(new DelphiException(ERR_API_MODULE));
    }

    StringBuffer buf;

    try {
      buf = ((IoModule) module).loadString(path);
    } catch (IOException exc) {
      return Result.ioError(exc);
    }

    return parseItemStack(buf.toString());
  }

  @Override
  public Result<ItemStack, DelphiException> parseItemStack(String json) {
    JsonObject obj;

    try {
      var element = JsonParser.parseString(json);
      if (!(element instanceof JsonObject o)) {
        return Result.err(new DelphiException(ERR_SYNTAX, "Not a JSON object: " + element));
      }
      obj = o;
    } catch (JsonSyntaxException exc) {
      return Result.err(new DelphiException(ERR_SYNTAX, exc));
    }

    // This should be public API in UnsafeValues but the --brilliant-- (read: downright stupid)
    // devs of Paper put it as a public function in CraftMagicNumbers but not in the API
    // Hopefully this changes. If not, we go back to using ItemCodec.NMS_CODEC
    CraftMagicNumbers unsafe = CraftMagicNumbers.INSTANCE;
    ItemStack item;

    // If this is not check, will throw error, a DataVersion must
    // always be present
    if (!obj.has("DataVersion")) {
      obj.addProperty("DataVersion", unsafe.getDataVersion());
    }

    try {
      item = unsafe.deserializeItemFromJson(obj);
    } catch (IllegalArgumentException exc) {
      return Result.err(new DelphiException(ERR_SCHEMA_ERROR, exc));
    }

    return Result.ok(item);
  }

  @Override
  public Result<Document, DelphiException> loadDocument(String uri) {
    return resourcePath(uri)
        .flatMap(path -> loadDocument(path, uri))
        .map(delphiDocument -> delphiDocument);
  }

  public Result<DelphiDocument, DelphiException> loadDocument(ResourcePath path, String uri) {
    if (module instanceof ApiModule api) {
      Result<Document, String> result;

      try {
        result = api.loadDocument(
            path,
            new ContextImpl(view.getPlayers(), view, pluginResources.getDefaultStyle(), this)
        );
      } catch (Exception t) {
        LOGGER.error("Module {} threw an error when attempting to load document",
            path.getModuleName(), t
        );

        return Result.err(new DelphiException(ERR_MODULE_ERROR, t));
      }
      
      return result
          .mapError(string -> {
            if (string.equalsIgnoreCase("No such file")) {
              return new DelphiException(ERR_NO_FILE, path.toString());
            }
            if (string.startsWith("Access denied: ")) {
              return new DelphiException(ERR_ACCESS_DENIED, string);
            }
            if (string.startsWith("IO Error: ")) {
              return new DelphiException(ERR_IO_ERROR, string);
            }
            if (string.startsWith("Missing plugins: ")) {
              String pluginList = string.substring("Missing plugins: ".length());
              return new DelphiException(ERR_MISSING_PLUGINS, pluginList);
            }

            return new DelphiException(ERR_MODULE_ERROR, string);
          })
          .map(document -> (DelphiDocument) document);
    }

    IoModule io = (IoModule) module;
    StringBuffer buf;

    try {
      buf = io.loadString(path);
    } catch (IOException exc) {
      return Result.ioError(exc);
    }

    return parseDocument(buf, uri);
  }

  private Result<DelphiDocument, DelphiException> parseDocument(StringBuffer buf, String uri) {
    SAXParser parser;
    try {
      parser = DelphiSaxParser.PARSER_FACTORY.newSAXParser();
    } catch (ParserConfigurationException | SAXException e) {
      return Result.err(new DelphiException(ERR_SAX_PARSER_INIT, e));
    }

    InputSource source = new InputSource(new java.io.StringReader(buf.toString()));
    source.setPublicId(uri);
    source.setSystemId(uri);

    DelphiSaxParser handler = new DelphiSaxParser();
    handler.setListener(DelphiDocument.ERROR_LISTENER);
    handler.setView(view);

    try {
      parser.parse(source, handler);
    } catch (SAXException e) {
      return Result.err(new DelphiException(ERR_DOC_PARSE, e));
    } catch (IOException ioErr) {
      return Result.ioError(ioErr);
    } catch (PluginMissingException miss) {
      StringBuilder builder = new StringBuilder();
      Iterator<String> it = miss.getPluginNames().iterator();

      while (it.hasNext()) {
        builder.append(it.next());

        if (it.hasNext()) {
          builder.append(", ");
        }
      }

      return Result.err(new DelphiException(ERR_MISSING_PLUGINS, builder.toString()));
    } catch (Exception exc) {
      return Result.err(new DelphiException(ERR_UNKNOWN, exc));
    }

    if (handler.getDocument() == null) {
      return Result.err(new DelphiException(ERR_UNKNOWN));
    }

    DelphiDocument doc = handler.getDocument();
    doc.getStyles().setDefaultStyleSheet(pluginResources.getDefaultStyle());

    return Result.ok(doc);
  }

  @Override
  public Result<Stylesheet, DelphiException> loadStylesheet(String uri) {
    return resourcePath(uri).flatMap(this::loadStylesheet);
  }

  public Result<Stylesheet, DelphiException> loadStylesheet(ResourcePath path) {
    if (module instanceof ApiModule) {
      return Result.err(new DelphiException(ERR_API_MODULE));
    }

    IoModule io = (IoModule) module;
    StringBuffer buf;

    try {
      buf = io.loadString(path);
    } catch (IOException exc) {
      return Result.ioError(exc);
    }

    return Result.ok(Chimera.parseSheet(buf, path.toString()));
  }

  @Override
  public Result<Component, DelphiException> loadComponent(
      @NotNull String uri,
      @Nullable ComponentFormat format
  ) {
    return resourcePath(uri).flatMap(p -> loadComponent(p, format));
  }

  public Result<Component, DelphiException> loadComponent(
      @NotNull ResourcePath path,
      @Nullable ComponentFormat format
  ) {
    if (module instanceof ApiModule) {
      return Result.err(new DelphiException(ERR_API_MODULE));
    }

    StringBuffer buf;

    try {
      buf = ((IoModule) module).loadString(path);
    } catch (IOException e) {
      return Result.ioError(e);
    }

    String s = buf.toString();
    return parseComponent(s, format);
  }

  @Override
  public Result<Component, DelphiException> parseComponent(
      @NotNull String data,
      @Nullable ComponentFormat format
  ) {
    if (format == ComponentFormat.MINIMESSAGE) {
      return Result.ok(MiniMessage.miniMessage().deserialize(data));
    }

    try {
      Component c = GsonComponentSerializer.gson().deserialize(data);
      return Result.ok(c);
    } catch (JsonParseException exc) {
      return Result.err(new DelphiException(ERR_SCHEMA_ERROR, exc));
    }
  }

  record ContextImpl(
      PlayerSet players,
      PageView view,
      ChimeraStylesheet defaultSheet,
      PageResources resources
  ) implements DocumentContext {

    @Override
    public @NotNull Document newDocument() {
      return DelphiDocument.createEmpty();
    }

    @Override
    public @NotNull StylesheetBuilder newStylesheet() {
      return new ChimeraSheetBuilder(null);
    }

    @Override
    public @NotNull Stylesheet parseStylesheet(@NotNull String string) {
      Objects.requireNonNull(string, "Null string");
      return Chimera.parseSheet(new StringBuffer(string), "<stylesheet>");
    }

    @Override
    public @NotNull Document parseDocument(@NotNull String string) throws ParserException {
      return resources
          .parseDocument(new StringBuffer(string), "<document>")
          .getOrThrow(e -> e);
    }

    @Override
    public @NotNull PlayerSet getPlayers() {
      return players;
    }

    @Override
    public @NotNull DocumentView getView() {
      return view;
    }
  }
}
