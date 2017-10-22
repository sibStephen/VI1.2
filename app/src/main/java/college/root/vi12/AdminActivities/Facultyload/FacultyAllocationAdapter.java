package college.root.vi12.AdminActivities.Facultyload;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import college.root.vi12.R;

/**
 * Created by root on 21/9/17.
 */

public class FacultyAllocationAdapter extends RecyclerView.Adapter<FacultyAllocationAdapter.FacultyAllocationViewHolder> {

   private Context context;
    private String TAG =  "Test";
    private int count=0;
   private FacultyLoadHelper[] helpers;

    public FacultyAllocationAdapter(Context context , FacultyLoadHelper[] helpers){

        this.context = context;
        this.helpers = helpers;




    }


    @Override
    public FacultyAllocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.facultyallocation, parent , false);
        return new FacultyAllocationViewHolder(context , v);
    }

    @Override
    public void onBindViewHolder(FacultyAllocationViewHolder holder, int position) {

        if (helpers[position] != null){
            Log.d(TAG, "onBindViewHolder: helpers length is "+helpers.length);
            Log.d(TAG, "onBindViewHolder: position is "+position);
            holder.tvSubjectName.setText(helpers[position].getSubjectName());
            holder.tvFacultyName.setText(helpers[position].getFacultyName());

        }
    }

    @Override
    public int getItemCount() {

        if (helpers!=null){
            return helpers.length;
        }
        else {
            return 0;
        }
    }

    public  class FacultyAllocationViewHolder extends RecyclerView.ViewHolder{

        TextView tvSubjectName;
        TextView tvFacultyName;

        public FacultyAllocationViewHolder(Context context , View itemView) {
            super(itemView);

            tvFacultyName = (TextView) itemView.findViewById(R.id.tvSubjectNameFac);
            tvSubjectName = (TextView) itemView.findViewById(R.id.tvFacultyName);

        }
    }
}
