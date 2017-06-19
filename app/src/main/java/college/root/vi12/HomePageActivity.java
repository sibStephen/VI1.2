package college.root.vi12;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import college.root.vi12.AdminActivities.AdminActivity;
import college.root.vi12.MySubjects.UploadSubjectsActivity;
import college.root.vi12.StudentProfile.UserProfile;

public class HomePageActivity extends AppCompatActivity {

    BottomNavigationView mbottomnav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

    mbottomnav = (BottomNavigationView)findViewById(R.id.bottomnav);


        mbottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.menu_home:
                        startActivity(new Intent(HomePageActivity.this , AdminActivity.class));

                        break;

                    case R.id.menu_profile:
                       startActivity(new Intent(HomePageActivity.this , UserProfile.class));
                        break;
                    case R.id.menu_search:
                        startActivity(new Intent(HomePageActivity.this , TestActivity.class));

                        //addFragment(new TestFragment() , null);
                        break;

                }


                // handle desired action here
                // One possibility of action is to replace the contents above the nav bar
                // return true if you want the item to be displayed as the selected item
                return true;
            }
        });




    }

    public void addFragment(Fragment fragment, Bundle bundle){

        if(bundle != null)
            fragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }
}
