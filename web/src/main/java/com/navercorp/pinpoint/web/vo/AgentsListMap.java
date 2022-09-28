package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.vo.agent.AgentInfoSupplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentsListMap<T extends AgentInfoSupplier> {

    private final List<AgentsList<T>> listMap;

    private AgentsListMap(List<AgentsList<T>> listMap) {
        this.listMap = Objects.requireNonNull(listMap, "listMap");
    }

    public static <T extends AgentInfoSupplier> AgentsListMap<T> newAgentsListMap(Collection<T> collection,
                                                                                  Function<T, String> keyExtractor,
                                                                                  Comparator<String> keyComparator,
                                                                                  AgentsList.SortBy sortBy) {
        if (collection.isEmpty()) {
            return emptyMap();
        }

        Collector<T, ?, Map<String, List<T>>> collector = Collectors.groupingBy(keyExtractor);
        Map<String, List<T>> mapByGivenClassifier = collection.stream().collect(collector);

        List<AgentsList<T>> agentsListMap = new ArrayList<>(mapByGivenClassifier.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new AgentsList<T>(e.getKey(), e.getValue(), sortBy),
                        (v1, v2) -> v2,
                        () -> new TreeMap<>(keyComparator)
                )
        ).values());
        return new AgentsListMap<>(agentsListMap);
    }

    public static <T extends AgentInfoSupplier> AgentsListMap<T> concat(AgentsListMap<T> map1, AgentsListMap<T> map2) {
        Stream<AgentsList<T>> stream1 = map1.getListMap().stream();
        Stream<AgentsList<T>> stream2 = map2.getListMap().stream();
        List<AgentsList<T>> concatenated = Stream.concat(stream1, stream2).collect(Collectors.toList());

        return new AgentsListMap<>(concatenated);
    }

    public static <T extends AgentInfoSupplier> AgentsListMap<T> emptyMap() {
        return new AgentsListMap<>(new ArrayList<>());
    }

    public List<AgentsList<T>> getListMap() {
        return listMap;
    }
}
