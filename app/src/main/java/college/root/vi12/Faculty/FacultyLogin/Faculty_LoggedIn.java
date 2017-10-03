package college.root.vi12.Faculty.FacultyLogin;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 30/7/17.
 */

public class Faculty_LoggedIn extends RealmObject {

    @PrimaryKey
    int id;

    boolean isLoggedIn = false;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
