package com.hhrpc.hhrpc.core.governance;

import java.util.Arrays;

/**
 * 环，作为滑动窗口的容器
 */
public class RingBuffer {

    private final int size;
    private final int[] ring;

    public RingBuffer(int size) {
        this.size = size;
        this.ring = new int[size];
    }

    public int sum() {
        return Arrays.stream(ring).sum();
    }

    public void inc(int index, int val) {
        ring[index % size] += val;
    }

    public void reset() {
        Arrays.fill(ring, 0);
    }

    /**
     * 前闭后开
     *
     * @param beginIndex
     * @param endIndex
     */
    public void reset(int beginIndex, int endIndex) {
        Arrays.fill(ring, beginIndex % size, endIndex % size, 0);
    }
}
