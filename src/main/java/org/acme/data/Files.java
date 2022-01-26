package org.acme.data;

import javax.validation.constraints.NotBlank;

public class Files {
    @NotBlank()
    private String fileName;

    @NotBlank()
    private String beginDate;

    @NotBlank()
    private String endDate;

    public Files() {
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
