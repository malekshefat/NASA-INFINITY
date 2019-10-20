package spaceapps.team42.nasadata;


import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mousebird.maply.GlobeMapFragment;
import com.mousebird.maply.QuadImageTileLayer;
import com.mousebird.maply.RemoteTileInfo;
import com.mousebird.maply.RemoteTileSource;
import com.mousebird.maply.SphericalMercatorCoordSystem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class AsteroidNeoWs extends GlobeMapFragment implements View.OnClickListener {

    private FrameLayout main;
    private Random random = new Random();
    private ArrayList<AsteroidData> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle inState) {
        super.onCreateView(inflater, container, inState);

        // Do app specific setup logic.


        main = new FrameLayout(getContext());
        main.addView(baseControl.getContentView());

        loadAsteroid(2019, 10, 20);

        return main;
    }

    @Override
    protected MapDisplayType chooseDisplayType() {
        return MapDisplayType.Globe;
    }

    @Override
    protected void controlHasStarted() {
        // setup base layer tiles
        String cacheDirName = "stamen_watercolor";
        File cacheDir = new File(getActivity().getCacheDir(), cacheDirName);
        cacheDir.mkdir();
        RemoteTileSource remoteTileSource = new RemoteTileSource(new RemoteTileInfo("http://tile.stamen.com/watercolor/", "png", 0, 18));
        remoteTileSource.setCacheDir(cacheDir);
        SphericalMercatorCoordSystem coordSystem = new SphericalMercatorCoordSystem();

        // globeControl is the controller when using MapDisplayType.Globe
        // mapControl is the controller when using MapDisplayType.Map
        QuadImageTileLayer baseLayer = new QuadImageTileLayer(globeControl, coordSystem, remoteTileSource);
        baseLayer.setImageDepth(1);
        baseLayer.setSingleLevelLoading(false);
        baseLayer.setUseTargetZoomLevel(false);
        baseLayer.setCoverPoles(true);
        baseLayer.setHandleEdges(true);

        // add layer and position
        globeControl.addLayer(baseLayer);
        globeControl.animatePositionGeo(-3.6704803, 40.5023056, 5, 1.0);
    }

    private String AsteroidURL(int YYYY, int mm, int DD) {
        return "https://api.nasa.gov/neo/rest/v1/feed?start_date=" + YYYY + "-" + mm + "-" + DD +
                "&end_date=" + YYYY + "-" + mm + "-" + DD + "&api_key=" + getString(R.string.API_KEY);
    }

    private void loadAsteroid(final int YYYY, final int mm, final int DD) {
        // Initialize a new StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AsteroidURL(YYYY, mm, DD),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with response string
                        data = parseJSON(response, YYYY, mm, DD);
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

    private ArrayList<AsteroidData> parseJSON(String response, int YYYY, int mm, int DD) {
        ArrayList<AsteroidData> asteroidData = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(response);
            // int count = object.getInt("element_count");
            JSONObject astObject = object.getJSONObject("near_earth_objects");
            JSONArray asteroids = astObject.getJSONArray("" + YYYY + "-" + mm + "-" + DD);
            for (int i = 0; i < Math.min(asteroids.length(), 6); i++) {
                JSONObject asteroid = asteroids.getJSONObject(i);
                JSONObject estimated_diameter = asteroid.getJSONObject("estimated_diameter");
                JSONObject kilometers = estimated_diameter.getJSONObject("kilometers");
                JSONArray close_approach_data = asteroid.getJSONArray("close_approach_data");
                JSONObject rel_vel = ((JSONObject) close_approach_data.get(0)).getJSONObject("relative_velocity");
                JSONObject dist = ((JSONObject) close_approach_data.get(0)).getJSONObject("miss_distance");
                String name = asteroid.getString("name");
                double h = asteroid.getDouble("absolute_magnitude_h");
                double max_diam = kilometers.getDouble("estimated_diameter_max");
                double min_diam = kilometers.getDouble("estimated_diameter_min");
                double kmps = rel_vel.getDouble("kilometers_per_second");
                double km = dist.getDouble("kilometers");
                boolean dangerous = asteroid.getBoolean("is_potentially_hazardous_asteroid");

                AsteroidData data = new AsteroidData();
                data.setName(name);
                data.setH(h);
                data.setMax_diam(max_diam);
                data.setMin_diam(min_diam);
                data.setRel_vel(kmps);
                data.setDistance(km);
                data.setDangerous(dangerous);

                addAsteroid(i, dangerous ? android.R.color.holo_red_light : android.R.color.holo_green_light);
                Log.d("MSL", "asteroid #" + i);

                asteroidData.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return asteroidData;
    }

    private void addAsteroid(int pos, int color) {
        boolean r_l = random.nextBoolean();
        long x_l = (long) (random.nextFloat() * (getSize().x / 4));
        long x_r = x_l + (getSize().x * 3 / 4);
        long y = (long) (random.nextFloat() * getSize().y);
        ImageView asteroid = new ImageView(getContext());
        asteroid.setTag("" + pos);
        asteroid.setClickable(true);
        asteroid.setOnClickListener(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(75, 75);
        asteroid.setLayoutParams(layoutParams);
        asteroid.animate().translationX(r_l ? x_l : x_r);
        asteroid.animate().translationY(y);
        asteroid.setImageResource(R.drawable.asteroid);
        asteroid.setColorFilter(ContextCompat.getColor(getContext(), color),
                android.graphics.PorterDuff.Mode.SRC_IN);
        //Add View to Layout:
        main.addView(asteroid);
    }

    private Point getSize() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    @Override
    public void onClick(View v) {
        showDialog(data.get(Integer.valueOf(String.valueOf(v.getTag()))));
    }

    private void showDialog(AsteroidData data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ViewGroup viewGroup = main.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.asteroid_dialog,
                viewGroup, false);
        TextView name = dialogView.findViewById(R.id.name);
        TextView abs_mag = dialogView.findViewById(R.id.abs_mag);
        TextView diameter = dialogView.findViewById(R.id.diameter);
        TextView rel_vel = dialogView.findViewById(R.id.rel_vel);
        TextView distance = dialogView.findViewById(R.id.distance);

        AppCompatButton ok = dialogView.findViewById(R.id.buttonOk);

        name.setText(data.getName());
        abs_mag.setText(String.format(getString(R.string.abs_mag), data.getH()));
        diameter.setText(String.format(getString(R.string.diameter), data.getMin_diam(), data.getMax_diam()));
        rel_vel.setText(String.format(getString(R.string.rel_vel), data.getRel_vel()));
        distance.setText(String.format(getString(R.string.distance), data.getDistance()));

        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}