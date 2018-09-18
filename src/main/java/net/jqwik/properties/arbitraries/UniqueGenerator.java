package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import net.jqwik.*;
import net.jqwik.api.*;

public class UniqueGenerator<T> implements RandomGenerator<T> {
	private static final long MAX_MISSES = 10000;
	private final RandomGenerator<T> toFilter;
	private final Set<T> generatedValues = ConcurrentHashMap.newKeySet();

	public UniqueGenerator(RandomGenerator<T> toFilter) {
		this.toFilter = toFilter;
	}

	@Override
	public Shrinkable<T> next(Random random) {
		// TODO: Wrap Shrinkables so that there cannot be duplicate objects during shrinking either
		return nextUntilAccepted(random, toFilter::next);
	}

	@Override
	public String toString() {
		return String.format("Unique [%s]", toFilter);
	}

	private Shrinkable<T> nextUntilAccepted(Random random, Function<Random, Shrinkable<T>> fetchShrinkable) {
		long count = 0;
		while (true) {
			Shrinkable<T> next = fetchShrinkable.apply(random);
			if (generatedValues.contains(next.value())) {
				if (++count > MAX_MISSES) {
					throw new JqwikException(String.format("%s missed more than %s times.", toString(), MAX_MISSES));
				}
				continue;
			} else {
				generatedValues.add(next.value());
			}
			return next;
		}
	}

}