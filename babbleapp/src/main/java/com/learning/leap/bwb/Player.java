package com.learning.leap.bwb;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.learning.leap.bwb.helper.LocalLoadSaveHelper;
import com.learning.leap.bwb.models.BabblePlayer;
import com.learning.leap.bwb.models.Notification;
import com.learning.leap.bwb.research.ResearchNotifications;
import com.learning.leap.bwb.utility.NetworkCheckerInterface;

import java.util.Date;

import io.reactivex.Observable;

/**
 * Created by ryanlgunn8 on 5/31/17.
 */

public interface Player {
    String mBabbleID = "";
    String mBabyBirthday = "";
    String mBabyName = "";
    int mZipCode = 0;
    int userAgeInMonth = 0;
    Date birthdayDate = new Date();
    String babyGender = "Not Now";

    Observable<Object> savePlayerObservable(DynamoDBMapper mapper, NetworkCheckerInterface checker, LocalLoadSaveHelper saveHelper);

    Observable<PaginatedScanList<Notification>> retriveNotifications(int babyAge, DynamoDBMapper mapper);

    Observable<PaginatedScanList<ResearchNotifications>> retriveNorthWestenNotifications(DynamoDBMapper mapper);

    void setuserAgeInMonth();

    Boolean checkNameIsTooLong();

    Boolean checkIfPlayerIsValid();

    Boolean checkNameIsEmpty();

    boolean checkZipCode();

    boolean checkDate();

    Player loadBabblePlayerFronSharedPref(LocalLoadSaveHelper saveHelper);

    void savePlayerToRealm();

    public String getBabbleID();

    public void setBabbleID(String babbleID);

    public String getBabyBirthday();

    public void setBabyBirthday(String babyBirthday);

    public String getBabyName();

    public void setBabyName(String babyName);

    public int getZipCode();

    public void setZipCode(int zipCode);

    String getBabyGender();

    void setBabyGender(String babyGender);

    int getuserAgeInMonth();

}
