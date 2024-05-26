package com.hhrpc.hhrpc.core.governance;

/**
 * 滑动的时间窗口
 */
public class SlidingTimeWindow {

    private final int size;
    private static final int DEFAULT_SIZE = 30;
    private final RingBuffer ringBuffer;

    // 上次异常的时间
    private long preTime = -1;
    // 当前异常的时间
    private long currentTime = -1;
    private int currentIndex = 0;

    private int sum;

    public SlidingTimeWindow() {
        this(DEFAULT_SIZE);
    }

    public SlidingTimeWindow(int size) {
        this.size = size;
        this.ringBuffer = new RingBuffer(size);
    }

    public void record(long tms) {
        // 毫秒换算成秒
        long ts = tms / 1000;
        if (preTime == -1) {
            initRing(ts);
        } else if (ts > currentTime && ts < currentTime + size) {
            int offset = (int) (ts - currentTime);
            int beginIndex = currentIndex + 1;
            int endIndex = beginIndex + offset;
            this.ringBuffer.reset(beginIndex, endIndex);
            this.currentIndex = (currentIndex + offset) % size;
            this.ringBuffer.inc(currentIndex, 1);
            this.preTime = currentTime;
            this.currentTime = ts;
        } else {
            this.ringBuffer.reset();
            initRing(ts);
        }
        this.sum = ringBuffer.sum();
    }

    private void initRing(long ts) {
        this.preTime = ts;
        this.currentTime = ts;
        this.currentIndex = 0;
        this.ringBuffer.inc(currentIndex, 1);
    }

    public int getSum() {
        return sum;
    }
}
