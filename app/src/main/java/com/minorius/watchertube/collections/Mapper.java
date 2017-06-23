package com.minorius.watchertube.collections;

public interface Mapper<E, T> {

	public E apply(T value);
}
