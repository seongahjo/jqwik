package net.jqwik.properties;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.support.*;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("unchecked")
@Group
class PropertyMethodDataResolverTests {

	@Group
	class SingleParameter {
		@Example
		void findStringGeneratorByName() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "string");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("1"),
				Tuple.of("2"),
				Tuple.of("3")
			);
		}

		@Example
		void findStringGeneratorByMethodName() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "stringByMethodName");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("4"),
				Tuple.of("5"),
				Tuple.of("6")
			);
		}

		@Example
		void findGeneratorByMethodNameOutsideGroup() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.NestedNamedProviders.class);
			Method parameter = getMethod(NamedResolvers.NestedNamedProviders.class, "nestedStringByMethodName");

			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);
			Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("4"),
				Tuple.of("5"),
				Tuple.of("6")
			);
		}

		@Example
		void findGeneratorByNameOutsideGroup() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.NestedNamedProviders.class);
			Method parameter = getMethod(NamedResolvers.NestedNamedProviders.class, "nestedString");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple1<String>> data = (Iterable<Tuple1<String>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("1"),
				Tuple.of("2"),
				Tuple.of("3")
			);
		}

		@Example
		void namedDataGeneratorNotFound() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "otherString");
			assertThat(resolver.forMethod(parameter)).isEmpty();
		}

	}

	@Group
	class MoreThanOneParameter {
		@Example
		void twoParameters() {
			PropertyMethodDataResolver resolver = getResolver(NamedResolvers.class);
			Method parameter = getMethod(NamedResolvers.class, "twoParameters");
			Optional<Iterable<? extends Tuple>> optionalData = resolver.forMethod(parameter);

			Iterable<Tuple2<String, Integer>> data = (Iterable<Tuple2<String, Integer>>) optionalData.get();
			assertThat(data).containsExactly(
				Tuple.of("4", 4),
				Tuple.of("5", 5),
				Tuple.of("6", 6)
			);
		}
	}


	private class NamedResolvers {
		@Property
		@DataFrom("aString")
		boolean string(@ForAll String aString) {
			return true;
		}

		@Data("aString")
		Iterable<Tuple1<String>>  aString() {
			return Table.of("1", "2", "3");
		}

		@Property
		@DataFrom("otherString")
		boolean otherString(@ForAll String aString) {
			return true;
		}

		@Property
		@DataFrom("byMethodName")
		boolean stringByMethodName(String aString) {
			return true;
		}

		@Data
		Iterable<? extends Tuple> byMethodName() {
			return Table.of(
				Tuple.of("4"),
				Tuple.of("5"),
				Tuple.of("6")
			);
		}

		@Property
		@DataFrom("twos")
		boolean twoParameters(@ForAll String aString) {
			return true;
		}

		@Data
		Iterable twos() {
			return Table.of(
				Tuple.of("4", 4),
				Tuple.of("5", 5),
				Tuple.of("6", 6)
			);
		}

		@Group
		class NestedNamedProviders {
			@Property
			@DataFrom("byMethodName")
			boolean nestedStringByMethodName(@ForAll String aString) {
				return true;
			}

			@Property
			@DataFrom("aString")
			boolean nestedString(@ForAll String aString) {
				return true;
			}

		}
	}

	private static PropertyMethodDataResolver getResolver(Class<?> container) {
		return new PropertyMethodDataResolver(container, JqwikReflectionSupport.newInstanceWithDefaultConstructor(container));
	}

	private static Method getMethod(Class container, String methodName) {
		return TestHelper.getMethod(container, methodName);
	}

}