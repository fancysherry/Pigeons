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
import unique.fancysherry.pigeons.io.model.Group;
import unique.fancysherry.pigeons.io.model.User;

/**
 * Created by fancysherry on 16-1-10.
 */
public class AllGroupAdapter extends RecyclerView.Adapter<AllGroupAdapter.ViewHolder> implements View.OnClickListener {

    private List<Group> view_type_array = null;

    private Context context;

    public AllGroupAdapter(Context pContext) {
        this.context = pContext;
        setHasStableIds(true);// 必须该方法，不然可能bindviewtype不执行
    }

    public void setData(List<Group> items) {
        this.view_type_array = items;
        notifyDataSetChanged();
    }

    @Override
    public AllGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_list_item, parent, false);
        itemView.setOnClickListener(this);
        return new ViewHolder(itemView, this, context);
    }

    @Override
    public void onBindViewHolder(AllGroupAdapter.ViewHolder holder, int position) {
//        holder.search_portrait
        holder.contact_groupname.setText(view_type_array.get(position).groupname);
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
            mOnItemClickListener.onItemClick(v, (Group) v.getTag());
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, Group data);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView contact_portrait;
        TextView contact_groupname;
        TextView contact_content;
        Context context;
        View view;

        public ViewHolder(View itemView, AllGroupAdapter pAllGroupAdapter, Context pContext) {
            super(itemView);
            this.context = pContext;
            this.contact_portrait =
                    (SimpleDraweeView) itemView.findViewById(R.id.contact_item_message_profile);
            this.contact_groupname = (TextView) itemView.findViewById(R.id.contact_item_message_nickname);
            this.contact_content =
                    (TextView) itemView.findViewById(R.id.contact_item_message_text_content);
            this.view = itemView;
        }


    }


}
