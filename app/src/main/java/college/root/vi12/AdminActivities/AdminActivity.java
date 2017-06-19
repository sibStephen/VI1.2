package college.root.vi12.AdminActivities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import college.root.vi12.MySubjects.UploadSubjectsActivity;
import college.root.vi12.R;
import college.root.vi12.StudentProfile.UserProfile;

public class AdminActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        drawerLayout = (DrawerLayout) findViewById(R.id.adminDrawerLayout);
        Toolbar toolbar=(Toolbar)findViewById(R.id.adminToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.adminNav);
       // assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.loadSubjects:

                startActivity(new Intent(AdminActivity.this , UploadSubjectsActivity.class));

            break;

            case R.id.loadFaculty:

                startActivity(new Intent(AdminActivity.this , FacultyLoadActivity.class));
                break;

            case R.id.loadTimeTable:
                startActivity(new Intent(AdminActivity.this , PreTimeTableSetup.class));
                break;


            case R.id.roomAllocation:
                startActivity(new Intent(AdminActivity.this , LocationEntry.class));


                break;

        }



        return false;
    }
}
