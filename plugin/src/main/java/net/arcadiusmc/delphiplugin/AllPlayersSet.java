package net.arcadiusmc.delphiplugin;

import java.util.Collection;
import java.util.Iterator;
import net.arcadiusmc.delphi.PlayerSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AllPlayersSet implements PlayerSet {

  private final Collection<Player> playerList;

  public AllPlayersSet() {
    this.playerList = (Collection<Player>) Bukkit.getOnlinePlayers();
  }

  @Override
  public boolean isServerPlayerSet() {
    return true;
  }

  @Override
  public int size() {
    return playerList.size();
  }

  @Override
  public boolean isEmpty() {
    return playerList.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return playerList.contains(o);
  }

  @NotNull
  @Override
  public Iterator<Player> iterator() {
    return playerList.iterator();
  }

  @NotNull
  @Override
  public Object[] toArray() {
    return playerList.toArray();
  }

  @NotNull
  @Override
  public <T> T[] toArray(@NotNull T[] a) {
    return playerList.toArray(a);
  }

  @Override
  public boolean add(Player player) {
    throw new UnsupportedOperationException("add()");
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException("remove()");
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> c) {
    return playerList.contains(c);
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends Player> c) {
    throw new UnsupportedOperationException("addAll()");
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> c) {
    throw new UnsupportedOperationException("retainAll()");
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> c) {
    throw new UnsupportedOperationException("removeAll()");
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("clear()");
  }
}
