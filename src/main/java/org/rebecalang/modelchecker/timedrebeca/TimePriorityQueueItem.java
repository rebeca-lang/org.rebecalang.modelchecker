package org.rebecalang.modelchecker.timedrebeca;

public class TimePriorityQueueItem<T> implements Comparable<TimePriorityQueueItem> {
    private int time;
    private T item;

    public TimePriorityQueueItem(int time, T item) {
        super();
        this.time = time;
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public int compareTo(TimePriorityQueueItem timePriorityQueueItem) {
        return -Integer.compare(timePriorityQueueItem.time, this.time);
    }
}
