package net.arcadiusmc.delphidom.parser;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.arcadiusmc.delphidom.selector.AttributedNode;
import net.arcadiusmc.delphidom.selector.AttributedNode.AttributeTest;
import net.arcadiusmc.delphidom.selector.AttributedNode.Operation;
import net.arcadiusmc.delphidom.selector.ClassNameFunction;
import net.arcadiusmc.delphidom.selector.IdFunction;
import net.arcadiusmc.delphidom.selector.PseudoClass;
import net.arcadiusmc.delphidom.selector.PseudoClassFunction;
import net.arcadiusmc.delphidom.selector.Selector;
import net.arcadiusmc.delphidom.selector.SelectorFunction;
import net.arcadiusmc.delphidom.selector.SelectorNode;
import net.arcadiusmc.delphidom.selector.TagNameFunction;
import net.arcadiusmc.delphidom.parser.TokenStream.ParseMode;

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

  public Token softExpect(int tokenType) {
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

      if (p.type() == Token.STAR) {
        next();
        node = SelectorFunction.ALL;
      } else if (p.type() == Token.DOT) {
        next();
        node = new ClassNameFunction(stream.expect(Token.ID).value());
      } else if (p.type() == Token.HASHTAG) {
        next();
        node = new IdFunction(stream.expect(Token.ID).value());
      } else if (p.type() == Token.ID) {
        next();
        node = new TagNameFunction(p.value());
      } else if (p.type() == Token.SQUARE_OPEN) {
        node = attributed();
      } else if (p.type() == Token.COLON) {
        node = pseudoClass();
      } else {
        break;
      }

      functions.add(node);

      if (peek().type() == Token.WHITESPACE) {
        pushFunctions(nodes, functions);
        next();
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
    functions.clear();
  }

  private PseudoClassFunction pseudoClass() {
    expect(Token.COLON);
    Token id = expect(Token.ID);

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
    List<AttributeTest> tests = new ArrayList<>();

    while (peek().type() == Token.SQUARE_OPEN) {
      next();

      Token attrNameT = expect(Token.ID);
      String attrValue;

      Token peek = peek();
      int ptype = peek.type();

      Operation op = switch (ptype) {
        case Token.EQUALS -> Operation.EQUALS;
        case Token.SQUIGLY -> Operation.CONTAINS_WORD;
        case Token.WALL -> Operation.DASH_PREFIXED;
        case Token.UP_ARROW -> Operation.STARTS_WITH;
        case Token.DOLLAR_SIGN -> Operation.ENDS_WITH;
        case Token.STAR -> Operation.CONTAINS_SUBSTRING;
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
          expect(Token.EQUALS);
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
        Token valT = expect(Token.STRING);
        attrValue = valT.value();
      } else {
        attrValue = null;
      }

      expect(Token.SQUARE_CLOSE);

      AttributeTest test = new AttributeTest(attrNameT.value(), op, attrValue);
      tests.add(test);
    }

    return new AttributedNode(tests);
  }
}
