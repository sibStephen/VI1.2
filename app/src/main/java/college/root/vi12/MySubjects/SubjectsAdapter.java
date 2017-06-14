package college.root.vi12.MySubjects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;

import college.root.vi12.R;

/**
 * Created by root on 6/6/17.
 */

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectsHolder> {


    Context context;
    ArrayList<MySubjects> list;
    String TAG = "Test";


    public  SubjectsAdapter(ArrayList<MySubjects> list ,Context context){
        this.context = context;
        this.list = list;
    }

    @Override
    public SubjectsHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.mysubjects , parent , false);
        return new SubjectsHolder(context ,v);
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(SubjectsHolder holder, int position) {

        MySubjects subjects = list.get(position);
        holder.tvSubjCode.setText(subjects.getSubjectCode());
        holder.cbSubjects.setText(subjects.getSubjectName());




    }

    @Override
    public int getItemCount() {
        if(list == null){
            return  0;
        }else{

            Log.d(TAG, "getItemCount: item count is "+list.size());
            return  list.size() ;
        }
    }

    public class SubjectsHolder extends RecyclerView.ViewHolder {


        private CheckBox cbSubjects;
        private TextView tvSubjCode;

        public SubjectsHolder(final Context context, View itemView) {
            super(itemView);

            cbSubjects = (CheckBox) itemView.findViewById(R.id.cbSubjects);
            tvSubjCode = (TextView) itemView.findViewById(R.id.tvSubjCode);


            cbSubjects.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    if (cbSubjects.isChecked() ){
                        String code = tvSubjCode.getText().toString();
                        ((MySubjectsActivity)context).codes.add(code);
                        Log.d(TAG, "SubjectsHolder: Code "+code + " added ......");
                    }
                }
            });


        }


    }
}
