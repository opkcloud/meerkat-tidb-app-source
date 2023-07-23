package com.meerkat.mytidbapp.util;

import java.util.Objects;

/**
 * 二元组
 *
 * @param <T> 第一个元素
 * @param <R> 第二个元素
 * @author jiayuan
 * @since 1.0.0
 */
public class Tuple2<T, R> {

    /**
     * 第一个元素
     */
    T first;

    /**
     * 第二个元素
     */
    R second;

    Tuple2() {
    }

    Tuple2(T t, R r) {
        this.first = t;
        this.second = r;
    }

    public T getFirst() {
        return first;
    }

    public R getSecond() {
        return second;
    }

    /**
     * @param first  第一个元素
     * @param second 第二个元素
     * @param <T>    第一个元素类
     * @param <R>    第二个元素类
     * @return 初始化后的二元组
     */
    public static <T, R> Tuple2<T, R> of(T first, R second) {
        return new Tuple2<>(first, second);
    }

    public String toString() {
        return "Tuple2(first=" + this.getFirst() + ", second=" + this.getSecond() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        return Objects.equals(first, tuple2.first) && Objects.equals(second, tuple2.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
