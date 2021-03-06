package net.jqwik.engine.execution.lifecycle;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

@AddLifecycleHook(IncrementBefore.class)
@AddLifecycleHook(IncrementPropagatedBefore.class)
class BeforeContainerHookTests {
	static int before = 0;
	static int propagatedBefore = 0;

	@Example
	void beforeShouldBe1() {
		assertThat(before).isEqualTo(1);
	}

	@Example
	void beforeShouldStillBe1() {
		assertThat(before).isEqualTo(1);
	}

	@Group
	class NestedTests {

		@Example
		void beforeShouldBe1InNested() {
			assertThat(before).isEqualTo(1);
		}

		@Example
		void propagatedBeforeShouldBe2() {
			assertThat(propagatedBefore).isEqualTo(2);
		}

	}
}

class IncrementBefore implements BeforeContainerHook {

	@Override
	public void beforeContainer(ContainerLifecycleContext context) {
		assertThat(CurrentTestDescriptor.get()).isInstanceOf(TestDescriptor.class);
		BeforeContainerHookTests.before++;
		assertThat(context.optionalContainerClass()).isPresent();
		assertThat(context.optionalContainerClass().get()).isEqualTo(BeforeContainerHookTests.class);
	}
}

class IncrementPropagatedBefore implements BeforeContainerHook {

	@Override
	public void beforeContainer(ContainerLifecycleContext context) {
		BeforeContainerHookTests.propagatedBefore++;
		assertThat(context.optionalContainerClass()).isPresent();
		assertThat(context.optionalContainerClass().get())
			.isIn(BeforeContainerHookTests.class, BeforeContainerHookTests.NestedTests.class);
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

}

