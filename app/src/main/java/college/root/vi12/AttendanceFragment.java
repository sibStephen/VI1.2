package college.root.vi12;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendanceFragment extends Fragment implements ProgressGenerator.OnCompleteListener{


    public AttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionProcessButton btnSignIn = (ActionProcessButton) getActivity().findViewById(R.id.btnSignIn);
        btnSignIn.setMode(ActionProcessButton.Mode.PROGRESS);

// no progress
        btnSignIn.setProgress(0);
// progressDrawable cover 50% of button width, progressText is shown
        btnSignIn.setProgress(50);
// progressDrawable cover 75% of button width, progressText is shown
        btnSignIn.setProgress(75);
// completeColor & completeText is shown
        btnSignIn.setProgress(100);

// you can display endless google like progress indicator
        btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);
// set progress > 0 to start progress indicator animation
        btnSignIn.setProgress(1);
    }

    @Override
    public void onComplete() {

        android.widget.Toast.makeText(getContext(), "Completed", Toast.LENGTH_SHORT).show();

    }
}
