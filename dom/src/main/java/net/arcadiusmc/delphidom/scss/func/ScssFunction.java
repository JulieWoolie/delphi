package net.arcadiusmc.delphidom.scss.func;

import net.arcadiusmc.delphidom.parser.ParserErrors;

public interface ScssFunction {

  Object evaluate(String functionName, ArgsParser parser, ParserErrors errors);
}
