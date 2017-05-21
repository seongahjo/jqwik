package net.jqwik.newArbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.stream.*;

public class NSingleValueShrinkerTests {

	@Example
	void unshrinkableValueIsShrinkedToItself() {
		NShrinkable<String> unshrinkable = NShrinkableValue.unshrinkable("hello");

		MockFalsifier<String> falsifier = MockFalsifier.falsifyAll();
		NSingleValueShrinker<String> singleValueShrinker = new NSingleValueShrinker<>(unshrinkable);

		Assertions.assertThat(singleValueShrinker.shrink(falsifier)).isEqualTo("hello");
	}

	@Example
	void shrinkSingletonShrinkSetToFalsifiedValueWithLowestDistance() {
		NShrinker<Integer> integerNShrinker = new NShrinker<Integer>() {
			@Override
			public Set<Integer> shrink(Integer value) {
				return Collections.singleton(value - 1);
			}

			@Override
			public int distance(Integer value) {
				return value;
			}
		};
		NShrinkable<Integer> shrinkable = new NShrinkableValue<Integer>(10, integerNShrinker);
		MockFalsifier<Integer> falsifier = MockFalsifier.falsifyWhen(anInt -> anInt < 3);
		NSingleValueShrinker<Integer> singleValueShrinker = new NSingleValueShrinker<>(shrinkable);
		Assertions.assertThat(singleValueShrinker.shrink(falsifier)).isEqualTo(3);
	}

	@Example
	void shrinkMultiShrinkSetToFalsifiedValueWithLowestDistance() {
		List<NShrinkable<Character>> chars = "hello this is a longer sentence." //
			.chars() //
			.mapToObj(e -> NShrinkableValue.unshrinkable((char) e)) //
			.collect(Collectors.toList());

		NShrinkable<String> shrinkable = NContainerShrinkable.stringFromChars(chars);
		MockFalsifier<String> falsifier = MockFalsifier.falsifyWhen(aString -> aString.length() < 3 || !aString.startsWith("h"));
		NSingleValueShrinker<String> singleValueShrinker = new NSingleValueShrinker<>(shrinkable);
		Assertions.assertThat(singleValueShrinker.shrink(falsifier)).isEqualTo("hel");
	}
}
