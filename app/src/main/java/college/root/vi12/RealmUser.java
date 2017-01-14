package college.root.vi12;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 14/1/17.
 */

public class RealmUser extends RealmObject {

    @PrimaryKey
    int uid;

    String branch , year, Name , Email ;
    int GrNumber;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getGrNumber() {
        return GrNumber;
    }

    public void setGrNumber(int grNumber) {
        GrNumber = grNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
