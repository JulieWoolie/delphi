package com.juliewoolie.delphiplugin.devtools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import joptsimple.internal.Strings;
import com.juliewoolie.delphi.Delphi;
import com.juliewoolie.delphi.DelphiProvider;
import com.juliewoolie.delphi.DocumentView;
import com.juliewoolie.delphi.resource.ApiModule;
import com.juliewoolie.delphi.resource.DocumentContext;
import com.juliewoolie.delphi.resource.ResourceModule;
import com.juliewoolie.delphi.resource.ResourcePath;
import com.juliewoolie.delphi.util.Result;
import com.juliewoolie.dom.Document;
import com.juliewoolie.dom.style.Stylesheet;
import com.juliewoolie.dom.style.StylesheetBuilder;
import org.jetbrains.annotations.NotNull;

public class DevtoolModule implements ApiModule {

  @Override
  public Result<Document, String> loadDocument(
      @NotNull ResourcePath path,
      @NotNull DocumentContext context
  ) {
    String targetInstName = path.getQuery("target");
    if (Strings.isNullOrEmpty(targetInstName)) {
      return Result.err("No 'target' query param set");
    }

    Delphi delphi = DelphiProvider.get();
    Optional<DocumentView> viewOpt = delphi.getByInstanceName(targetInstName);

    if (viewOpt.isEmpty()) {
      return Result.err("No view with instance-name '" + targetInstName + "' found");
    }

    DocumentView view = viewOpt.get();
    ResourceModule targetModule = view.getResources().getModule();

    if (targetModule instanceof DevtoolModule) {
      return Result.err("Cannot open devtools on devtools view");
    }

    String domSource = loadResource("devtools/index.xml");
    String scssSource = loadResource("devtools/devtools.scss");

    Stylesheet stylesheet = context.parseStylesheet(scssSource);
    Stylesheet indents = generateIndentsSheet(context);

    Document dom = context.parseDocument(domSource);
    dom.addStylesheet(stylesheet);
    dom.addStylesheet(indents);

    Devtools devtools = new Devtools(view, dom);
    devtools.switchTo(Tabs.INSPECT_ELEMENT);

    return Result.ok(dom);
  }

  private Stylesheet generateIndentsSheet(DocumentContext ctx) {
    StylesheetBuilder builder = ctx.newStylesheet();
    final int indentLevels = 25;

    for (int i = 1; i <= indentLevels; i++) {
      float indentLevel = ((float) i) * 0.5f;

      builder.addRule(".indent-" + i + " span:first-of-type", prop -> {
        prop.setMarginInlineStart(indentLevel + "ch");
      });
    }

    return builder.build();
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

  @Override
  public @NotNull Collection<String> getModulePaths(@NotNull ResourcePath pathSoFar) {
    return List.of();
  }
}
