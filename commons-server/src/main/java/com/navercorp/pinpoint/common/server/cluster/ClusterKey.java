package com.navercorp.pinpoint.common.server.cluster;

import com.google.common.base.Preconditions;

import java.util.Objects;

public record ClusterKey(String applicationName, String agentId, String agentName, long startTimestamp) {
    public static final char DELIMITER_CHAR = ':';
    public static final String DELIMITER = "" + DELIMITER_CHAR;

    public static ClusterKey parse(String clusterKeyFormat) {
        Objects.requireNonNull(clusterKeyFormat, "clusterKeyFormat");

        String[] tokens = clusterKeyFormat.split(DELIMITER, 4);
        Preconditions.checkArgument(tokens.length == 4, "invalid token.length == 4");
        return new ClusterKey(tokens[0], tokens[1], tokens[2], Long.parseLong(tokens[3]));
    }

    public static String compose(String applicationName, String agentId, String agentName, long startTimestamp) {
        return String.join(DELIMITER, applicationName, agentId, agentName, String.valueOf(startTimestamp));

    }

    public ClusterKey(String applicationName, String agentId, String agentName, long startTimestamp) {
        this.applicationName = Objects.requireNonNull(applicationName, "applicationName");
        this.agentId = Objects.requireNonNull(agentId, "agentId");
        this.agentName = Objects.requireNonNull(agentName, "agentName");
        this.startTimestamp = startTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClusterKey that = (ClusterKey) o;

        if (!applicationName.equals(that.applicationName)) return false;
        if (!agentId.equals(that.agentId)) return false;
        return agentName.equals(that.agentName);
    }

    @Override
    public int hashCode() {
        int result = applicationName.hashCode();
        result = 31 * result + agentId.hashCode();
        result = 31 * result + agentName.hashCode();
        return result;
    }

    public String format() {
        return compose(applicationName, agentId, agentName, startTimestamp);
    }

    @Override
    public String toString() {
        return format();
    }
}
