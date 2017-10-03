package college.root.vi12.Faculty.FacultyProfile;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 3/9/17.
 */

public class FacultyProfileRealm extends RealmObject {

    @PrimaryKey
    private String eid;
    private String div;
    int uid;
    private String branch;
    public String username;
    public String password;
    public String mname;
    public String fprofession,fdesig,fworkplace,fmobile,femail,mprofession,mworkplace,mdesig,mmobile;
    private String name;
    public String ssc_maths;
    public String email_pri;
    public String email_sec;
    public String religion;
    public String mother_ton;
    public String birth_place;
    public String sub_caste;
    public String uni_area;
    public String full_name;
    public String pref_no;
    public String income;
    public String aadhar;
    public String nationality;
    public String blood;
    public String mobile;
    public String mstatus;
    public String emcontact;
    public String ssc_sci;
    public String ssc_total;
    public String hsc_maths;
    public String hsc_chem;
    public String hsc_phy;
    public String hsc_eng;
    public String hsc_it;
    public String hsc_total;
    public String imagePath;
    public String fname;
    private String surname;
    private String year;
    String semester;
    String address, landlineNumber, area, pincode,state,city, district;
    String certificate10th, certificate12th;
    boolean notificationEnabled = true;

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getDiv() {
        return div;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getFprofession() {
        return fprofession;
    }

    public void setFprofession(String fprofession) {
        this.fprofession = fprofession;
    }

    public String getFdesig() {
        return fdesig;
    }

    public void setFdesig(String fdesig) {
        this.fdesig = fdesig;
    }

    public String getFworkplace() {
        return fworkplace;
    }

    public void setFworkplace(String fworkplace) {
        this.fworkplace = fworkplace;
    }

    public String getFmobile() {
        return fmobile;
    }

    public void setFmobile(String fmobile) {
        this.fmobile = fmobile;
    }

    public String getFemail() {
        return femail;
    }

    public void setFemail(String femail) {
        this.femail = femail;
    }

    public String getMprofession() {
        return mprofession;
    }

    public void setMprofession(String mprofession) {
        this.mprofession = mprofession;
    }

    public String getMworkplace() {
        return mworkplace;
    }

    public void setMworkplace(String mworkplace) {
        this.mworkplace = mworkplace;
    }

    public String getMdesig() {
        return mdesig;
    }

    public void setMdesig(String mdesig) {
        this.mdesig = mdesig;
    }

    public String getMmobile() {
        return mmobile;
    }

    public void setMmobile(String mmobile) {
        this.mmobile = mmobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsc_maths() {
        return ssc_maths;
    }

    public void setSsc_maths(String ssc_maths) {
        this.ssc_maths = ssc_maths;
    }

    public String getEmail_pri() {
        return email_pri;
    }

    public void setEmail_pri(String email_pri) {
        this.email_pri = email_pri;
    }

    public String getEmail_sec() {
        return email_sec;
    }

    public void setEmail_sec(String email_sec) {
        this.email_sec = email_sec;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getMother_ton() {
        return mother_ton;
    }

    public void setMother_ton(String mother_ton) {
        this.mother_ton = mother_ton;
    }

    public String getBirth_place() {
        return birth_place;
    }

    public void setBirth_place(String birth_place) {
        this.birth_place = birth_place;
    }

    public String getSub_caste() {
        return sub_caste;
    }

    public void setSub_caste(String sub_caste) {
        this.sub_caste = sub_caste;
    }

    public String getUni_area() {
        return uni_area;
    }

    public void setUni_area(String uni_area) {
        this.uni_area = uni_area;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPref_no() {
        return pref_no;
    }

    public void setPref_no(String pref_no) {
        this.pref_no = pref_no;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMstatus() {
        return mstatus;
    }

    public void setMstatus(String mstatus) {
        this.mstatus = mstatus;
    }

    public String getEmcontact() {
        return emcontact;
    }

    public void setEmcontact(String emcontact) {
        this.emcontact = emcontact;
    }

    public String getSsc_sci() {
        return ssc_sci;
    }

    public void setSsc_sci(String ssc_sci) {
        this.ssc_sci = ssc_sci;
    }

    public String getSsc_total() {
        return ssc_total;
    }

    public void setSsc_total(String ssc_total) {
        this.ssc_total = ssc_total;
    }

    public String getHsc_maths() {
        return hsc_maths;
    }

    public void setHsc_maths(String hsc_maths) {
        this.hsc_maths = hsc_maths;
    }

    public String getHsc_chem() {
        return hsc_chem;
    }

    public void setHsc_chem(String hsc_chem) {
        this.hsc_chem = hsc_chem;
    }

    public String getHsc_phy() {
        return hsc_phy;
    }

    public void setHsc_phy(String hsc_phy) {
        this.hsc_phy = hsc_phy;
    }

    public String getHsc_eng() {
        return hsc_eng;
    }

    public void setHsc_eng(String hsc_eng) {
        this.hsc_eng = hsc_eng;
    }

    public String getHsc_it() {
        return hsc_it;
    }

    public void setHsc_it(String hsc_it) {
        this.hsc_it = hsc_it;
    }

    public String getHsc_total() {
        return hsc_total;
    }

    public void setHsc_total(String hsc_total) {
        this.hsc_total = hsc_total;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandlineNumber() {
        return landlineNumber;
    }

    public void setLandlineNumber(String landlineNumber) {
        this.landlineNumber = landlineNumber;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCertificate10th() {
        return certificate10th;
    }

    public void setCertificate10th(String certificate10th) {
        this.certificate10th = certificate10th;
    }

    public String getCertificate12th() {
        return certificate12th;
    }

    public void setCertificate12th(String certificate12th) {
        this.certificate12th = certificate12th;
    }
}
