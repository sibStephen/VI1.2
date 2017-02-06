package college.root.vi12.StudentProfile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import college.root.vi12.R;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Form extends AppCompatActivity {

    Realm realm;
    EditText name,surname,grno;
    Button save;
    Student_profile profile;
    String TAG = "Test";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);



        RealmConfiguration config = new RealmConfiguration.Builder(getApplication()).schemaVersion(4).deleteRealmIfMigrationNeeded().build();
        realm.setDefaultConfiguration(config);


        realm = Realm.getDefaultInstance();
        name=(EditText)findViewById(R.id.form_name);
        surname=(EditText)findViewById(R.id.form_surname);
        grno=(EditText)findViewById(R.id.form_grno);

        save=(Button)findViewById(R.id.save_form);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile = new Student_profile();
                profile = realm.where(Student_profile.class).findFirst();
                if(profile==null) {
                    Log.d(TAG, "save: profile is null");
                    //     profile = realm.createObject(Student_profile.class);
                    realm.beginTransaction();
                    profile = new Student_profile();
                    profile.setName(name.getText().toString());
                    profile.setSurname(surname.getText().toString());

                    profile.setGrno(grno.getText().toString());

                    realm.commitTransaction();

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(profile);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Intent intent=new Intent(Form.this,UserProfile.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(),"Data saved successfully",Toast.LENGTH_SHORT).show();
                        }
                    });


//            realm.commitTransaction();
                }
                else
                {
                    //  oldgrno=profile.getGrno();
                    profile = realm.where(Student_profile.class).findFirst();
                    realm.beginTransaction();
                    profile.setName(name.getText().toString());
                    profile.setSurname(surname.getText().toString());

                    profile.setGrno(grno.getText().toString());
                    realm.commitTransaction();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(profile);

                        }
                    });
                }
            }
        });
    }
}
