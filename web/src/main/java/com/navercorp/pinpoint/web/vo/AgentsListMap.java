package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoSupplier;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentsListMap<T extends AgentInfoSupplier> {

    @JsonValue
    private final SortedMap<String, AgentsList<T>> listmap;

    private AgentsListMap(SortedMap<String, AgentsList<T>> listmap) {
        this.listmap = Objects.requireNonNull(listmap, "listmap");
    }

    public static <T extends AgentInfoSupplier> AgentsListMap<T> newAgentsListMap(Collection<T> collection,
                                                                                  Function<T, String> classifier,
                                                                                  Comparator<String> keyComparator,
                                                                                  AgentsList.SortBy sortBy) {
        Collector<T, ?, Map<String, List<T>>> collector = Collectors.groupingBy(classifier);
        Map<String, List<T>> mapByGivenClassifier = collection.stream().collect(collector);

        SortedMap<String, AgentsList<T>> agentsListMap = mapByGivenClassifier.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new AgentsList<T>(e.getKey(), e.getValue(), sortBy),
                        (v1, v2) -> v2,
                        () -> new TreeMap<>(keyComparator)
                )
        );
        return new AgentsListMap<>(agentsListMap);
    }

    public static <T extends AgentInfoSupplier> AgentsListMap<T> emptyMap() {
        return new AgentsListMap<>(new TreeMap<>());
    }

    public SortedMap<String, AgentsList<T>> getListmap() {
        return listmap;
    }

    public static <T extends AgentInfoSupplier> AgentsListMap<T> merge(AgentsListMap<T> map1, AgentsListMap<T> map2, Comparator<String> keyComparator) {
        Stream<Map.Entry<String, AgentsList<T>>> stream1 = map1.getListmap().entrySet().stream();
        Stream<Map.Entry<String, AgentsList<T>>> stream2 = map2.getListmap().entrySet().stream();
        Stream<Map.Entry<String, AgentsList<T>>> mergedStream = Stream.concat(stream1, stream2);

        SortedMap<String, AgentsList<T>> agentsListMap = mergedStream.collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v2,
                        () -> new TreeMap<>(keyComparator)
                )
        );

        return new AgentsListMap<>(agentsListMap);
    }
}
