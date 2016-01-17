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
public class ChatFileViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView avatar;
    TextView file_path;
    TextView file_md5;
    Context context;
    public View view;

    public ChatFileViewHolder(View itemView, Context pContext) {
        super(itemView);
        this.context = pContext;
        avatar = (SimpleDraweeView) itemView.findViewById(R.id.layout_chat_list_item_file_other_avatar);
        file_path = (TextView) itemView.findViewById(R.id.file_path);
        file_md5 = (TextView) itemView.findViewById(R.id.file_hash);
        this.view = itemView;
    }

    public ChatFileViewHolder(View itemView, Context pContext, boolean right) {
        super(itemView);
        this.context = pContext;
        avatar = (SimpleDraweeView) itemView.findViewById(R.id.layout_chat_list_item_file_other_avatar);
        file_path = (TextView) itemView.findViewById(R.id.file_path_other);
        file_md5 = (TextView) itemView.findViewById(R.id.file_hash_other);
        this.view = itemView;
    }

    public void onBindViewHolder(Message pMessage) {
        avatar.setImageURI(Uri.parse(Constants.BASE_URL + "avatar/" + pMessage.from));
        String[] parts = pMessage.message.split(",");
        String text_path = parts[0].replace("file:", "");
        String text_md5 = parts[1];
        file_path.setText(text_path);
        file_md5.setText(text_md5);
        view.setTag(null);
    }

}
