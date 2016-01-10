package unique.fancysherry.pigeons.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import unique.fancysherry.pigeons.R;
import unique.fancysherry.pigeons.io.model.User;

/**
 * Created by fancysherry on 16-1-10.
 */
public class SearchMemberAdapter extends RecyclerView.Adapter<SearchMemberAdapter.ViewHolder> implements View.OnClickListener {

    private List<User> view_type_array = null;

    private Context context;

    public SearchMemberAdapter(Context pContext) {
        this.context = pContext;
        setHasStableIds(true);// 必须该方法，不然可能bindviewtype不执行
    }

    public void setData(List<User> items) {
        this.view_type_array = items;
        notifyDataSetChanged();
    }

    @Override
    public SearchMemberAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_list_item, parent, false);
        itemView.setOnClickListener(this);
        return new ViewHolder(itemView, this, context);
    }

    @Override
    public void onBindViewHolder(SearchMemberAdapter.ViewHolder holder, int position) {
//        holder.search_portrait
        holder.search_nickname.setText(view_type_array.get(position).nickname);
        holder.search_content.setText(view_type_array.get(position).description);
        holder.view.setTag(view_type_array.get(position));
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

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null && v.getTag() != null) {
            // 注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (User) v.getTag());
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, User data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView search_portrait;
        TextView search_nickname;
        TextView search_content;
        Context context;
        View view;

        public ViewHolder(View itemView, SearchMemberAdapter searchmemberAdapter, Context pContext) {
            super(itemView);
            this.context = pContext;
            this.search_portrait =
                    (SimpleDraweeView) itemView.findViewById(R.id.search_item_message_profile);
            this.search_nickname = (TextView) itemView.findViewById(R.id.search_item_message_nickname);
            this.search_content =
                    (TextView) itemView.findViewById(R.id.search_item_message_text_content);
            this.view = itemView;
        }


    }


}
