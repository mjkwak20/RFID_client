package nlcsjeju.signin;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import nlcsjeju.signin.Model.Location;

public class SelectLocation extends AppCompatActivity {
    RecyclerView locationList;
    static Location[] location;
    LocationAdapter adapter;

    class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder>{
        Context context;

        public LocationAdapter(Context context){
            this.context = context;
        }

        @Override
        public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.location_cell, parent, false);
            return new LocationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LocationViewHolder holder, int position) {
            holder.label.setText(SelectLocation.location[position].getName());
        }

        @Override
        public int getItemCount() {
            return SelectLocation.location.length;
        }
    }

    class LocationViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView label;
        public LocationViewHolder(View view){
            super(view);
            image = (ImageView) view.findViewById(R.id.locationImage);
            label = (TextView) view.findViewById(R.id.locationLabel);
        }

        public void bind(){

        }

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        locationList = (RecyclerView) findViewById(R.id.locationList);
        locationList.setHasFixedSize(true);

        // calculate appropriate width
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numberOfColumns = (int) (dpWidth / 100);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        locationList.setLayoutManager(layoutManager);

        adapter = new LocationAdapter(this);
        locationList.setAdapter(adapter);

//        locationList.setOnI
    }
}
