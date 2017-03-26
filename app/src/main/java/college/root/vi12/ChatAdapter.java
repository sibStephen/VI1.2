package college.root.vi12;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by root on 26/2/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {

    Context context;
    ArrayList<Message> list;

    public ChatAdapter(ArrayList<Message> list , Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.chat , parent , false);
        return new ChatHolder(context ,v);

    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {

        Message message = list.get(position);
        holder.tvMessage.setText(message.getMessage());


    }

    @Override
    public int getItemCount() {
        if(list == null){
            return  0;
        }else{
            return  list.size() ;
        }

    }
}
