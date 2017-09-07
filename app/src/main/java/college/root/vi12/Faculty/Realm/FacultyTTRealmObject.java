package college.root.vi12.Faculty.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 7/9/17.
 */

public class FacultyTTRealmObject extends RealmObject {

    @PrimaryKey
    String id;
    String jsonTT;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonTT() {
        return jsonTT;
    }

    public void setJsonTT(String jsonTT) {
        this.jsonTT = jsonTT;
    }
}
