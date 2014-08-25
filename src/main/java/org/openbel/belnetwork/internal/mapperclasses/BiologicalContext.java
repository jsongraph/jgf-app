package org.openbel.belnetwork.internal.mapperclasses;

public class BiologicalContext {
    private Integer ncbi_tax_id;
    private String species_common_name;
    private String cell;
    private String tissue;
    
    public String getCell() {
        return cell;
    }
    public void setCell(String cell) {
        this.cell = cell;
    }
    public String getTissue() {
        return tissue;
    }
    public void setTissue(String tissue) {
        this.tissue = tissue;
    }
    public String getDisease() {
        return disease;
    }
    public void setDisease(String disease) {
        this.disease = disease;
    }
    private String disease;
    
    public Integer getNcbi_tax_id() {
        return ncbi_tax_id;
    }
    public void setNcbi_tax_id(Integer ncbi_tax_id) {
        this.ncbi_tax_id = ncbi_tax_id;
    }
    public String getSpecies_common_name() {
        return species_common_name;
    }
    public void setSpecies_common_name(String species_common_name) {
        this.species_common_name = species_common_name;
    }
    

}
