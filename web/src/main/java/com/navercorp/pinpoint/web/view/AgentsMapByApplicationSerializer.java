package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.AgentsList;
import com.navercorp.pinpoint.web.vo.AgentsMapByApplication;
import com.navercorp.pinpoint.web.vo.agent.AgentStatusAndLink;

import java.io.IOException;
import java.util.List;

public class AgentsMapByApplicationSerializer extends JsonSerializer<AgentsMapByApplication> {

    @Override
    public void serialize(AgentsMapByApplication agentsMapByApplication, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        List<AgentsList<AgentStatusAndLink>> applicationAgentsLists = agentsMapByApplication.getAgentsListsList();
        for (AgentsList<AgentStatusAndLink> agentsList : applicationAgentsLists) {
            jgen.writeFieldName(agentsList.getGroupName());
            jgen.writeObject(agentsList.getAgentSuppliersList());
        }
        jgen.writeEndObject();
    }
}
