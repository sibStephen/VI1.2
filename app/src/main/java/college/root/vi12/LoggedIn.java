package college.root.vi12;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 30/7/17.
 */

public class LoggedIn extends RealmObject {

    @PrimaryKey
    private int id;

    private boolean isLoggedIn = false;

    boolean isLoggedIn() {
        return isLoggedIn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
