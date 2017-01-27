package saeed.life.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import saeed.life.Model.DonationRequest;
import saeed.life.R;

public class HomeRequestAdapter extends BaseAdapter {

    private List<DonationRequest> requestsList;
    private Context context;
    private LayoutInflater inflater;

    public HomeRequestAdapter(Context context, List<DonationRequest> requestsList) {
        this.requestsList = requestsList;
        this.context = context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return requestsList.size();
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
            rootView = inflater.inflate(R.layout.request_item, null);
            holder = new Holder();
            holder.user = (TextView)rootView.findViewById(R.id.user);
            holder.blood = (TextView)rootView.findViewById(R.id.blood_type);
            holder.name = (TextView)rootView.findViewById(R.id.patient_name);
            holder.city = (TextView)rootView.findViewById(R.id.city);
            holder.hospital = (TextView)rootView.findViewById(R.id.hospital);
            holder.phone = (TextView)rootView.findViewById(R.id.phone_to_connect);
            rootView.setTag(holder);
        }
        else {
            holder = (Holder)rootView.getTag();
        }
        holder.user.setText(requestsList.get(position).getUserName());
        holder.blood.setText(requestsList.get(position).getBloodType());
        holder.name.setText(requestsList.get(position).getName());
        holder.city.setText(requestsList.get(position).getCity());
        holder.hospital.setText(requestsList.get(position).getHospital());
        holder.phone.setText(requestsList.get(position).getPhone());

        return rootView;
    }

    public class Holder {
        TextView user, blood, name, city, hospital, phone;
    }
}
