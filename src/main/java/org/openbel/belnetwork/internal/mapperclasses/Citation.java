package org.openbel.belnetwork.internal.mapperclasses;

public class Citation {
    private String type;
    private String id;
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String reference_type) {
        this.type = reference_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String reference_id) {
        this.id = reference_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}