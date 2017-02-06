package college.root.vi12.StudentProfile;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by MRUNAL JOSHI on 2/5/2017.
 */

public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(adapterView.getContext(),
                "On Item Select : \n" + adapterView.getItemAtPosition(i).toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
