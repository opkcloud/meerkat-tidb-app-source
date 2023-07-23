package com.meerkat.mytidbapp.util;

import lombok.Data;

/**
 * 三元组
 *
 * @param <T>
 * @param <R>
 * @param <U>
 * @author jiayuan.qu
 */
//@Value
@Data
public class Tuple3<T, R, U> {

    /**
     * 第一个元素
     */
    T first;

    /**
     * 第二个元素
     */
    R second;

    /**
     * 第三个元素
     */
    U third;

    public Tuple3() {
    }

    public Tuple3(T first, R second, U third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <T, R, U> Tuple3<T, R, U> of(T t, R r, U u) {
        return new Tuple3<>(t, r, u);
    }
}
