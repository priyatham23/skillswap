package com.skillswap.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Education {
    private String id;
    private String school;
    private String degree;
    private String fieldOfStudy;
    private int startYear;
    private int endYear;

    public Education() {}

    public Education(String id, String school, String degree, String fieldOfStudy, int startYear, int endYear) {
        this.id = id;
        this.school = school;
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }
    public int getStartYear() { return startYear; }
    public void setStartYear(int startYear) { this.startYear = startYear; }
    public int getEndYear() { return endYear; }
    public void setEndYear(int endYear) { this.endYear = endYear; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("school", school);
        m.put("degree", degree);
        m.put("fieldOfStudy", fieldOfStudy);
        m.put("startYear", startYear);
        m.put("endYear", endYear);
        return m;
    }
}

