package net.arcadiusmc.delphi.parser;

import static net.arcadiusmc.delphi.parser.Token.COLON;
import static net.arcadiusmc.delphi.parser.Token.DOLLAR_SIGN;
import static net.arcadiusmc.delphi.parser.Token.DOT;
import static net.arcadiusmc.delphi.parser.Token.EQUALS;
import static net.arcadiusmc.delphi.parser.Token.HASHTAG;
import static net.arcadiusmc.delphi.parser.Token.ID;
import static net.arcadiusmc.delphi.parser.Token.SPACE;
import static net.arcadiusmc.delphi.parser.Token.SQUARE_CLOSE;
import static net.arcadiusmc.delphi.parser.Token.SQUARE_OPEN;
import static net.arcadiusmc.delphi.parser.Token.SQUIGLY;
import static net.arcadiusmc.delphi.parser.Token.STAR;
import static net.arcadiusmc.delphi.parser.Token.STRING;
import static net.arcadiusmc.delphi.parser.Token.UP_ARROW;
import static net.arcadiusmc.delphi.parser.Token.WALL;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.arcadiusmc.delphi.dom.selector.AttributedNode;
import net.arcadiusmc.delphi.dom.selector.AttributedNode.AttributeTest;
import net.arcadiusmc.delphi.dom.selector.AttributedNode.Operation;
import net.arcadiusmc.delphi.dom.selector.ClassNameFunction;
import net.arcadiusmc.delphi.dom.selector.IdFunction;
import net.arcadiusmc.delphi.dom.selector.PseudoClass;
import net.arcadiusmc.delphi.dom.selector.PseudoClassFunction;
import net.arcadiusmc.delphi.dom.selector.Selector;
import net.arcadiusmc.delphi.dom.selector.SelectorFunction;
import net.arcadiusmc.delphi.dom.selector.SelectorNode;
import net.arcadiusmc.delphi.dom.selector.TagNameFunction;
import net.arcadiusmc.delphi.parser.TokenStream.ParseMode;

public class Parser {

  @Getter
  protected final ParserErrors errors;
  protected final TokenStream stream;

  public Parser(StringBuffer in) {
    this.errors = new ParserErrors(in);
    this.stream = new TokenStream(in, errors);
  }

  public boolean hasNext() {
    return stream.hasNext();
  }

  public Token next() {
    return stream.next();
  }

  public Token peek() {
    return stream.peek();
  }

  public Token expect(int tokenType) {
    return stream.expect(tokenType);
  }

  public Selector selector() {
    stream.pushMode(ParseMode.TOKENS);
    stream.whitespaceMatters(true);

    List<SelectorNode> nodes = new ArrayList<>();
    List<SelectorFunction> functions = new ArrayList<>();

    while (true) {
      SelectorFunction node;
      Token p = peek();

      if (p.type() == STAR) {
        next();
        node = SelectorFunction.ALL;
      } else if (p.type() == DOT) {
        next();
        node = new ClassNameFunction(stream.expect(ID).value());
      } else if (p.type() == HASHTAG) {
        next();
        node = new IdFunction(stream.expect(ID).value());
      } else if (p.type() == ID) {
        next();
        node = new TagNameFunction(p.value());
      } else if (p.type() == SQUARE_OPEN) {
        node = attributed();
      } else if (p.type() == COLON) {
        node = pseudoClass();
      } else {
        break;
      }

      functions.add(node);

      if (peek().type() == SPACE) {
        pushFunctions(nodes, functions);
      }
    }

    if (!functions.isEmpty()) {
      pushFunctions(nodes, functions);
    }

    stream.popMode();
    stream.whitespaceMatters(false);

    SelectorNode[] nodeArr = nodes.toArray(SelectorNode[]::new);
    return new Selector(nodeArr);
  }

  private void pushFunctions(List<SelectorNode> nodes, List<SelectorFunction> functions) {
    SelectorFunction[] arr = functions.toArray(SelectorFunction[]::new);
    SelectorNode n = new SelectorNode(arr);
    nodes.add(n);
  }

  private PseudoClassFunction pseudoClass() {
    expect(COLON);
    Token id = expect(ID);

    PseudoClass pseudoClass = switch (id.value()) {
      case "hover" -> PseudoClass.HOVER;
      case "click", "active" -> PseudoClass.ACTIVE;
      case "enabled" -> PseudoClass.ENABLED;
      case "disabled" -> PseudoClass.DISABLED;
      case "root" -> PseudoClass.ROOT;

      default -> {
        errors.fatal(id.location(), "Invalid/unsupported pseudo class :%s", id.value());
        yield null;
      }
    };

    return new PseudoClassFunction(pseudoClass);
  }

  private AttributedNode attributed() {
    expect(SQUARE_OPEN);

    List<AttributeTest> tests = new ArrayList<>();

    while (true) {
      Token attrNameT = expect(ID);
      String attrValue;

      Token peek = peek();
      int ptype = peek.type();

      Operation op = switch (ptype) {
        case EQUALS -> Operation.EQUALS;
        case SQUIGLY -> Operation.CONTAINS_WORD;
        case WALL -> Operation.DASH_PREFIXED;
        case UP_ARROW -> Operation.STARTS_WITH;
        case DOLLAR_SIGN -> Operation.ENDS_WITH;
        case STAR -> Operation.CONTAINS_SUBSTRING;
        default -> Operation.HAS;
      };

      boolean expectValue;

      switch (op) {
        case CONTAINS_WORD:
        case DASH_PREFIXED:
        case STARTS_WITH:
        case ENDS_WITH:
        case CONTAINS_SUBSTRING:
          next();
          expect(EQUALS);
          expectValue = true;
          break;

        case EQUALS:
          next();
          expectValue = true;
          break;

        default:
          expectValue = false;
          break;
      }

      if (expectValue) {
        Token valT = expect(STRING);
        attrValue = valT.value();
      } else {
        attrValue = null;
      }

      expect(SQUARE_CLOSE);

      AttributeTest test = new AttributeTest(attrNameT.value(), op, attrValue);
      tests.add(test);

      if (peek().type() != SQUARE_OPEN) {
        break;
      } else {
        next();
      }
    }

    return new AttributedNode(tests);
  }
}
