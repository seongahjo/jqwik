package net.jqwik.engine;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class ArbitraryDelegator<T> extends ArbitraryDecorator<T> {

	private final Arbitrary<T> delegate;

	public ArbitraryDelegator(Arbitrary<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Arbitrary<T> arbitrary() {
		return delegate;
	}
}
