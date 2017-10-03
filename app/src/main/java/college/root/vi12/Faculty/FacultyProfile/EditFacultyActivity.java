package college.root.vi12.Faculty.FacultyProfile;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import college.root.vi12.R;

public class EditFacultyActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_faculty);




        viewPager=(ViewPager)findViewById(R.id.facview);
        viewPager.setAdapter(new EditFacultyActivity.CustomAdapter(getSupportFragmentManager(), EditFacultyActivity.this));
        tabLayout=(TabLayout)findViewById(R.id.FactabLayout);
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
        private String fragments []={"MY INFO"/*"DETAILS","RESIDENTIAL INFO","ACADEMIC DETAILS","PARENT INFO", "Certificates"*/};
        CustomAdapter(FragmentManager supportFragmentManager, Context applicationContext) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new FragmentFacultyProfile();
                case 1:
                 //   return new FragmentProfile2();
                case 2:
                   // return new FragmentProfile3();
                case 3:
                    //return new FragmentProfile4();
                case 4:
                    //return new FragmentProfile5();
                case 5:
                    //return new FragmentCertificates();

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
