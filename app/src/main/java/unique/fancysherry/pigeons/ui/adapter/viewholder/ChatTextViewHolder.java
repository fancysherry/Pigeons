package unique.fancysherry.pigeons.ui.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.io.model.Message;

/**
 * Created by fancysherry on 16-1-15.
 */
public class ChatTextViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView avatar;
    TextView chat_message;
    Context context;
    public View view;

    public ChatTextViewHolder(View itemView, Context pContext) {
        super(itemView);
        this.context = pContext;
        this.chat_message = (TextView) itemView.findViewById(R.id.layout_chat_list_item_text_mine_content);
        this.view = itemView;
    }

    public ChatTextViewHolder(View itemView, Context pContext, boolean right) {
        super(itemView);
        this.chat_message = (TextView) itemView.findViewById(R.id.layout_chat_list_item_text_other_content);
        this.context = pContext;
        this.view = itemView;
    }

    public void onBindViewHolder(Message pMessage) {
        chat_message.setText(pMessage.message);
        view.setTag(null);
    }

}
