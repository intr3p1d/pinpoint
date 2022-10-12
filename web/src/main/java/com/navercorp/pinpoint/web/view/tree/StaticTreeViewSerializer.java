package com.navercorp.pinpoint.web.view.tree;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Iterator;

public class StaticTreeViewSerializer<I,C> extends JsonSerializer<StaticTreeView<I, C>> {

    private final static String GROUP_NAME = "groupName";
    private final static String INSTANCES_LIST = "instancesList";

    @Override
    public void serialize(StaticTreeView<I, C> treeView, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        Iterator<TreeNode<C>> nodes = treeView.nodes();
        while (nodes.hasNext()) {
            TreeNode<C> treeNode = nodes.next();
            jgen.writeFieldName(GROUP_NAME);
            jgen.writeObject(treeNode.getValue());
            jgen.writeFieldName(INSTANCES_LIST);
            jgen.writeObject(treeNode.getChildren());
        }
        jgen.writeEndObject();
    }
}