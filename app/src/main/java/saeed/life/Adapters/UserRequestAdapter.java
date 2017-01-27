package saeed.life.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import saeed.life.Model.DonationRequest;
import saeed.life.R;

import static saeed.life.R.id.city;

public class UserRequestAdapter extends BaseAdapter {

    private List<DonationRequest> requestsList;
    private Context context;
    private LayoutInflater inflater;
    private ProgressDialog progress;

    public UserRequestAdapter(Context context, List<DonationRequest> requestsList) {
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
            rootView = inflater.inflate(R.layout.user_request_item, null);
            holder = new Holder();
            holder.name = (TextView)rootView.findViewById(R.id.patient_name);
            holder.blood = (TextView)rootView.findViewById(R.id.blood_type);
            holder.city = (TextView)rootView.findViewById(city);
            holder.hospital = (TextView)rootView.findViewById(R.id.hospital);
            holder.phone = (TextView)rootView.findViewById(R.id.phone_to_connect);
            holder.delete = (Button)rootView.findViewById(R.id.delete);
            rootView.setTag(holder);
        }
        else {
            holder = (Holder)rootView.getTag();
        }
        holder.name.setText(requestsList.get(position).getName());
        holder.blood.setText(requestsList.get(position).getBloodType());
        holder.city.setText(requestsList.get(position).getCity());
        holder.hospital.setText(requestsList.get(position).getHospital());
        holder.phone.setText(requestsList.get(position).getPhone());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = ProgressDialog.show(context, "", context.getString(R.string.delete_message));
                deleteRequest(requestsList.get(position), position);
            }
        });
        return rootView;
    }

    private void deleteRequest(final DonationRequest donationRequest, final int position){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Requests");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null) {
                            Map<String, Object> requestsMap = (Map<String, Object>) dataSnapshot.getValue();
                            for (Map.Entry<String, Object> entry : requestsMap.entrySet()) {
                                Map singleRequest = (Map) entry.getValue();
                                DonationRequest donationRequestData = new DonationRequest(singleRequest.get("name").toString(),
                                        singleRequest.get("bloodType").toString(),
                                        singleRequest.get("city").toString(),
                                        singleRequest.get("hospital").toString(),
                                        singleRequest.get("phone").toString(),
                                        singleRequest.get("userId").toString(),
                                        singleRequest.get("userName").toString());
                                if(donationRequest.equals(donationRequestData)){
                                    myRef.child("Requests").child(entry.getKey()).removeValue();
                                    progress.cancel();
                                    update(position);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void update(int position){
        requestsList.remove(position);
        this.notifyDataSetChanged();
    }

    public class Holder {
        TextView name, blood, city, hospital, phone;
        Button delete;
    }
}
