package com.minorius.watchertube.collections;

public interface Function<E, T> {

	public E apply(E predResult, T value);
}
