package college.root.vi12.NetworkTasks;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;



public class JsontoSend extends RealmObject {

    @PrimaryKey
    String id;
    String Json;
    String collection;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJson() {
        return Json;
    }

    public void setJson(String json) {
        Json = json;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }
}
