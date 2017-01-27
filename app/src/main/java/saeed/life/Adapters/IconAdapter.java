package saeed.life.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import saeed.life.R;

public class IconAdapter extends BaseAdapter {

    private Context context;
    private int[] icons;
    private LayoutInflater inflater;

    public IconAdapter(Context context, int[] icons) {
        this.context = context;
        this.icons = icons;
        inflater = ( LayoutInflater )this.context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        Holder holder;

        if (rootView == null){
            rootView = inflater.inflate(R.layout.icon_item, null);
            holder = new Holder();
            holder.icon = (ImageView)rootView.findViewById(R.id.icon);
            rootView.setTag(holder);
        }
        else {
            holder = (Holder)rootView.getTag();
        }
        holder.icon.setImageResource(icons[position]);
        return rootView;
    }

    public class Holder {
        ImageView icon;
    }
}
