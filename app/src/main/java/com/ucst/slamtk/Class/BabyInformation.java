package com.ucst.slamtk.Class;

public class BabyInformation {
    public String babyName;
    public String gender;
    public String birthDate;
    public String birthTime;
    public String gestationalAge;
    public String riskFactor;
    public String babyId;
    public String userId;

    public BabyInformation(String babyName) {
        this.babyName=babyName;
    }

    public BabyInformation(String babyId,String userId,String babyName, String gender, String birthDate, String birthTime, String gestationalAge, String riskFactor) {
        this.babyId=babyId;
        this.userId=userId;
        this.babyName = babyName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.birthTime = birthTime;
        this.gestationalAge = gestationalAge;
        this.riskFactor = riskFactor;
    }//BabyInformation()

    public String getBabyId() {
        return babyId;
    }//getBabyId()

    public void setBabyId(String babyId) {
        this.babyId = babyId;
    }//setBabyId()

    public String getUserId() {
        return userId;
    }//getUserId()

    public void setUserId(String userId) {
        this.userId = userId;
    }//setUserId()

    public String getBabyName() {
        return babyName;
    }//getBabyName()

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }//setBabyName()

    public String getGender() {
        return gender;
    }//getGender()

    public void setGender(String gender) {
        this.gender = gender;
    }//setGender()

    public String getBirthDate() {
        return birthDate;
    }//getBirthDate()

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }//setBirthDate()

    public String getBirthTime() {
        return birthTime;
    }//getBirthTime()

    public void setBirthTime(String birthTime) {
        this.birthTime = birthTime;
    }//setBirthTime()

    public String getGestationalAge() {
        return gestationalAge;
    }//getGestationalAge()

    public void setGestationalAge(String gestationalAge) {
        this.gestationalAge = gestationalAge;
    }//setGestationalAge()

    public String getRiskFactor() {
        return riskFactor;
    }//getRiskFactor()

    public void setRiskFactor(String riskFactor) {
        this.riskFactor = riskFactor;
    }//setRiskFactor()
}//BabyInformation
