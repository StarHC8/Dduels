package org.starhc.dduels.models;

import java.util.Map;

public class MapTemplate {

    private String templateName;
    private String templateDisplayName;
    private Map<Integer, Spawn> spawns;

    public MapTemplate(String templateName, String templateDisplayName, Map<Integer, Spawn> spawns) {
        this.templateName = templateName;
        this.templateDisplayName = templateDisplayName;
        this.spawns = spawns;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getTemplateDisplayName() {
        return templateDisplayName;
    }

    public Map<Integer, Spawn> getSpawns() {
        return spawns;
    }

}

