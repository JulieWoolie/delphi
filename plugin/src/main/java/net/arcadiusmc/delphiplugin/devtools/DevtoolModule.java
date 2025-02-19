package net.arcadiusmc.delphiplugin.devtools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import joptsimple.internal.Strings;
import net.arcadiusmc.delphi.Delphi;
import net.arcadiusmc.delphi.DelphiProvider;
import net.arcadiusmc.delphi.DocumentView;
import net.arcadiusmc.delphi.resource.ApiModule;
import net.arcadiusmc.delphi.resource.DocumentContext;
import net.arcadiusmc.delphi.resource.ResourcePath;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.dom.Document;
import net.arcadiusmc.dom.Element;
import net.arcadiusmc.dom.style.Stylesheet;
import org.jetbrains.annotations.NotNull;

public class DevtoolModule implements ApiModule {

  @Override
  public Result<Document, String> loadDocument(
      @NotNull ResourcePath path,
      @NotNull DocumentContext context
  ) {
    String targetInstName = path.getQuery("target-instance");
    if (Strings.isNullOrEmpty(targetInstName)) {
      return Result.err("No 'target-instance' query param set");
    }

    Delphi delphi = DelphiProvider.get();
    Optional<DocumentView> viewOpt = delphi.getByInstanceName(targetInstName);

    if (viewOpt.isEmpty()) {
      return Result.err("No view with instance-name '" + targetInstName + "' found");
    }

    DocumentView view = viewOpt.get();

    String domSource = loadResource("devtools/index.xml");
    String scssSource = loadResource("devtools/devtools.scss");

    Stylesheet stylesheet = context.parseStylesheet(scssSource);
    Document dom = context.parseDocument(domSource);

    dom.addStylesheet(stylesheet);

    return Result.ok(dom);
  }

  private String loadResource(String uri) {
    InputStream stream = getClass().getClassLoader().getResourceAsStream(uri);
    if (stream == null) {
      return "";
    }

    InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    StringWriter writer = new StringWriter();

    try {
      reader.transferTo(writer);
      stream.close();
    } catch (IOException exc) {
      throw new RuntimeException(exc);
    }

    return writer.toString();
  }

  private void createTopBar(Document document) {
    Element topbar = document.createElement("navbar");

    topbar.appendElement("navlink")
        .setTextContent("");
  }

  @Override
  public @NotNull Collection<String> getModulePaths(@NotNull ResourcePath pathSoFar) {
    return List.of();
  }
}
