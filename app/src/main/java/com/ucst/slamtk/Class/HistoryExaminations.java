package com.ucst.slamtk.Class;
public class HistoryExaminations {
    String sampleDate;
    String examinationId;
    String sampleTime;
    int babyId;
    String bilirubinRate;
    String riskType;
    String recommendation1;
    String recommendation2;
    String recommendation3;

    public HistoryExaminations() {
    }//HistoryExaminations()

    public HistoryExaminations(String sampleDate, String examinationId, String sampleTime, int babyId, String bilirubinRate, String riskType, String recommendation1, String recommendation2, String recommendation3) {
        this.sampleDate = sampleDate;
        this.examinationId = examinationId;
        this.sampleTime = sampleTime;
        this.babyId = babyId;
        this.bilirubinRate = bilirubinRate;
        this.riskType = riskType;
        this.recommendation1 = recommendation1;
        this.recommendation2 = recommendation2;
        this.recommendation3 = recommendation3;
    }

    public String getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    public String getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(String sampleTime) {
        this.sampleTime = sampleTime;
    }

    public int getBabyId() {
        return babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public String getBilirubinRate() {
        return bilirubinRate;
    }

    public void setBilirubinRate(String bilirubinRate) {
        this.bilirubinRate = bilirubinRate;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public String getRecommendation1() {
        return recommendation1;
    }

    public void setRecommendation1(String recommendation1) {
        this.recommendation1 = recommendation1;
    }

    public String getRecommendation2() {
        return recommendation2;
    }

    public void setRecommendation2(String recommendation2) {
        this.recommendation2 = recommendation2;
    }

    public String getRecommendation3() {
        return recommendation3;
    }

    public void setRecommendation3(String recommendation3) {
        this.recommendation3 = recommendation3;
    }

    public String getExaminationId() {
        return examinationId;
    }

    public void setExaminationId(String examinationId) {
        this.examinationId = examinationId;
    }
}//HistoryExaminations
