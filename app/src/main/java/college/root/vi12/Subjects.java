package college.root.vi12;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.socket.client.Socket;

/**
 * Created by root on 28/2/17.
 */

public class Subjects extends RealmObject {



@PrimaryKey
    String GRNumber ;
   String subj1Name, subj2Name,subj3Name,subj4Name,subj5Name;
    String subj1Total,subj2Total,subj3Total,subj4Total,subj5Total;
    String subj1Att,subj2Att,subj3Att,subj4Att,subj5Att;
    String myTotalAtendance;

    public String getMyTotalAtendance() {
        return myTotalAtendance;
    }

    public void setMyTotalAtendance(String myTotalAtendance) {
        this.myTotalAtendance = myTotalAtendance;
    }

    public String  getGRNumber() {
        return GRNumber;
    }

    public void setGRNumber(String GRNumber) {
        this.GRNumber = GRNumber;
    }

    public String getSubj1Att() {
        return subj1Att;
    }

    public void setSubj1Att(String subj1Att) {
        this.subj1Att = subj1Att;
    }

    public String getSubj1Name() {
        return subj1Name;
    }

    public void setSubj1Name(String subj1Name) {
        this.subj1Name = subj1Name;
    }

    public String getSubj1Total() {
        return subj1Total;
    }

    public void setSubj1Total(String subj1Total) {
        this.subj1Total = subj1Total;
    }

    public String getSubj2Att() {
        return subj2Att;
    }

    public void setSubj2Att(String subj2Att) {
        this.subj2Att = subj2Att;
    }

    public String getSubj2Name() {
        return subj2Name;
    }

    public void setSubj2Name(String subj2Name) {
        this.subj2Name = subj2Name;
    }

    public String getSubj2Total() {
        return subj2Total;
    }

    public void setSubj2Total(String subj2Total) {
        this.subj2Total = subj2Total;
    }

    public String getSubj3Att() {
        return subj3Att;
    }

    public void setSubj3Att(String subj3Att) {
        this.subj3Att = subj3Att;
    }

    public String getSubj3Name() {
        return subj3Name;
    }

    public void setSubj3Name(String subj3Name) {
        this.subj3Name = subj3Name;
    }

    public String getSubj3Total() {
        return subj3Total;
    }

    public void setSubj3Total(String subj3Total) {
        this.subj3Total = subj3Total;
    }

    public String getSubj4Att() {
        return subj4Att;
    }

    public void setSubj4Att(String subj4Att) {
        this.subj4Att = subj4Att;
    }

    public String getSubj4Name() {
        return subj4Name;
    }

    public void setSubj4Name(String subj4Name) {
        this.subj4Name = subj4Name;
    }

    public String getSubj4Total() {
        return subj4Total;
    }

    public void setSubj4Total(String subj4Total) {
        this.subj4Total = subj4Total;
    }

    public String getSubj5Att() {
        return subj5Att;
    }

    public void setSubj5Att(String subj5Att) {
        this.subj5Att = subj5Att;
    }

    public String getSubj5Name() {
        return subj5Name;
    }

    public void setSubj5Name(String subj5Name) {
        this.subj5Name = subj5Name;
    }

    public String getSubj5Total() {
        return subj5Total;
    }

    public void setSubj5Total(String subj5Total) {
        this.subj5Total = subj5Total;
    }
}
