/*
 * Copyright (c) 2021 Terminus, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cloud.erda.analyzer.tracing.functions;

import cloud.erda.analyzer.common.models.MetricEvent;
import cloud.erda.analyzer.common.utils.ConvertUtils;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.io.Serializable;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuhaoyang
 * @date 2021/9/22 01:42
 */
@Slf4j
public class MetricFieldAggregateFunction implements AggregateFunction<MetricEvent, StatsAccumulator, MetricEvent> {

    @Override
    public StatsAccumulator createAccumulator() {
        return new StatsAccumulator();
    }

    @Override
    public StatsAccumulator add(MetricEvent metricEvent, StatsAccumulator statsAccumulator) {
        statsAccumulator.apply(metricEvent);
        return statsAccumulator;
    }

    @Override
    public MetricEvent getResult(StatsAccumulator statsAccumulator) {
        MetricEvent metricEvent = statsAccumulator.getLastMetric().copy();
        metricEvent.getFields().clear();
        for (Map.Entry<String, StatsAccumulator.FieldAggregator> entry : statsAccumulator.getAggregators().entrySet()) {
            StatsAccumulator.FieldAggregator aggregator = entry.getValue();
            metricEvent.addField(aggregator.getName() + "_mean", aggregator.getMean());
            metricEvent.addField(aggregator.getName() + "_count", aggregator.getCount());
            metricEvent.addField(aggregator.getName() + "_sum", aggregator.getSum());
            metricEvent.addField(aggregator.getName() + "_min", aggregator.getMin());
            metricEvent.addField(aggregator.getName() + "_max", aggregator.getMax());
        }
        log.info("Aggregate metric. now: {} spanEndTime: {}", new Date(), new Date(metricEvent.getTimestamp() / 1000000));
        return metricEvent;
    }

    @Override
    public StatsAccumulator merge(StatsAccumulator statsAccumulator, StatsAccumulator acc1) {
        // todo Merge
        return statsAccumulator;
    }
}
