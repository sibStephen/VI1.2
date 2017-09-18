
package college.root.vi12.AdminActivities;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import college.root.vi12.R;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<JSONObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subject;
        TextView location;
        TextView time;
        TextView staff;

        public DataObjectHolder(View itemView) {
            super(itemView);
            subject = (TextView) itemView.findViewById(R.id.subject);
            time = (TextView) itemView.findViewById(R.id.time);
            location = (TextView) itemView.findViewById(R.id.location);
            staff = (TextView) itemView.findViewById(R.id.staff);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        MyRecyclerViewAdapter.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<JSONObject> myDataset) {
        mDataset = myDataset;
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
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        JSONObject object = mDataset.get(position);
        try {
            holder.subject.setText(object.get("Subject").toString());
            String time = object.getString("Time");
            int index = time.indexOf(".");
            String hourOfLec = time.substring(0, index);
            int hourOfLecIint = Integer.parseInt(hourOfLec);
            String minOfLec = time.substring(index+1);

            if (hourOfLecIint >= 12){
                hourOfLecIint -= 12;
                time = String.valueOf(hourOfLecIint) +"."+ minOfLec + " PM";
            }else{
                time = String.valueOf(hourOfLecIint) +"."+ minOfLec + " AM";

            }

            holder.time.setText(time);
            holder.location.setText(object.get("Location").toString());
            holder.staff.setText(object.get("Staff").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void addItem(JSONObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }
}