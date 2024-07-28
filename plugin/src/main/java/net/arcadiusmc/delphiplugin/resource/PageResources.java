package net.arcadiusmc.delphiplugin.resource;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import lombok.Getter;
import lombok.Setter;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.scss.ScssParser;
import net.arcadiusmc.delphidom.scss.Sheet;
import net.arcadiusmc.delphidom.parser.DocumentSaxParser;
import net.arcadiusmc.delphidom.parser.ErrorListener;
import net.arcadiusmc.delphi.resource.ApiModule;
import net.arcadiusmc.delphi.resource.DocumentFactory;
import net.arcadiusmc.delphi.resource.IoModule;
import net.arcadiusmc.delphi.resource.ResourceModule;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphiplugin.command.PathParser;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.ParserException;
import net.arcadiusmc.dom.TagNames;
import net.arcadiusmc.dom.style.Stylesheet;
import org.slf4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PageResources implements ViewResources {

  static final DocumentFactory FACTORY = () -> {
    DelphiDocument doc = new DelphiDocument();
    doc.setBody(doc.createElement(TagNames.BODY));
    return doc;
  };

  private static final Logger LOGGER = Loggers.getDocumentLogger();

  static final ErrorListener ERROR_LISTENER = error -> {
    switch (error.level()) {
      case ERROR -> LOGGER.error(error.message());
      case WARN -> LOGGER.warn(error.message());
      default -> {}
    }
  };

  @Getter
  private final Map<String, Object> styleVariables = new HashMap<>();

  @Getter
  private final String moduleName;

  private final Modules modules;

  @Getter
  private final ResourceModule module;

  @Setter
  private ResourcePath cwd;

  public PageResources(Modules modules, String moduleName, ResourceModule module) {
    this.modules = modules;
    this.moduleName = moduleName;
    this.module = module;
  }

  public Result<ResourcePath, String> resourcePath(String uri) {
    ResourcePath path;

    try {
      StringReader reader = new StringReader(uri);
      PathParser<?> parser = new PathParser<>(modules, reader);

      parser.setModule(module);
      parser.setPath(ResourcePath.create(moduleName));
      parser.setCwd(cwd);

      parser.parsePath();
      path = parser.getPath();
    } catch (CommandSyntaxException exc) {
      return Result.err("Invalid path");
    }

    return Result.ok(path);
  }

  @Override
  public Result<Document, String> loadDocument(String uri) {
    return resourcePath(uri)
        .flatMap(path -> loadDocument(path, uri))
        .map(delphiDocument -> delphiDocument);
  }

  public Result<DelphiDocument, String> loadDocument(ResourcePath path, String uri) {
    if (module instanceof ApiModule api) {
      Document doc = api.loadDocument(path, FACTORY);
      return Result.ok((DelphiDocument) doc);
    }

    IoModule io = (IoModule) module;
    StringBuffer buf;

    try {
      buf = io.loadString(path);
    } catch (IOException exc) {
      return handleIo(exc);
    }

    SAXParser parser;
    try {
      parser = DocumentSaxParser.PARSER_FACTORY.newSAXParser();
    } catch (ParserConfigurationException | SAXException e) {
      return Result.formatted("Internal error creating parser", path);
    }

    InputSource source = new InputSource(new java.io.StringReader(buf.toString()));
    source.setPublicId(uri);
    source.setSystemId(uri);

    DocumentSaxParser handler = new DocumentSaxParser(this);
    handler.setListener(DelphiDocument.ERROR_LISTENER);

    try {
      parser.parse(source, handler);
    } catch (SAXException e) {
      return Result.formatted("Failed to parse document");
    } catch (IOException ioErr) {
      return handleIo(ioErr);
    }

    if (handler.getDocument() == null) {
      return Result.err("Unknown");
    }

    return Result.ok(handler.getDocument());
  }

  @Override
  public Result<Stylesheet, String> loadStylesheet(String uri) {
    return resourcePath(uri).flatMap(this::loadStylesheet);
  }

  public Result<Stylesheet, String> loadStylesheet(ResourcePath path) {
    if (module instanceof ApiModule) {
      return Result.err("API-MODULE");
    }

    IoModule io = (IoModule) module;
    StringBuffer buf;

    try {
      buf = io.loadString(path);
    } catch (IOException exc) {
      return handleIo(exc);
    }

    ScssParser parser = new ScssParser(buf);
    parser.setVariables(styleVariables);
    parser.getErrors().setListener(ERROR_LISTENER);

    Sheet sheet;

    try {
      sheet = parser.stylesheet();
    } catch (ParserException exc) {
      // Ignored, the error has already been logged by setListener
      return Result.formatted("Fatal parser error: %s", exc.getMessage());
    }

    return Result.ok(sheet);
  }

  <T> Result<T, String> handleIo(IOException exc) {
    if (exc instanceof NoSuchFileException) {
      return Result.formatted("No such file");
    }
    if (exc instanceof AccessDeniedException acc) {
      return Result.formatted("Access Denied: %s", acc.getReason());
    }

    return Result.formatted("IO Error: %s", exc.getMessage());
  }
}
