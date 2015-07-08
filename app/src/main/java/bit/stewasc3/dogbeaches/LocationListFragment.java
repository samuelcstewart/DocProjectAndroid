package bit.stewasc3.dogbeaches;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import UserAPI.Location;
import UserAPI.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationListFragment extends ListFragment
{
    private ArrayList<Location> mLocations;
    private LocationAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mLocations = new ArrayList<>();
        mAdapter = new LocationAdapter(mLocations);
        setListAdapter(mAdapter);
        getLocations();
    }

    // Call RestClient to get locations. Implement filters, local storage and distance options
    // later.
    public void getLocations()
    {
        // TODO: Refine later to get locations close to user, other filtering options
        RestClient.get().getAllLocations(new Callback<ArrayList<Location>>()
        {
            @Override
            public void success(ArrayList<Location> locations, Response response)
            {
                mLocations.addAll(locations);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error)
            {
                Toast.makeText(getActivity().getApplicationContext(),
                        error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        // Pass the array of locations, and current selected location to ViewPager.
        Intent i = new Intent(getActivity(), LocationPagerActivity.class);
        i.putExtra(LocationPagerActivity.KEY_LOCATION_ARRAY, mLocations);
        i.putExtra(LocationPagerActivity.KEY_LOCATION_ARRAY_INDEX, position);
        startActivity(i);
    }

    private class LocationAdapter extends ArrayAdapter<Location>
    {
        public LocationAdapter(ArrayList<Location> locations) { super(getActivity(), 0, locations);}

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
           if(convertView == null)
           {
               convertView = getActivity().getLayoutInflater()
                       .inflate(R.layout.location_list_item, null);
           }

            Location l = getItem(position);

            TextView titleTextView = (TextView)convertView.findViewById(R.id.locationListTitleTextView);
            titleTextView.setText(l.getName());

            ImageView thumbImageView = (ImageView)convertView.findViewById(R.id.locationListThumbImageView);
            Picasso.with(getActivity()).load(l.getImage().getThumbnail()).into(thumbImageView);

            TextView statusTextView = (TextView)convertView.findViewById(R.id.locationListStatusTextView);
            statusTextView.setText("Dogs: " + l.getDogStatus());

            return convertView;
        }
    }
}
