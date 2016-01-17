package unique.fancysherry.pigeons.ui.adapter.viewholder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.io.Constants;
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
        avatar=(SimpleDraweeView)itemView.findViewById(R.id.layout_chat_list_item_text_mine_avator);
        this.view = itemView;
    }

    public ChatTextViewHolder(View itemView, Context pContext, boolean right) {
        super(itemView);
        this.chat_message = (TextView) itemView.findViewById(R.id.layout_chat_list_item_text_other_content);
        this.context = pContext;
        avatar=(SimpleDraweeView)itemView.findViewById(R.id.layout_chat_list_item_text_other_avatar);
        this.view = itemView;
    }

    public void onBindViewHolder(Message pMessage) {
        chat_message.setText(pMessage.message);
        avatar.setImageURI(Uri.parse(Constants.BASE_URL+"avatar/"+pMessage.from));
        view.setTag(null);
    }

}
