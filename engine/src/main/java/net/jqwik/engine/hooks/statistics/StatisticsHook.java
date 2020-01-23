package net.jqwik.engine.hooks.statistics;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.LifecycleHook.*;
import net.jqwik.engine.hooks.*;

public class StatisticsHook implements AroundPropertyHook, PropagateToChildren {

	private static final Supplier<Map<String, StatisticsCollectorImpl>> STATISTICS_MAP_SUPPLIER =
		() -> new HashMap<String, StatisticsCollectorImpl>() {
			@Override
			public StatisticsCollectorImpl get(Object key) {
				return this.computeIfAbsent((String) key, StatisticsCollectorImpl::new);
			}
		};

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		Store<Map<String, StatisticsCollectorImpl>> collectorsStore =
			Store.create(
				Store.Visibility.LOCAL,
				StatisticsCollectorImpl.STORE_NAME,
				STATISTICS_MAP_SUPPLIER
			);
		PropertyExecutionResult testExecutionResult = property.execute();
		report(collectorsStore.get(), context.reporter(), context.extendedLabel());
		return testExecutionResult;
	}

	private void report(Map<String, StatisticsCollectorImpl> collectors, Reporter reporter, String propertyName) {
		for (StatisticsCollectorImpl collector : collectors.values()) {
			publishStatisticsReport(collector, reporter, propertyName);
		}
	}

	private void publishStatisticsReport(StatisticsCollectorImpl collector, Reporter reporter, String propertyName) {
		StatisticsReportGenerator reportGenerator = new StatisticsReportGenerator(collector);
		String statisticsReport = reportGenerator.createReport();
		String statisticsReportEntryKey = reportGenerator.createReportEntryKey(propertyName);
		Tuple2<String, String> reportEntry = Tuple.of(statisticsReportEntryKey, statisticsReport);
		reporter.publish(reportEntry.get1(), reportEntry.get2());
	}

	@Override
	public int aroundPropertyProximity() {
		return Hooks.AroundProperty.STATISTICS_PROXIMITY;
	}

}
