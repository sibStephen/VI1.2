package college.root.vi12.AdminActivities.TimeTable;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.R;

import static android.content.ContentValues.TAG;

/**
 * Created by root on 13/9/17.
 */

public class RecyclerTimetableAdapter extends RecyclerView.Adapter<RecyclerTimetableAdapter.RecyclerViewHolder>{


    Context context;
    TTHelper[] tthelpers;
    String[] time, days;
    int position ;
    Spinner spinner_location;
    String loc;
    int day;
    int schedule;
    int timeSlots ;

    public RecyclerTimetableAdapter(Context context, String[] days, String[] time, TTHelper[] tthelpers, int timeSlots) {
        this.context = context;
        this.time = time;
        this.days = days;
        this.tthelpers = tthelpers;
        this.timeSlots = timeSlots;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.timetable_layout, parent , false);
        return new RecyclerViewHolder(context ,v);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        // first restore the state of tt activity;


        Log.d(TAG, "onBindViewHolder: position is "+position);


        if (tthelpers[position] != null){
            // object present...
            holder.checkBox.setChecked(true);
            holder.textView.setText(tthelpers[position].getSubject());


        }


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked){

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialogforlocation);
                    dialog.setCancelable(true);
                    //getAvailableLocations(position);
                    spinner_location = (Spinner) dialog.findViewById(R.id.spinner_location);
                    spinner_location.setAdapter(TableActivity.adapter_location);
                    Button button = (Button) dialog.findViewById(R.id.save);
                    dialog.show();
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loc = spinner_location.getSelectedItem().toString();
                            Log.d(TAG, "onCheckedChanged: state is "+holder.checkBox.isChecked());
                            Log.d(TAG, "onCheckedChanged: position is "+position);
                            day = position / timeSlots;
                            Log.d(TAG, "onBindViewHolder: day number is "+day);
                            schedule = position % timeSlots;

                            tthelpers[position] = new TTHelper();
                            tthelpers[position].setDay(days[day]);
                            tthelpers[position].setTime(time[schedule]);
                            tthelpers[position].setSubjectCode(TableActivity.mapOfSubjects.get(TableActivity.subject_selected));
                            tthelpers[position].setLocation(loc);
                            tthelpers[position].setFaculty(TableActivity.faculty);
                            tthelpers[position].setSubject(TableActivity.subject_selected);
                            tthelpers[position].setFacultyEID(Utils.mapOfFaculty.get(TableActivity.faculty));
                            Log.d(TAG, "onCheckedChanged: faculty is "+TableActivity.faculty);
                            Log.d(TAG, "onClick: day is "+tthelpers[position].getDay());

                            holder.textView.setText(tthelpers[position].getSubject());
                            dialog.cancel();
                        }
                    });

                }else{
                    Log.d(TAG, "onCheckedChanged: checkbox is unchecked...");
                    tthelpers[position] = null;
                    Log.d(TAG, "onCheckedChanged: marked null");
                    holder.textView.setText("");

                }






            }
        });



    }




    @Override
    public int getItemCount() {
        return tthelpers.length;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView textView;


        public RecyclerViewHolder(final Context context, final View itemView) {
            super(itemView);
           textView = (TextView) itemView.findViewById(R.id.textView);
           checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);



        }
    }
}
