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
import unique.fancysherry.pigeons.util.LogUtil;

/**
 * Created by fancysherry on 16-1-15.
 */
public class ChatImageViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView avatar;
    SimpleDraweeView chat_message;
    Context context;
    public View view;

    public ChatImageViewHolder(View itemView, Context pContext) {
        super(itemView);
        this.context = pContext;
        this.view = itemView;
        chat_message = (SimpleDraweeView) itemView.findViewById(R.id.layout_chat_list_item_image_mine_content);
        avatar = (SimpleDraweeView) itemView.findViewById(R.id.layout_chat_list_item_image_mine_avatar);
    }

    public ChatImageViewHolder(View itemView, Context pContext, boolean right) {
        super(itemView);
        this.context = pContext;
        this.view = itemView;
        chat_message = (SimpleDraweeView) itemView.findViewById(R.id.layout_chat_list_item_image_other_content);
        avatar = (SimpleDraweeView) itemView.findViewById(R.id.layout_chat_list_item_image_other_avatar);
    }

    public void onBindViewHolder(Message pMessage) {
        String image_md5 = pMessage.message.replace("image:", "");
        String image_url = Constants.BASE_URL + "uploads/" + image_md5;
        LogUtil.e(image_url);
        chat_message.setImageURI(Uri.parse(image_url));
        view.setTag(null);
    }

}
