package spaceapps.team42.nasadata;


import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class APOD extends Fragment implements CompoundButton.OnCheckedChangeListener {


    private AppCompatTextView title, desc;
    private AppCompatImageView imageView;
    private SwitchCompat switchCompat;

    public APOD() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_apod, container, false);

        title = v.findViewById(R.id.image_title);
        desc = v.findViewById(R.id.image_desc);
        imageView = v.findViewById(R.id.apod_image);
        switchCompat = v.findViewById(R.id.apod_auto_wallpaper);
        switchCompat.setOnCheckedChangeListener(this);
        switchCompat.setChecked(App.getState("auto_wallpaper"));

        loadAsteroid();

        return v;
    }


    private String AsteroidURL() {
        return "https://api.nasa.gov/planetary/apod?api_key=" + getString(R.string.API_KEY);
    }

    private void loadAsteroid() {
        // Initialize a new StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                AsteroidURL(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with response string
                        ApodData data = parseJSON(response);
                        title.setText(data.getTitle());
                        desc.setText(data.getDesc());
                        Glide.with(getContext())
                                .asBitmap()
                                .load(data.getUrl())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                @Nullable Transition<? super Bitmap> transition) {
                                        imageView.setImageBitmap(resource);
                                        if (switchCompat.isChecked())
                                            setWallpaper(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                    }
                                });
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

    private ApodData parseJSON(String response) {
        ApodData data = new ApodData();
        try {
            JSONObject object = new JSONObject(response);
            String desc = object.getString("explanation");
            String imgUrl = object.getString("url");
            String title = object.getString("title");

            data.setTitle(title);
            data.setDesc(desc);
            data.setUrl(imgUrl);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        App.saveState("auto_wallpaper", isChecked);
    }

    private void setWallpaper(Bitmap bitmap) {
        WallpaperManager manager = WallpaperManager.getInstance(getContext());
        try {
            manager.setBitmap(bitmap);
            Toast.makeText(getContext(), "Wallpaper set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error!", Toast.LENGTH_SHORT).show();
        }
    }
}
