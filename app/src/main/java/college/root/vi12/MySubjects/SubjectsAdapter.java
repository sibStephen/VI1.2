package college.root.vi12.MySubjects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
            return  list.size() ;
        }
    }

    public class SubjectsHolder extends RecyclerView.ViewHolder {


        private CheckBox cbSubjects;
        private TextView tvSubjCode;

        public SubjectsHolder(Context context, View itemView) {
            super(itemView);

            cbSubjects = (CheckBox) itemView.findViewById(R.id.cbSubjects);
            tvSubjCode = (TextView) itemView.findViewById(R.id.tvSubjCode);

            if (cbSubjects.isChecked() ){
                String code = tvSubjCode.getText().toString();


            }

        }


    }
}
