package net.arcadiusmc.delphidom;

import com.google.common.base.Strings;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ClassList implements List<String> {

  final DelphiElement element;
  final List<String> strings;

  public ClassList(DelphiElement element, List<String> strings) {
    this.element = element;
    this.strings = strings;
  }

  public static void copyFromElement(ClassList list) {
    list.strings.clear();

    // Add all existing classes
    String className = list.element.getClassName();
    if (!Strings.isNullOrEmpty(className)) {
      String[] names = className.split("\\s+");
      Collections.addAll(list.strings, names);
    }
  }

  @Override
  public int size() {
    return strings.size();
  }

  @Override
  public boolean isEmpty() {
    return strings.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return strings.contains(o);
  }

  @NotNull
  @Override
  public Iterator<String> iterator() {
    return listIterator();
  }

  @NotNull
  @Override
  public Object[] toArray() {
    return strings.toArray();
  }

  @NotNull
  @Override
  public <T> T[] toArray(@NotNull T[] a) {
    return strings.toArray(a);
  }

  @Override
  public boolean add(String s) {
    strings.add(s);
    element.classListChanged();
    return true;
  }

  @Override
  public boolean remove(Object o) {
    if (!strings.remove(o)) {
      return false;
    }

    element.classListChanged();
    return true;
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> c) {
    return strings.containsAll(c);
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends String> c) {
    return addAll(size(), c);
  }

  @Override
  public boolean addAll(int index, @NotNull Collection<? extends String> c) {
    boolean res = false;
    int idx = index;

    for (String s : c) {
      strings.add(idx, s);

      idx++;
      res = true;
    }

    if (!res) {
      return false;
    }

    element.classListChanged();
    return true;
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> c) {
    if (!strings.removeAll(c)) {
      return false;
    }

    element.classListChanged();
    return true;
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> c) {
    if (!strings.retainAll(c)) {
      return false;
    }

    element.classListChanged();
    return true;
  }

  @Override
  public void clear() {
    boolean change = !isEmpty();
    strings.clear();

    if (!change) {
      return;
    }

    element.classListChanged();
  }

  @Override
  public String get(int index) {
    Objects.checkIndex(index, strings.size());
    return strings.get(index);
  }

  @Override
  public String set(int index, String element) {
    Objects.checkIndex(index, strings.size());
    String old = strings.set(index, element);

    if (old.equals(element)) {
      return old;
    }

    this.element.classListChanged();
    return old;
  }

  @Override
  public void add(int index, String element) {
    strings.add(index, element);
    this.element.classListChanged();
  }

  @Override
  public String remove(int index) {
    Objects.checkIndex(index, strings.size());
    String removed = strings.remove(index);
    element.classListChanged();
    return removed;
  }

  @Override
  public int indexOf(Object o) {
    return strings.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return strings.lastIndexOf(o);
  }

  @NotNull
  @Override
  public ListIterator<String> listIterator() {
    return new ListIterWrapper(strings.listIterator());
  }

  @NotNull
  @Override
  public ListIterator<String> listIterator(int index) {
    Objects.checkIndex(index, strings.size());
    return new ListIterWrapper(strings.listIterator(index));
  }

  @NotNull
  @Override
  public List<String> subList(int fromIndex, int toIndex) {
    Objects.checkFromToIndex(fromIndex, toIndex, strings.size());
    List<String> sublist = strings.subList(fromIndex, toIndex);
    return new ClassList(element, sublist);
  }

  class ListIterWrapper implements ListIterator<String> {

    final ListIterator<String> base;

    public ListIterWrapper(ListIterator<String> base) {
      this.base = base;
    }

    @Override
    public boolean hasNext() {
      return base.hasNext();
    }

    @Override
    public String next() {
      return base.next();
    }

    @Override
    public boolean hasPrevious() {
      return base.hasPrevious();
    }

    @Override
    public String previous() {
      return base.previous();
    }

    @Override
    public int nextIndex() {
      return base.nextIndex();
    }

    @Override
    public int previousIndex() {
      return base.previousIndex();
    }

    @Override
    public void remove() {
      base.remove();
      element.classListChanged();
    }

    @Override
    public void set(String s) {
      base.set(s);
      element.classListChanged();
    }

    @Override
    public void add(String s) {
      base.add(s);
      element.classListChanged();
    }
  }
}
