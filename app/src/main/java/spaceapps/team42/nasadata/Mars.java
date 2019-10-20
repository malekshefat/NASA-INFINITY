package spaceapps.team42.nasadata;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Mars extends Fragment implements AdapterView.OnItemSelectedListener {

    private AppCompatSpinner rover, camera;
    private RecyclerView recyclerView;
    private String[] cameras, rovers;
    private MarsAdapter adapter;

    public Mars() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mars, container, false);

        camera = v.findViewById(R.id.camera_spinner);
        camera.setOnItemSelectedListener(this);
        cameras = getResources().getStringArray(R.array.mars_cameras);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, cameras);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        camera.setAdapter(typesAdapter);

        rover = v.findViewById(R.id.rover_spinner);
        rover.setOnItemSelectedListener(this);
        rovers = getResources().getStringArray(R.array.mars_rovers);
        ArrayAdapter<String> qualitiesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, rovers);
        qualitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rover.setAdapter(qualitiesAdapter);

        recyclerView = v.findViewById(R.id.epic_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        loadMars();

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadMars() {
        // Initialize a new StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                MarsURL(cameras[camera.getSelectedItemPosition()], 1, 1),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with response string
                        ArrayList<MarsItemData> data = parseJSON(response);
                        adapter = new MarsAdapter(data, getActivity());
                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when get error
                        Toast.makeText(getContext(), "Error...", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add StringRequest to the RequestQueue
        VolleySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private ArrayList<MarsItemData> parseJSON(String response) {
        ArrayList<MarsItemData> data = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(response);
            JSONArray photos = object.getJSONArray("photos");
            for (int i = 0; i < photos.length(); i++) {
                JSONObject photo = photos.getJSONObject(i);
                String date = photo.getString("earth_date");
                String image = photo.getString("img_src");
                Log.d("MSL", "parseJSON: " + image);
                Log.d("MSL", "parseJSON: " + date);

                MarsItemData itemData = new MarsItemData();
                itemData.setDate(date);
                itemData.setImage(image);
                data.add(itemData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String MarsURL(String rover, int page, int sol) {
        return "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?sol=200&page=1&api_key=0Mz06HDTIvATrZhQrAj6aG1LTVZuyL4XdPjoo63N";
    }
}
