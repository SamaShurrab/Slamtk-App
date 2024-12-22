package com.ucst.slamtk.Class;

public class UserData {
    int userId;
    String userName;
    String mobileNumber;
    String password;

//    default Constructor
    public UserData() {}//UserData()

    public int getUserId() {
        return userId;
    }//getUserId()

    public UserData(int userId, String userName, String mobileNumber, String password) {
        this.userId = userId;
        this.userName = userName;
        this.mobileNumber = mobileNumber;
        this.password = password;
    }//UserData()

    public void setUserId(int userId) {
        this.userId = userId;
    }//setUserId()

    public String getUserName() {
        return userName;
    }//getUserName()

    public void setUserName(String userName) {
        this.userName = userName;
    }//setUserName()

    public String getMobileNumber() {
        return mobileNumber;
    }//getMobileNumber()

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }//setMobileNumber()

    public String getPassword() {
        return password;
    }//getPassword()

    public void setPassword(String password) {
        this.password = password;
    }//setPassword()

}//UserData
