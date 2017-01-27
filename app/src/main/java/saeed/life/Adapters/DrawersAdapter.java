package saeed.life.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import saeed.life.Activities.ProfileActivity;
import saeed.life.Activities.UserRequestsActivity;
import saeed.life.Model.NavItem;
import saeed.life.Model.User;
import saeed.life.R;

public class DrawersAdapter extends BaseAdapter {

    private Context context;
    private List<NavItem> navItems;
    private LayoutInflater inflater;
    private User user;

    public DrawersAdapter(Context context, List<NavItem> navItems, User user) {
        this.context = context;
        this.navItems = navItems;
        inflater = ( LayoutInflater )this.context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.user = user;
    }

    @Override
    public int getCount() {
        return navItems.size();
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
            rootView = inflater.inflate(R.layout.drawer_item, null);
            holder = new Holder();
            holder.title = (TextView)rootView.findViewById(R.id.title);
            holder.icon = (ImageView)rootView.findViewById(R.id.icon);
            rootView.setTag(holder);
        }
        else {
            holder = (Holder)rootView.getTag();
        }
        holder.title.setText( navItems.get(position).getTitle() );
        holder.icon.setImageResource(navItems.get(position).getIcon());
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == 0){
                    context.startActivity(new Intent(context, ProfileActivity.class));
                }
                else if(position == 1){
                    context.startActivity(new Intent(context, UserRequestsActivity.class));
                }
            }
        });
        return rootView;
    }

    public class Holder {
        TextView title;
        ImageView icon;
    }
}
