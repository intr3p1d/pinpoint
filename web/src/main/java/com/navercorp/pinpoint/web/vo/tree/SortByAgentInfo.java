package com.navercorp.pinpoint.web.vo.tree;

import com.navercorp.pinpoint.web.vo.agent.AgentInfo;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class SortByAgentInfo<T> {
    public static Comparator<AgentInfo> AGENT_NAME_ASC = Comparator.comparing(AgentInfo::getAgentName)
            .thenComparing(AgentInfo::getAgentId);

    public static Comparator<AgentInfo> AGENT_NAME_DESC = AGENT_NAME_ASC.reversed();

    public static Comparator<AgentInfo> AGENT_ID_ASC = Comparator.comparing(AgentInfo::getAgentId)
            .thenComparing(AgentInfo::getAgentName);

    public static Comparator<AgentInfo> AGENT_ID_DESC = AGENT_ID_ASC.reversed();

    public static Comparator<AgentInfo> LAST_STARTED_TIME = Comparator.comparingLong(AgentInfo::getStartTimestamp)
            .reversed()
            .thenComparing(AgentInfo::getAgentId);

    public static <T> SortByAgentInfo<T> agentNameAsc(Function<T, AgentInfo> keyExtractor) {
        Objects.requireNonNull(keyExtractor, "keyExtractor");
        return new SortByAgentInfo<>(Comparator.comparing(keyExtractor, AGENT_NAME_ASC));
    }

    public static <T> SortByAgentInfo<T> agentIdAsc(Function<T, AgentInfo> keyExtractor) {
        Objects.requireNonNull(keyExtractor, "keyExtractor");
        return new SortByAgentInfo<>(Comparator.comparing(keyExtractor, AGENT_ID_ASC));
    }

    public static <T> SortByAgentInfo<T> comparing(Function<T, AgentInfo> keyExtractor, Comparator<AgentInfo> comparator) {
        Objects.requireNonNull(keyExtractor, "keyExtractor");
        Objects.requireNonNull(comparator, "comparator");
        return new SortByAgentInfo<>(Comparator.comparing(keyExtractor, comparator));
    }

    public static Comparator<AgentInfo> of(String sortBy) {
        switch (sortBy) {
            case "AGENT_NAME_ASC":
                return AGENT_NAME_ASC;
            case "AGENT_NAME_DESC":
                return AGENT_NAME_DESC;
            case "AGENT_ID_ASC":
                return AGENT_ID_ASC;
            case "AGENT_ID_DESC":
                return AGENT_ID_DESC;
            case "LAST_STARTED_TIME":
                return LAST_STARTED_TIME;
            default:
                return AGENT_ID_ASC;
        }
    }

    private final Comparator<T> comparator;

    private SortByAgentInfo(Comparator<T> comparator) {
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    public Comparator<T> getComparator() {
        return comparator;
    }
}
