package net.arcadiusmc.delphiplugin.resource;

import com.google.common.base.Strings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;

@Getter @Setter
public class MeasuredFont {

  static final NamespacedKey DEFAULT_FONT_ID = NamespacedKey.fromString("minecraft:default");

  static final Codec<Key> KEY_CODEC = Codec.STRING
      .comapFlatMap(
          s -> {
            Key key = NamespacedKey.fromString(s);
            if (key == null) {
              return DataResult.error(() -> "Invalid key: '" + s + "'");
            }
            return DataResult.success(key);
          },
          Key::asString
      );

  static final Codec<Object2FloatMap<String>> CHAR_WIDTH_MAP = Codec.unboundedMap(Codec.STRING, Codec.FLOAT)
      .xmap(
          stringIntegerMap -> {
            Object2FloatMap<String> map = new Object2FloatOpenHashMap<>();
            stringIntegerMap.forEach((s, integer) -> {
              if (Strings.isNullOrEmpty(s)) {
                return;
              }
              map.put(s, integer.intValue());
            });
            return map;
          },
          char2IntMap -> char2IntMap
      );

  public static final Codec<MeasuredFont> CODEC = RecordCodecBuilder.create(instance -> {
    return instance
        .group(
            KEY_CODEC.optionalFieldOf("font-id", DEFAULT_FONT_ID)
                .forGetter(MeasuredFont::getFontId),

            Codec.INT.optionalFieldOf("priority", 0)
                .forGetter(MeasuredFont::getPriority),

            Codec.FLOAT.optionalFieldOf("bold-modifier", 0f)
                .forGetter(MeasuredFont::getBoldModifier),

            Codec.FLOAT.optionalFieldOf("height", 0f)
                .forGetter(MeasuredFont::getHeight),

            Codec.FLOAT.optionalFieldOf("descender-height", 0f)
                .forGetter(MeasuredFont::getDescenderHeight),

            CHAR_WIDTH_MAP.optionalFieldOf("char-widths", Object2FloatMaps.emptyMap())
                .forGetter(MeasuredFont::getSizeMap)
        )
        .apply(instance, (fontId, prio, boldMod, height, descender, charWidths) -> {
          MeasuredFont font = new MeasuredFont();
          font.setFontId(fontId);
          font.setPriority(prio);
          font.setBoldModifier(boldMod);
          font.setHeight(height);
          font.setDescenderHeight(descender);
          font.getSizeMap().putAll(charWidths);
          return font;
        });
  });

  private Key fontId = DEFAULT_FONT_ID;
  private int priority = 0;
  private boolean resourceLoaded = false;

  private final Object2FloatMap<String> sizeMap = new Object2FloatOpenHashMap<>();
  private float boldModifier;
  private float height;
  private float descenderHeight;

  public MeasuredFont() {
    sizeMap.defaultReturnValue(-1f);
  }
}
