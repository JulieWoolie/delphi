package net.arcadiusmc.delphi.dom.scss.func;

import net.arcadiusmc.delphi.parser.ParserErrors;

public interface ScssFunction {

  Object evaluate(String functionName, ArgsParser parser, ParserErrors errors);
}
