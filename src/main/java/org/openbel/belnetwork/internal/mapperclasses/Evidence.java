package org.openbel.belnetwork.internal.mapperclasses;

public class Evidence {
    private String bel_statement;
    private Citation citation;
    //biological_context can contain ncbi_tax_id, species_common_name, cell, tissue, disease ....
    private BiologicalContext biological_context;  //optional
    private String summary_text;

    public String getBel_statement() {
        return bel_statement;
    }

    public void setBel_statement(String bel_statement) {
        this.bel_statement = bel_statement;
    }

    public Citation getCitation() {
        return citation;
    }

    public void setCitation(Citation citation) {
        this.citation = citation;
    }

    public BiologicalContext getBiological_context() {
        return biological_context;
    }

    public void setBiological_context(BiologicalContext biological_context) {
        this.biological_context = biological_context;
    }

    public String getSummary_text() {
        return summary_text;
    }

    public void setSummary_text(String summary_text) {
        this.summary_text = summary_text;
    }
}