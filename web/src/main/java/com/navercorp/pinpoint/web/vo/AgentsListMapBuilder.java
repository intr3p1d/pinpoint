package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.vo.agent.AgentInfoSupplier;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AgentsListMapBuilder<I extends AgentInfoSupplier, T extends AgentInfoSupplier> {
    Collection<I> agentCollection;

    private Predicate<I> agentFilter = x -> true;
    private Function<I, T> finisher = null;

    private Function<T, String> keyExtractor = a -> String.valueOf(a.hashCode());
    private Comparator<String> keyComparator = Comparator.naturalOrder();
    private AgentsList.SortBy sortAgentsListBy = AgentsList.SortBy.AGENT_ID_ASCENDING;

    AgentsListMapBuilder(Predicate<I> agentFilter,
                         Function<I, T> finisher) {
        this.agentFilter = Objects.requireNonNull(agentFilter, "agentFilter");
        this.finisher = Objects.requireNonNull(finisher, "finisher");
    }

    public AgentsListMapBuilder<I, T> withKeyExtractor(Function<T, String> keyGenerator) {
        this.keyExtractor = keyGenerator;
        return this;
    }

    public AgentsListMapBuilder<I, T> withKeyComparator(Comparator<String> keyComparator) {
        this.keyComparator = keyComparator;
        return this;
    }

    public AgentsListMapBuilder<I, T> sortBy(AgentsList.SortBy sortAgentsListBy) {
        this.sortAgentsListBy = sortAgentsListBy;
        return this;
    }

    public AgentsListMapBuilder<I, T> withCollection(Collection<I> collection) {
        this.agentCollection = collection;
        return this;
    }

    public AgentsListMap<T> build() {
        List<T> stream = agentCollection.stream()
                .filter(agentFilter)
                .map(finisher)
                .collect(Collectors.toList());

        return AgentsListMap.newAgentsListMap(
                stream,
                keyExtractor,
                keyComparator,
                sortAgentsListBy
        );
    }

}
