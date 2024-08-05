package net.arcadiusmc.delphiplugin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public final class ItemCodec {
  private ItemCodec() {}

  public static final Codec<ItemStack> NMS_CODEC = new Codec<>() {
    @Override
    public <T> DataResult<Pair<ItemStack, T>> decode(DynamicOps<T> ops, T input) {
      RegistryOps<T> regOps = DedicatedServer.getServer()
          .registryAccess()
          .createSerializationContext(ops);

      return net.minecraft.world.item.ItemStack.CODEC.decode(regOps, input)
          .map(p -> Pair.of(CraftItemStack.asBukkitCopy(p.getFirst()), p.getSecond()));
    }

    @Override
    public <T> DataResult<T> encode(ItemStack input, DynamicOps<T> ops, T prefix) {
      RegistryOps<T> regOps = DedicatedServer.getServer()
          .registryAccess()
          .createSerializationContext(ops);

      return net.minecraft.world.item.ItemStack.CODEC
          .encode(CraftItemStack.asNMSCopy(input), regOps, prefix);
    }
  };
}
