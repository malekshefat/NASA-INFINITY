package spaceapps.team42.nasadata;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ImageZoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        String url = getIntent().getExtras().getString("image");

        TouchImageView imageView = findViewById(R.id.image_zoom);
        Glide.with(this)
                .load(url)
                .into(imageView);

    }
}
