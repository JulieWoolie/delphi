package net.arcadiusmc.chimera.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.arcadiusmc.chimera.parse.ast.Expression;
import net.arcadiusmc.chimera.parse.ast.SelectorExpression;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.slf4j.event.Level;

public class SyntaxTests {

  static final String JSON_SUFFIX = ".test.json";
  static final String SCSS_SUFFIX = ".test.scss";
  static final Filter<Path> TEST_JSON_FILTER = entry -> {
    if (Files.isDirectory(entry)) {
      return true;
    }

    String p = entry.toString();
    return p.endsWith(JSON_SUFFIX) || p.endsWith(SCSS_SUFFIX);
  };

  @TestFactory
  Iterable<DynamicTest> generateTests() throws Exception {
    String resourceUrl = getClass().getClassLoader().getResource("syntax-tests/").getFile().substring(1);
    Path resourcesDirectory = Path.of(resourceUrl);

    List<DynamicTest> tests = new ObjectArrayList<>();

    loadFromDir(resourcesDirectory, resourcesDirectory, l -> {
      DynamicTest t = DynamicTest.dynamicTest(l.displayName, l.fileName.toUri(), l);
      tests.add(t);
    });

    return tests;
  }

  private void loadFromDir(Path root, Path dir, Consumer<LoadedSyntaxTest> consumer)
      throws IOException
  {
    String relPath = root.relativize(dir).toString().replace("\\", "/");

    if (!relPath.isEmpty()) {
      relPath += "/";
    }

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, TEST_JSON_FILTER)) {
      for (Path path : stream) {
        if (Files.isDirectory(path)) {
          loadFromDir(root, path, consumer);
          continue;
        }

        LoadedSyntaxTest test = new LoadedSyntaxTest();
        String fname = path.getFileName().toString();

        test.displayName = relPath + fname
            .replace(SCSS_SUFFIX, "")
            .replace(JSON_SUFFIX, "");

        test.fileName = path;

        if (fname.endsWith(SCSS_SUFFIX)) {
          test.parserCall = "execute";
          test.scssSource = Files.readString(path, StandardCharsets.UTF_8);
        } else {
          loadFromJson(test, fname, dir, path);
        }

        consumer.accept(test);
      }
    }
  }

  private void loadFromJson(LoadedSyntaxTest test, String fname, Path dir, Path path)
      throws IOException
  {
    String jsonContent = Files.readString(path, StandardCharsets.UTF_8);
    JsonObject obj = JsonParser.parseString(jsonContent).getAsJsonObject();

    test.errors = loadErrors(obj);

    if (obj.has("should-succeed")) {
      test.expectedResult = obj.get("should-succeed").getAsBoolean();
    } else {
      test.expectedResult = null;
    }

    if (obj.has("parser-call")) {
      test.parserCall = obj.get("parser-call").getAsString();
    } else {
      test.parserCall = "";
    }

    if (obj.has("expected")) {
      test.resultString = obj.get("expected").getAsString();
    } else {
      test.resultString = null;
    }

    if (obj.has("scss")) {
      test.scssSource = obj.get("scss").getAsString();
    } else {
      String scssName = fname.replace(JSON_SUFFIX, ".scss");

      Path p = dir.resolve(scssName);
      if (!Files.exists(p)) {
        throw new FileNotFoundException(p.toString());
      }

      test.scssSource = Files.readString(p, StandardCharsets.UTF_8);
    }
  }

  private ExpectedError[] loadErrors(JsonObject object) {
    if (!object.has("expected-outputs")) {
      return new ExpectedError[0];
    }

    JsonArray array = object.getAsJsonArray("expected-outputs");
    ExpectedError[] errors = new ExpectedError[array.size()];

    for (int i = 0; i < array.size(); i++) {
      JsonObject json = array.get(i).getAsJsonObject();
      ExpectedError err = new ExpectedError();

      if (json.has("level")) {
        String levelName = json.get("level").getAsString();
        err.level = switch (levelName) {
          case "error" -> Level.ERROR;
          case "warn" -> Level.WARN;
          case "info" -> Level.INFO;
          default -> null;
        };
      }

      if (json.has("message")) {
        err.message = json.get("message").getAsString();
      }

      if (json.has("line")) {
        err.line = json.get("line").getAsInt();
      }
      if (json.has("column")) {
        err.column = json.get("column").getAsInt();
      }

      errors[i] = err;
    }

    return errors;
  }

  static class ExpectedError {
    Level level;
    String message;
    Integer line;
    Integer column;

    void validate(ChimeraError error) {
      if (!matches(level, error.getLevel())) {
        validationFail("message log level", error.getLevel(), level, error);
      }

      if (!matches(message, error.getMessage())) {
        validationFail("message", error.getMessage(), message, error);
      }

      Location location = error.getLocation();
      int line = location == null ? -1 : location.line();
      int col = location == null ? -1 : location.column();

      if (!matches(this.line, line)) {
        validationFail("location.line", line, this.line, error);
      }
      if (!matches(this.column, col)) {
        validationFail("location.column", col, this.column, error);
      }
    }

    void validationFail(String name, Object found, Object expected, ChimeraError error) {
      String message = "Unexpected output %s: [[%s]], expected [[%s]]".formatted(name, found, expected);
      fail(message, new ChimeraException(error));
    }

    boolean matches(Object predicate, Object object) {
      if (predicate == null) {
        return true;
      }
      return Objects.equals(predicate, object);
    }
  }

  static class LoadedSyntaxTest implements Executable {

    String displayName = "test";
    Boolean expectedResult = null;
    Path fileName;

    String parserCall;
    String resultString;

    String scssSource = "";

    int outputIdx = 0;
    ExpectedError[] errors = null;

    public void execute() throws Exception {
      ChimeraParser parser = new ChimeraParser(scssSource);
      parser.getErrors().setSourceName(displayName);

      parser.getErrors().setListener(error -> {
        if (errors == null) {
          if (error.getLevel() == Level.ERROR) {
            throw new ChimeraException(error);
          }

          System.out.println(error.getFormattedError());
          return;
        }

        int idx = outputIdx++;
        if (idx >= errors.length) {
          fail("Unexpected output", new ChimeraException(error));
        }

        ExpectedError err = errors[idx];
        err.validate(error);
      });

      Object output;

      try {
        switch (parserCall) {
          case "" -> output = parser.stylesheet();

          case "execute" -> {
            var sheet = parser.stylesheet();

            ChimeraContext ctx = parser.createContext();
            ctx.setIgnoringAsserts(false);

            Interpreter intr = new Interpreter(ctx, Scope.createTopLevel());

            output = sheet.visit(intr);
          }

          case "selector" -> {
            SelectorExpression selector = parser.selector();
            CompilerErrors errors = parser.getErrors();
            output = selector.compile(errors);
          }

          case "expr" -> {
            Expression expr = parser.expr();

            ChimeraContext ctx = parser.createContext();
            ctx.setIgnoringAsserts(false);

            Interpreter intr = new Interpreter(ctx, Scope.createTopLevel());

            output = expr.visit(intr);
          }
          case "statement" -> output = parser.statement();

          default -> {
            output = null;
            fail("Invalid parser call: " + parserCall);
          }
        }

        System.out.printf("Ran test \"%s\"\n", displayName);

        if (resultString != null) {
          String outputStr = String.valueOf(output);
          assertEquals(resultString, outputStr, "Output did not match expected");
        }

        if (expectedResult != null) {
          if (expectedResult) {
            return;
          }

          fail("Passed when expected to fail");
        }
      } catch (ChimeraException exception) {
        if (expectedResult != null) {
          if (!expectedResult) {
            return;
          }
        }

        fail(exception);
      }
    }
  }
}
