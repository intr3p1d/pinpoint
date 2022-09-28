package com.navercorp.pinpoint.web.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.navercorp.pinpoint.web.vo.AgentsList;
import com.navercorp.pinpoint.web.vo.AgentsMapByHost;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;

import java.io.IOException;
import java.util.List;

public class AgentsMapByHostSerializer extends JsonSerializer<AgentsMapByHost> {

    @Override
    public void serialize(AgentsMapByHost agentsMapByHost, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        List<AgentsList<AgentAndStatus>> applicationAgentsLists = agentsMapByHost.getAgentsListsList();
        for (AgentsList<AgentAndStatus> agentsList : applicationAgentsLists) {
            jgen.writeFieldName(agentsList.getGroupName());
            jgen.writeObject(agentsList.getAgentSuppliersList());
        }
        jgen.writeEndObject();
    }
}
