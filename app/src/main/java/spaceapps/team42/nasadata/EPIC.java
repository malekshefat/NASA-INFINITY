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
public class EPIC extends Fragment implements AdapterView.OnItemSelectedListener {

    private AppCompatSpinner type, quality;
    private RecyclerView recyclerView;
    private String[] types, quals;
    private EpicAdapter adapter;

    public EPIC() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_epic, container, false);

        type = v.findViewById(R.id.type_spinner);
        type.setOnItemSelectedListener(this);
        types = getResources().getStringArray(R.array.EPIC_type);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item, types);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typesAdapter);

        quality = v.findViewById(R.id.quality_spinner);
        quality.setOnItemSelectedListener(this);
        quals = getResources().getStringArray(R.array.EPIC_qual);
        ArrayAdapter<String> qualitiesAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, quals);
        qualitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quality.setAdapter(qualitiesAdapter);

        recyclerView = v.findViewById(R.id.epic_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        loadEpic();

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadEpic() {
        // Initialize a new StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                EpicURL(types[type.getSelectedItemPosition()]),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with response string
                        ArrayList<MarsItemData> data = parseJSON(response);
                        adapter = new EpicAdapter(data, getActivity());
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
            JSONArray array = new JSONArray(response);
            Log.d("MSL", "parseJSON: " + response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                String id = object.getString("identifier");
                String caption = object.getString("caption");
                String date = object.getString("date").
                        split(" ")[0].replace("-", "/");
                String image = EpicImageURL(types[type.getSelectedItemPosition()],
                        quals[quality.getSelectedItemPosition()], id, date);

                MarsItemData itemData = new MarsItemData();
                itemData.setCaption(caption);
                itemData.setDate(date);
                itemData.setImage(image);
                itemData.setId(id);
                data.add(itemData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String EpicURL(String type) {
        return "https://api.nasa.gov/EPIC/api/enhanced?api_key=0Mz06HDTIvATrZhQrAj6aG1LTVZuyL4XdPjoo63N";
    }

    private String EpicImageURL(String type, String quality, String id, String date) {
        String prf = type.equalsIgnoreCase("natural") ? "1b" : "RGB";
        String prf2 = quality.equalsIgnoreCase("png") ? "png" : "jpg";
        return "https://epic.gsfc.nasa.gov/archive/" + type + "/" + date + "/" +
                quality + "/epic_" + prf + "_" + id + "." + prf2;
    }
}
