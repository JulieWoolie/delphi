package net.arcadiusmc.delphiplugin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.arcadiusmc.delphidom.Loggers;
import org.slf4j.Logger;

public class ViewScheduler {

  private static final Logger LOGGER = Loggers.getLogger();

  static final byte TT_DELAYED = 0;
  static final byte TT_REPEATING = 1;

  final List<ViewTask> tasks = new ObjectArrayList<>(25);
  int nextId = 0;

  boolean stopped = false;

  int scheduleLater(long interval, Runnable runnable) {
    Objects.requireNonNull(runnable, "Null task");
    if (interval < 1) {
      throw new IllegalArgumentException("Delay cannot be under 1 tick");
    }

    ViewTask task = new ViewTask(TT_DELAYED, runnable, interval);
    task.untilExec = interval;

    return addTask(task);
  }

  int scheduleRepeating(long delay, long interval, Runnable runnable) {
    Objects.requireNonNull(runnable, "Null task");
    if (delay < 0) {
      throw new IllegalArgumentException("Delay cannot be under 0 ticks");
    }
    if (interval < 1) {
      throw new IllegalArgumentException("Interval cannot be under 1 tick");
    }

    ViewTask task = new ViewTask(TT_REPEATING, runnable, interval);
    task.untilExec = delay;

    return addTask(task);
  }

  int addTask(ViewTask task) {
    task.id = nextId++;
    tasks.add(task);
    return task.id;
  }

  boolean cancelTask(int id) {
    return tasks.removeIf(task -> task.id == id);
  }

  void tick() {
    if (stopped) {
      return;
    }

    Iterator<ViewTask> it = tasks.iterator();
    while (it.hasNext()) {
      var task = it.next();

      if (task.untilExec > 0) {
        task.untilExec--;
        continue;
      }

      task.runSafe();

      if (stopped) {
        return;
      }

      if (task.type == TT_REPEATING) {
        task.untilExec = task.interval;
        continue;
      }

      it.remove();
    }
  }

  static class ViewTask {
    final byte type;
    final Runnable callback;
    final long interval;

    int id;
    long untilExec;

    public ViewTask(byte type, Runnable callback, long interval) {
      this.type = type;
      this.callback = callback;
      this.interval = interval;
    }

    void runSafe() {
      try {
        callback.run();
      } catch (Exception e) {
        LOGGER.error("Error executing scheduled task (id: {}) {}", id, callback, e);
      }
    }
  }
}
