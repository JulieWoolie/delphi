package com.juliewoolie.delphiplugin;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import com.juliewoolie.delphi.PlayerSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class PlayerSetImpl implements PlayerSet {

  private final ObjectOpenHashSet<Player> backing = new ObjectOpenHashSet<>();
  private PageView view;

  @Override
  public boolean isServerPlayerSet() {
    return false;
  }

  @Override
  public int size() {
    return backing.size();
  }

  @Override
  public boolean isEmpty() {
    return backing.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return backing.contains(o);
  }

  @NotNull
  @Override
  public Iterator<Player> iterator() {
    return new WrappedIter(backing.iterator());
  }

  @NotNull
  @Override
  public Object[] toArray() {
    return backing.toArray();
  }

  @NotNull
  @Override
  public <T> T[] toArray(@NotNull T[] a) {
    return backing.toArray(a);
  }

  @Override
  public boolean add(Player player) {
    if (!backing.add(player)) {
      return false;
    }
    if (view == null) {
      return true;
    }

    view.onPlayerAdded(player);
    return true;
  }

  @Override
  public boolean remove(Object o) {
    if (!backing.remove(o)) {
      return false;
    }
    if (view == null) {
      return true;
    }

    view.onPlayerRemoved((Player) o);
    return true;
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> c) {
    return backing.containsAll(c);
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends Player> c) {
    boolean res = false;
    for (Player player : c) {
      res |= add(player);
    }
    return res;
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> c) {
    boolean res = false;
    Iterator<Player> it = iterator();

    while (it.hasNext()) {
      var n = it.next();
      if (c.contains(n)) {
        continue;
      }

      it.remove();
      res = true;
    }

    return res;
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> c) {
    boolean res = false;
    for (Object o : c) {
      res |= remove(o);
    }
    return res;
  }

  @Override
  public void clear() {
    if (isEmpty()) {
      return;
    }

    if (view != null) {
      for (Player player : backing) {
        view.onPlayerRemoved(player);
      }
    }

    backing.clear();
  }

  class WrappedIter implements ObjectIterator<Player> {

    private final ObjectIterator<Player> backing;
    private Player current;

    public WrappedIter(ObjectIterator<Player> backing) {
      this.backing = backing;
    }

    @Override
    public boolean hasNext() {
      return backing.hasNext();
    }

    @Override
    public Player next() {
      return current = backing.next();
    }

    @Override
    public void remove() {
      if (current == null) {
        throw new IllegalStateException();
      }
      PlayerSetImpl.this.remove(current);
    }
  }
}
