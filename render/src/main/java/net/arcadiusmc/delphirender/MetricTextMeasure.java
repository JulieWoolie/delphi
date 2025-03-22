package net.arcadiusmc.delphirender;

import org.jetbrains.annotations.NotNull;

public class MetricTextMeasure extends TextMeasure {

  private final FontMeasureCallback metrics;
  private final FontMeasureOutput output;

  public MetricTextMeasure(FontMeasureCallback metrics) {
    this.metrics = metrics;
    this.output = new FontMeasureOutput();
  }

  @Override
  public void component(@NotNull String text) {
    int idx = 0;

    text = removeColorCodes(text);

    while (idx < text.length()) {
      boolean foundMatchingChar = metrics.measureNextChar(text, style, idx, output);

      char ch = text.charAt(idx);
      if (ch == '\n') {
        idx++;
        onLineBreak();
        continue;
      }

      if (lineChars > 0) {
        incWidth(1f);
      }

      lineChars++;

      if (!foundMatchingChar) {
        idx++;

        // yeah idk man, just default it
        charHeight(8f, 2f);
        incWidth(6f);

        continue;
      }

      incWidth(output.width);
      charHeight(output.height, output.descenderHeight);

      idx += output.consumedChars;
      lineChars += output.consumedChars;
    }
  }
}
