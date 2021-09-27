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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuhaoyang
 * @date 2021/9/23 22:49
 */
@Data
public class StatsAccumulator implements Serializable {

    private MetricEvent lastMetric;

    private Map<String, FieldAggregator> aggregators = new HashMap<>();

    public void apply(MetricEvent metricEvent) {
        if (metricEvent == null) {
            return;
        }
        lastMetric = metricEvent;
        for (Map.Entry<String, Object> entry : metricEvent.getFields().entrySet()) {
            FieldAggregator aggregator = aggregators.computeIfAbsent(entry.getKey(), FieldAggregator::new);
            aggregator.apply(entry.getValue());
        }
    }
}

