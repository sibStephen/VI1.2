package college.root.vi12.Student.Student_TT.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 18/8/17.
 */

public class TTRealmObject extends RealmObject {

    @PrimaryKey
    String id;
    String jsonTTObject;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonTTObject() {
        return jsonTTObject;
    }

    public void setJsonTTObject(String jsonTTObject) {
        this.jsonTTObject = jsonTTObject;
    }
}