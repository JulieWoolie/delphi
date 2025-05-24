package net.arcadiusmc.hephaestus;

import net.arcadiusmc.delphi.resource.DelphiException;
import net.arcadiusmc.delphi.resource.ViewResources;
import net.arcadiusmc.delphi.util.Result;
import net.arcadiusmc.delphidom.ContentSource;
import net.arcadiusmc.delphidom.DelphiDocument;
import net.arcadiusmc.delphidom.DelphiScriptElement;
import net.arcadiusmc.delphidom.ExtendedView;
import net.arcadiusmc.delphidom.Loggers;
import net.arcadiusmc.delphidom.system.ParsedDataElementSystem;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;

public class ScriptElementSystem extends ParsedDataElementSystem<DelphiScriptElement> {

  public static final String JS_LANGUAGE = "js";

  private static final Logger LOGGER = Loggers.getLogger();
  private Context context;
  private Value jsScope;

  public ScriptElementSystem() {
    super(DelphiScriptElement.class);
  }

  @Override
  public void onAttach(DelphiDocument document) {
    super.onAttach(document);

    this.context = Scripting.setupContext();
    this.jsScope = context.getBindings(JS_LANGUAGE);

    jsScope.putMember("document", document);
  }

  @Override
  public void onViewAttach(ExtendedView view) {
    super.onViewAttach(view);
    jsScope.putMember("view", view);
  }

  @Override
  protected void onRemove(DelphiScriptElement delphiScriptElement) {
    super.onRemove(delphiScriptElement);

  }

  @Override
  public void onDetach() {
    super.onDetach();

    jsScope.removeMember("document");

    context.close(true);

    context = null;
    jsScope = null;
  }

  @Override
  public void onViewDetach() {
    super.onViewDetach();
    jsScope.removeMember("view");
  }

  @Override
  protected void parseFromContent(DelphiScriptElement element) {
    String textContent = element.getTextContent();
    evaluate("<script>", textContent);
  }

  @Override
  protected void loadFromSrc(DelphiScriptElement element, String uri) {
    ViewResources resources = document.getView().getResources();
    Result<StringBuffer, DelphiException> result = resources.loadBuffer(uri);

    if (result.isError()) {
      LOGGER.error("Failed to load script source: {}", result.error().orElseThrow().getMessage());
      return;
    }

    String content = result.value().orElseThrow().toString();
    evaluate(uri, content);
  }

  @Override
  protected ContentSource getSource(DelphiScriptElement element) {
    return element.source;
  }

  @Override
  protected void setSource(DelphiScriptElement element, ContentSource source) {
    element.source = source;
  }

  private void evaluate(String uri, String src) {
    Source source = Source.newBuilder(JS_LANGUAGE, src, uri).buildLiteral();

    try {
      context.eval(source);
    } catch (PolyglotException exc) {
      LOGGER.error("Failed to compile JS from {}:", uri, exc);
    } catch (Exception exc) {
      LOGGER.error("JS Evaluation error: ", exc);
    }
  }
}
