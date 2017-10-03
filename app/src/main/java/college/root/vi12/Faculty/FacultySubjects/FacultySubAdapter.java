package college.root.vi12.Faculty.FacultySubjects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import college.root.vi12.R;

/**
 * Created by root on 6/9/17.
 */

public class FacultySubAdapter  extends RecyclerView.Adapter<FacultySubAdapter.SubjectViewHolder> {


    ArrayList<FacultySubj> subjArrayList;
    Context context;
    FacultySubj subj;
    String TAG = "Test";


    public FacultySubAdapter(ArrayList<FacultySubj> subjArrayList, Context context) {
        this.subjArrayList = subjArrayList;
        this.context = context;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.faculty_subjects , parent , false);
        return new FacultySubAdapter.SubjectViewHolder(context, v);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {

        subj = new FacultySubj();
        subj = subjArrayList.get(position);
        holder.tvYear.setText(subj.getYear());
        holder.tvDiv.setText(subj.getDiv());
        holder.tvSubject.setText(subj.getSubject());
        Log.d(TAG, "onBindViewHolder: size is "+subjArrayList.size());
        Log.d(TAG, "onBindViewHolder: subject object is "+subj.getYear()+" "+subj.getSubject()+" "+subj.getDiv());

    }

    @Override
    public int getItemCount() {
        if (subjArrayList == null){
            return 0;
        }else{
            return subjArrayList.size();
        }

    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder{

        TextView tvYear, tvSubject, tvDiv;

        public SubjectViewHolder(Context context, View itemView) {
            super(itemView);

            tvYear = (TextView) itemView.findViewById(R.id.tvfsubjyear);
            tvSubject = (TextView) itemView.findViewById(R.id.facultySubjName);
            tvDiv = (TextView) itemView.findViewById(R.id.tvfsubjdiv);



        }
    }
}
