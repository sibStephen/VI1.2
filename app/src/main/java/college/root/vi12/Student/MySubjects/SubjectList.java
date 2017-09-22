package college.root.vi12.Student.MySubjects;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 27/4/17.
 */

public class SubjectList extends RealmObject {

    @PrimaryKey
    String GrNumber;
    String count ;
    RealmList<MySubjects> subjectsRealmList;


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    public String getGrNumber() {
        return GrNumber;
    }

    public void setGrNumber(String grNumber) {
        GrNumber = grNumber;
    }

    public RealmList<MySubjects> getSubjectsRealmList() {
        return subjectsRealmList;
    }

    public void setSubjectsRealmList(RealmList<MySubjects> subjectsRealmList) {
        this.subjectsRealmList = subjectsRealmList;
    }
}
