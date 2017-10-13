package college.root.vi12.Student.MySubjects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 25/4/17.
 */
import java.lang.String;

public class MySubjects extends RealmObject {

    @PrimaryKey
    String subjectCode;
    String subjectName;

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


}