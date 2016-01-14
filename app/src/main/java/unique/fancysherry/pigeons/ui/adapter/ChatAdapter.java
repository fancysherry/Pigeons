package unique.fancysherry.pigeons.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.account.AccountManager;
import unique.fancysherry.pigeons.io.model.Message;
import unique.fancysherry.pigeons.io.model.User;
import unique.fancysherry.pigeons.ui.adapter.viewholder.ChatFileViewHolder;
import unique.fancysherry.pigeons.ui.adapter.viewholder.ChatImageViewHolder;
import unique.fancysherry.pigeons.ui.adapter.viewholder.ChatTextViewHolder;
import unique.fancysherry.pigeons.ui.adapter.viewholder.ChatVoiceViewHolder;

/**
 * Created by fancysherry on 16-1-10.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> view_type_array = null;

    private Context context;

    private String current_username;

    public ChatAdapter(Context pContext) {
        this.context = pContext;
        setHasStableIds(true);// 必须该方法，不然可能bindviewtype不执行
        current_username = AccountManager.getInstance().getCurrentUser().getAccountBean().username;
    }

    public void setData(List<Message> items) {
        this.view_type_array = items;
//        for (Message mMessage : view_type_array) {
//            if (mMessage.from.equals(current_username))
//                mMessage.type = Message.Type.TEXT;
//            else
//                mMessage.type = Message.Type.TEXT_OTHER;
//        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        if (Message.Type.TEXT.ordinal() == viewType) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list_item_text_mine,
                            parent,
                            false);
            return new ChatTextViewHolder(itemView, context);
        } else if (Message.Type.TEXT_OTHER.ordinal() == viewType) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.layout_chat_list_item_text_other,
                            parent, false);
            return new ChatTextViewHolder(itemView, context, true);
        } else if (Message.Type.IMAGE.ordinal() == viewType) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list_item_image_mine,
                            parent,
                            false);
            return new ChatImageViewHolder(itemView, context);
        } else if (Message.Type.IMAGE_OTHER.ordinal() == viewType) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.layout_chat_list_item_image_other,
                            parent, false);
            return new ChatImageViewHolder(itemView, context, true);
        } else if (Message.Type.VOICE.ordinal() == viewType) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list_item_voice_mine,
                            parent,
                            false);
            return new ChatVoiceViewHolder(itemView, context);
        } else if (Message.Type.VOICE_OTHER.ordinal() == viewType) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.layout_chat_list_item_voice_other,
                            parent, false);
            return new ChatVoiceViewHolder(itemView, context, true);
        } else if (Message.Type.FILE.ordinal() == viewType) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list_item_file_mine,
                            parent,
                            false);
            return new ChatFileViewHolder(itemView, context);
        } else if (Message.Type.FILE_OTHER.ordinal() == viewType) {
            itemView =
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.layout_chat_list_item_file_other,
                            parent, false);
            return new ChatFileViewHolder(itemView, context, true);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatTextViewHolder) {
            ChatTextViewHolder mChatTextViewHolder = (ChatTextViewHolder) holder;
            mChatTextViewHolder.onBindViewHolder(view_type_array.get(position));
            mChatTextViewHolder.view.setTag(view_type_array.get(position));
        } else if (holder instanceof ChatImageViewHolder) {
            ChatImageViewHolder mChatImageViewHolder = (ChatImageViewHolder) holder;
            mChatImageViewHolder.onBindViewHolder(view_type_array.get(position));
            mChatImageViewHolder.view.setTag(view_type_array.get(position));
        } else if (holder instanceof ChatVoiceViewHolder) {
            ChatVoiceViewHolder mChatVoiceViewHolder = (ChatVoiceViewHolder) holder;
            mChatVoiceViewHolder.onBindViewHolder(view_type_array.get(position));
            mChatVoiceViewHolder.view.setTag(view_type_array.get(position));
        } else if (holder instanceof ChatFileViewHolder) {
            ChatFileViewHolder mChatFileViewHolder = (ChatFileViewHolder) holder;
            mChatFileViewHolder.onBindViewHolder(view_type_array.get(position));
            mChatFileViewHolder.view.setTag(view_type_array.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Log.e("position:", String.valueOf(position));

        // Implement your logic here
        if (view_type_array.get(position).type.equals(Message.Type.TEXT)) {
            return Message.Type.TEXT.ordinal();
        } else if (view_type_array.get(position).type.equals(Message.Type.IMAGE)) {
            return Message.Type.IMAGE.ordinal();
        } else if (view_type_array.get(position).type.equals(Message.Type.VOICE)) {
            return Message.Type.VOICE.ordinal();
        } else if (view_type_array.get(position).type.equals(Message.Type.FILE)) {
            return Message.Type.FILE.ordinal();
        } else if (view_type_array.get(position).type.equals(Message.Type.MIX)) {
            return Message.Type.MIX.ordinal();
        } else if (view_type_array.get(position).type.equals(Message.Type.IMAGE_OTHER)) {
            return Message.Type.IMAGE_OTHER.ordinal();
        } else if (view_type_array.get(position).type.equals(Message.Type.VOICE_OTHER)) {
            return Message.Type.VOICE_OTHER.ordinal();
        } else if (view_type_array.get(position).type.equals(Message.Type.FILE_OTHER)) {
            return Message.Type.FILE_OTHER.ordinal();
        } else if (view_type_array.get(position).type.equals(Message.Type.TEXT_OTHER)) {
            return Message.Type.TEXT_OTHER.ordinal();
        } else {
            return Message.Type.MIX_OTHER.ordinal();
        }

    }

    @Override
    public int getItemCount() {
        if (view_type_array == null)
            return 0;
        else
            return view_type_array.size();
    }

    @Override
    public long getItemId(int position) {
        return view_type_array.get(position).hashCode();
    }


}
