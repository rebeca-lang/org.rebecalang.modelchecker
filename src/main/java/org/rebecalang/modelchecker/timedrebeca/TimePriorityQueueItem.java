package org.rebecalang.modelchecker.timedrebeca;

public class TimePriorityQueueItem implements Comparable<TimePriorityQueueItem> {
    private int time;
    private Object item;

    public TimePriorityQueueItem(int time, Object item) {
        super();
        this.time = time;
        this.item = item;
    }

    public Object getItem() {
        return item;
    }

    public int compareTo(TimePriorityQueueItem timePriorityQueueItem) {
        return -Integer.compare(timePriorityQueueItem.time, this.time);
    }
}
