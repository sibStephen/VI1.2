package college.root.vi12.StudentProfile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import college.root.vi12.R;
import io.realm.Realm;

public class EditProfileActivity extends AppCompatActivity{
    Realm realm;
    EditText name,surname,year,div,branch,grno;
    String oldgrno;
    Student_profile profile;
    Uri imageuri;
    String TAG = "Test";
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);




        viewPager=(ViewPager)findViewById(R.id.view);
        viewPager.setAdapter(new CustomAdapter(getSupportFragmentManager(),getApplicationContext()));
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
       }




    private class CustomAdapter extends FragmentPagerAdapter {
        private String fragments []={"MY INFO","DETAILS","RESIDENTIAL INFO","ACADEMIC DETAILS","PARENT INFO", "Certificates"};
        public CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new FragmentProfile1();
                case 1:
                    return new FragmentProfile2();
                case 2:
                    return new FragmentProfile3();
                case 3:
                    return new FragmentProfile4();
                case 4:
                    return new FragmentProfile5();
                case 5:
                    return new FragmentCertificates();

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments[position];
        }
    }


}
