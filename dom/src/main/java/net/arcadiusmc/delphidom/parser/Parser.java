package net.arcadiusmc.delphidom.parser;

import static net.arcadiusmc.delphidom.parser.Token.ANGLE_RIGHT;
import static net.arcadiusmc.delphidom.parser.Token.BRACKET_CLOSE;
import static net.arcadiusmc.delphidom.parser.Token.BRACKET_OPEN;
import static net.arcadiusmc.delphidom.parser.Token.COLON;
import static net.arcadiusmc.delphidom.parser.Token.COMMA;
import static net.arcadiusmc.delphidom.parser.Token.DOT;
import static net.arcadiusmc.delphidom.parser.Token.EQUALS;
import static net.arcadiusmc.delphidom.parser.Token.HASHTAG;
import static net.arcadiusmc.delphidom.parser.Token.ID;
import static net.arcadiusmc.delphidom.parser.Token.NUMBER;
import static net.arcadiusmc.delphidom.parser.Token.PLUS;
import static net.arcadiusmc.delphidom.parser.Token.SQUARE_CLOSE;
import static net.arcadiusmc.delphidom.parser.Token.SQUARE_OPEN;
import static net.arcadiusmc.delphidom.parser.Token.SQUIGLY;
import static net.arcadiusmc.delphidom.parser.Token.STAR;
import static net.arcadiusmc.delphidom.parser.Token.STRING;
import static net.arcadiusmc.delphidom.parser.Token.WHITESPACE;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import net.arcadiusmc.delphidom.parser.TokenStream.ParseMode;
import net.arcadiusmc.delphidom.selector.AttributedNode;
import net.arcadiusmc.delphidom.selector.AttributedNode.AttributeTest;
import net.arcadiusmc.delphidom.selector.AttributedNode.Operation;
import net.arcadiusmc.delphidom.selector.ClassNameFunction;
import net.arcadiusmc.delphidom.selector.Combinator;
import net.arcadiusmc.delphidom.selector.IdFunction;
import net.arcadiusmc.delphidom.selector.PseudoClass;
import net.arcadiusmc.delphidom.selector.PseudoClassFunction;
import net.arcadiusmc.delphidom.selector.PseudoFuncFunction;
import net.arcadiusmc.delphidom.selector.PseudoFunctions;
import net.arcadiusmc.delphidom.selector.Selector;
import net.arcadiusmc.delphidom.selector.SelectorFunction;
import net.arcadiusmc.delphidom.selector.SelectorGroup;
import net.arcadiusmc.delphidom.selector.SelectorNode;
import net.arcadiusmc.delphidom.selector.TagNameFunction;

public class Parser {

  @Getter
  protected final ParserErrors errors;
  protected final TokenStream stream;

  public Parser(StringBuffer in) {
    this.errors = new ParserErrors(in);
    this.stream = new TokenStream(in, errors);
  }

  public boolean matches(int... ttypes) {
    Token peek = peek();

    for (int ttype : ttypes) {
      if (peek.type() != ttype) {
        continue;
      }

      return true;
    }

    return false;
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

  public SelectorGroup selectorGroup() {
    Selector first = selector();
    List<Selector> list = new ArrayList<>();
    list.add(first);

    while (peek().type() == COMMA) {
      next();

      if (peek().type() == WHITESPACE) {
        next();
      }

      list.add(selector());
    }

    return new SelectorGroup(list.toArray(Selector[]::new));
  }

  public Selector selector() {
    stream.pushMode(ParseMode.SELECTOR);

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

      if (matches(WHITESPACE, PLUS, SQUIGLY, ANGLE_RIGHT)) {
        skipWhitespace();
        Combinator combinator = combinator();
        pushFunctions(nodes, functions, combinator);
        skipWhitespace();
      }
    }

    if (!functions.isEmpty()) {
      pushFunctions(nodes, functions, Combinator.DESCENDANT);
    }

    stream.popMode();

    SelectorNode[] nodeArr = nodes.toArray(SelectorNode[]::new);
    return new Selector(nodeArr);
  }

  private void skipWhitespace() {
    if (peek().type() == WHITESPACE) {
      next();
    }
  }

  private Combinator combinator() {
    // Assume white space has been skipped, we expect combinator character now
    return switch (peek().type()) {
      case PLUS -> {
        next();
        yield Combinator.DIRECT_SIBLING;
      }
      case SQUIGLY -> {
        next();
        yield Combinator.SIBLING;
      }
      case ANGLE_RIGHT -> {
        next();
        yield Combinator.PARENT;
      }

      default -> Combinator.DESCENDANT;
    };
  }

  private void pushFunctions(
      List<SelectorNode> nodes,
      List<SelectorFunction> functions,
      Combinator combinator
  ) {
    SelectorFunction[] arr = functions.toArray(SelectorFunction[]::new);
    SelectorNode n = new SelectorNode(combinator, arr);
    nodes.add(n);
    functions.clear();
  }

  private SelectorFunction pseudoClass() {
    expect(COLON);
    Token id = expect(ID);

    PseudoClass pseudoClass;

    switch (id.value()) {
      case "hover" -> pseudoClass = PseudoClass.HOVER;
      case "click", "active" -> pseudoClass = PseudoClass.ACTIVE;
      case "enabled" -> pseudoClass = PseudoClass.ENABLED;
      case "disabled" -> pseudoClass = PseudoClass.DISABLED;
      case "root" -> pseudoClass = PseudoClass.ROOT;
      case "first-child" -> pseudoClass = PseudoClass.FIRST_CHILD;
      case "last-child" -> pseudoClass = PseudoClass.LAST_CHILD;

      case "is" -> {
        expect(BRACKET_OPEN);
        SelectorGroup group = selectorGroup();
        expect(BRACKET_CLOSE);

        return new PseudoFuncFunction<>(PseudoFunctions.IS, group);
      }

      case "not" -> {
        expect(BRACKET_OPEN);
        SelectorGroup group = selectorGroup();
        expect(BRACKET_CLOSE);

        return new PseudoFuncFunction<>(PseudoFunctions.NOT, group);
      }

      case "nth-child" -> {
        expect(BRACKET_OPEN);
        Token indexToken = expect(NUMBER);
        float f = Float.parseFloat(indexToken.value());
        expect(BRACKET_CLOSE);

        return new PseudoFuncFunction<>(PseudoFunctions.NTH_CHILD, (int) f);
      }

      default -> {
        errors.fatal(id.location(), "Invalid/unsupported pseudo class :%s", id.value());
        pseudoClass = null;
      }
    };

    return new PseudoClassFunction(pseudoClass);
  }

  private AttributedNode attributed() {
    List<AttributeTest> tests = new ArrayList<>();

    while (peek().type() == SQUARE_OPEN) {
      next();

      Token attrNameT = expect(ID);
      String attrValue;

      Token peek = peek();
      int ptype = peek.type();

      Operation op = switch (ptype) {
        case EQUALS -> Operation.EQUALS;
        case Token.SQUIGLY -> Operation.CONTAINS_WORD;
        case Token.WALL -> Operation.DASH_PREFIXED;
        case Token.UP_ARROW -> Operation.STARTS_WITH;
        case Token.DOLLAR_SIGN -> Operation.ENDS_WITH;
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
    }

    return new AttributedNode(tests);
  }
}
