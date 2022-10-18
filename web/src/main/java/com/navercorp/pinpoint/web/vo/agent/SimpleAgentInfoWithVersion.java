package com.navercorp.pinpoint.web.vo.agent;

import com.navercorp.pinpoint.common.server.bo.JvmInfoBo;

public class SimpleAgentInfoWithVersion {

    private String applicationName;
    private String agentId;
    private String agentName;

    private String agentVersion;
    private JvmInfoBo jvmInfo;


    public SimpleAgentInfoWithVersion() {
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public JvmInfoBo getJvmInfo() {
        return jvmInfo;
    }

    public void setJvmInfo(JvmInfoBo jvmInfo) {
        this.jvmInfo = jvmInfo;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }
}
