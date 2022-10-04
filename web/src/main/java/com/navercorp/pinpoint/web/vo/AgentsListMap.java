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
            return empty();
        }

        Collector<T, ?, Map<String, List<T>>> collector = Collectors.groupingBy(keyExtractor);
        Map<String, List<T>> mapByGivenClassifier = collection.stream().collect(collector);

        List<AgentsList<T>> agentsListMap = new ArrayList<>(mapByGivenClassifier.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new AgentsList<>(e.getKey(), e.getValue(), sortBy),
                        (left, right) -> left,
                        () -> new TreeMap<>(keyComparator)
                )
        ).values());
        return new AgentsListMap<>(agentsListMap);
    }

    public static <T extends AgentInfoSupplier> AgentsListMap<T> empty() {
        return new AgentsListMap<>(new ArrayList<>());
    }

    public List<AgentsList<T>> getListMap() {
        return listMap;
    }

    @Override
    public String toString() {
        return "AgentsListMap{" +
                "listMap=" + listMap +
                '}';
    }
}
