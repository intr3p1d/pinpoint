package com.navercorp.pinpoint.web.vo.agent;

public class SimpleAgentInfo {

    private String applicationName;
    private String agentId;
    private String agentName;

    public SimpleAgentInfo() {
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public static SimpleAgentInfo newSimpleAgentInfo(AgentInfo agentInfo) {
        SimpleAgentInfo simpleAgentInfo = new SimpleAgentInfo();
        simpleAgentInfo.setApplicationName(agentInfo.getApplicationName());
        simpleAgentInfo.setAgentId(agentInfo.getAgentId());
        simpleAgentInfo.setAgentName(agentInfo.getAgentName());
        return simpleAgentInfo;
    }

}
