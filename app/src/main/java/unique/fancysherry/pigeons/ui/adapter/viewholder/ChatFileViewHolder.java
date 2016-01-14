package unique.fancysherry.pigeons.ui.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import unique.fancysherry.pigeons.io.model.Message;

/**
 * Created by fancysherry on 16-1-15.
 */
public class ChatFileViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView avatar;
    TextView chat_message;
    Context context;
    public View view;

    public ChatFileViewHolder(View itemView, Context pContext) {
        super(itemView);
        this.context = pContext;
        this.view = itemView;
    }

    public ChatFileViewHolder(View itemView, Context pContext, boolean right) {
        super(itemView);
        this.context = pContext;
        this.view = itemView;
    }

    public void onBindViewHolder(Message pMessage) {
        view.setTag(null);
    }

}
