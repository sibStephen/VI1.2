
package college.root.vi12.AdminActivities.TimeTable;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import college.root.vi12.Faculty.FacultyProfile.FacultyProfileActivity;
import college.root.vi12.Miscleneous.Utils;
import college.root.vi12.NetworkTasks.NetworkUtils;
import college.root.vi12.R;
import college.root.vi12.Student.StudentProfile.UserProfile;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder> {
    private static String TAG = "MyRecyclerViewAdapter";
    private ArrayList<JSONObject> mDataset;
    Context context;


    public void addConstraints(DataObjectHolder holder , JSONObject object){

        if (TimeTableDisplayActivity.user.equals("Admin") ||
                TimeTableDisplayActivity.user.equals("Student")) {
            holder.reschedule.setVisibility(View.GONE);
        } else {
            holder.reschedule.setVisibility(View.VISIBLE);

        }
        if (TimeTableDisplayActivity.user.equals("Faculty")
                &&FacultyProfileActivity.currentLecObject != null  &&
                FacultyProfileActivity.currentLecObject.equals(object)){
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.greyColor));
            holder.reschedule.setVisibility(View.GONE);
        }

        Log.d(TAG, "addConstraints: current object is "+object +"  and loaded obj is"+UserProfile.currentLecObject);
        if (TimeTableDisplayActivity.user.equals("Student") &&
                UserProfile.currentLecObject != null &&
                UserProfile.currentLecObject.equals(object)){

            Log.d(TAG, "addConstraints: objects matched ...");
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.blue_normal));

        }
    }

    public  void setDay(int position){
        switch (position / 8){
            case 0:
                TimeTableDisplayActivity.tvday.setText("Monday");
                break;

            case 1:

                TimeTableDisplayActivity.tvday.setText("Tuesday");
                break;
            case 2:

                TimeTableDisplayActivity.tvday.setText("Monday");
                break;
            case 3:

                TimeTableDisplayActivity.tvday.setText("Wednesday");
                break;
            case 4:

                TimeTableDisplayActivity.tvday.setText("Thursday");
                break;
            case 5:

                TimeTableDisplayActivity.tvday.setText("Friday");
                break;
            case 6:

                TimeTableDisplayActivity.tvday.setText("Saturday");
                break;
        }
    }


    public MyRecyclerViewAdapter(ArrayList<JSONObject> mDataset ,Context context) {
        this.mDataset = mDataset;
        this.context = context;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {


        final JSONObject object = mDataset.get(position);
        addConstraints(holder , object);

        setDay(position);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: position is "+position);
                setDay(position);

            }
        });

        holder.reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: reschedule clicked...");
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.faculty_reschedule);
                dialog.setTitle("Reschedule your subject");
                dialog.setCancelable(true);
                final Spinner spFac = (Spinner) dialog.findViewById(R.id.spFaculty_resc);
                Button button = (Button) dialog.findViewById(R.id.btnSaveresc);

                ArrayAdapter<String> rescAdapter;
                List<String> listOfBranches = new ArrayList<String>();
                listOfBranches.add("Enter Faculty");
                listOfBranches.add("Shailesh Thaware");
                listOfBranches.add("shubham purandare");

                rescAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item,listOfBranches);
                rescAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFac.setAdapter(rescAdapter);



                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: saving changes");
                        String nameOfFac = spFac.getSelectedItem().toString();
                        Utils.loadHashMap();
                        String code = Utils.mapOfFaculty.get(nameOfFac);
                        Log.d(TAG, "onClick: code of "+nameOfFac +" is "+code);
                        JSONObject obj = new JSONObject();
                        try {
                            object.put("RequestFacEID" , "E103");
                            //TODO: replace eid with actual EID of requesting faculty
                            object.put("RequestType" ,"LecRescheduleRequest");
                            obj.put("code" , code);
                            obj.put("requestObject", object);
                            NetworkUtils utils = new NetworkUtils();
                            utils.emitSocket("ValidateTTRescheduling" , obj);
                            Utils.toast(context , "Request sent to "+nameOfFac);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    }
                });
                dialog.show();


            }
        });


        {

            try {


                holder.subject.setText(object.get("Subject").toString());
                String time = object.getString("Time");
                int index = time.indexOf(".");
                if (index != -1) {
                    String hourOfLec = time.substring(0, index);
                    int hourOfLecIint = Integer.parseInt(hourOfLec);
                    String minOfLec = time.substring(index + 1);

                    if (hourOfLecIint >= 12) {
                        hourOfLecIint -= 12;
                        time = String.valueOf(hourOfLecIint) + "." + minOfLec + " PM";
                    } else {
                        time = String.valueOf(hourOfLecIint) + "." + minOfLec + " AM";

                    }

                }

                holder.time.setText(time);
                holder.location.setText(object.get("Location").toString());
                holder.staff.setText(object.get("Staff").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView subject;
        TextView location;
        TextView time;
        TextView staff;
        CardView cardView;

        TextView reschedule;

        public DataObjectHolder(View itemView) {
            super(itemView);
            subject = (TextView) itemView.findViewById(R.id.subject);
            time = (TextView) itemView.findViewById(R.id.time);
            location = (TextView) itemView.findViewById(R.id.location);
            staff = (TextView) itemView.findViewById(R.id.staff);
            reschedule = (TextView) itemView.findViewById(R.id.tvreschedule);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            //   Log.i(LOG_TAG, "Adding Listener");
            //   itemView.setOnClickListener(this);
        }

    }



}