package college.root.vi12.Miscleneous;

import io.realm.RealmObject;

/**
 * Created by root on 8/2/17.
 */

public class IPAddess extends RealmObject{
    public String ipaddress;

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }
}
