package unique.fancysherry.pigeons.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import unique.fancysherry.pigeons.R;

public class SearchAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> items;
    private LayoutInflater mLayoutInflater;
    private boolean mIsFilterList;

    public SearchAdapter(Context context, ArrayList<String> itmes, boolean isFilterList) {
        this.mContext = context;
        this.items = itmes;
        this.mIsFilterList = isFilterList;
    }


    public void updateList(ArrayList<String> filterList, boolean isFilterList) {
        this.items = filterList;
        this.mIsFilterList = isFilterList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if (v == null) {

            holder = new ViewHolder();

            mLayoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

            v = mLayoutInflater.inflate(R.layout.layout_search_list_history_item, parent, false);
            holder.txtItem = (TextView) v.findViewById(R.id.search_item_text);
            v.setTag(holder);
        } else {

            holder = (ViewHolder) v.getTag();
        }

        holder.txtItem.setText(items.get(position));

        Drawable searchDrawable, recentDrawable;

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            searchDrawable = mContext.getResources().getDrawable(R.drawable.searcview_ic_magnify_grey600_24dp, null);
            recentDrawable = mContext.getResources().getDrawable(R.drawable.searcview_ic_backup_restore_grey600_24dp, null);

        } else {
            searchDrawable = mContext.getResources().getDrawable(R.drawable.searcview_ic_magnify_grey600_24dp);
            recentDrawable = mContext.getResources().getDrawable(R.drawable.searcview_ic_backup_restore_grey600_24dp);

        }
        if (mIsFilterList) {
            holder.txtItem.setCompoundDrawablesWithIntrinsicBounds(searchDrawable, null, null, null);
        } else {
            holder.txtItem.setCompoundDrawablesWithIntrinsicBounds(recentDrawable, null, null, null);

        }
        return v;
    }

}

class ViewHolder {

    TextView txtItem;
}





