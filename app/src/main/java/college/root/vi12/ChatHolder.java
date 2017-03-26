package college.root.vi12;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 26/2/17.
 */

public class ChatHolder extends RecyclerView.ViewHolder {

    TextView tvMessage;
    Context context;

    public ChatHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        tvMessage = (TextView)itemView.findViewById(R.id.tvMsg);

    }
}
