package college.root.vi12.Faculty.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 6/9/17.
 */

public class FacultySubjRealmObj extends RealmObject{

    @PrimaryKey
    String eid;
    String jsonSubjObj;

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getJsonSubjObj() {
        return jsonSubjObj;
    }

    public void setJsonSubjObj(String jsonSubjObj) {
        this.jsonSubjObj = jsonSubjObj;
    }
}
