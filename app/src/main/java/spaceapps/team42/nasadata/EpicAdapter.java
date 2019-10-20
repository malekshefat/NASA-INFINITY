package spaceapps.team42.nasadata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class EpicAdapter extends RecyclerView.Adapter<EpicAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MarsItemData> data;

    public EpicAdapter(ArrayList<MarsItemData> data, Context c) {
        this.context = c;
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.epic_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarsItemData itemData = data.get(position);
        Glide.with(context)
                .load(itemData.getImage())
                .dontAnimate()
                .into(holder.imageView);
        holder.caption.setText(itemData.getCaption());
        holder.date.setText(itemData.getDate());
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatImageView imageView;
        AppCompatTextView caption, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            caption = itemView.findViewById(R.id.item_caption);
            date = itemView.findViewById(R.id.item_date);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ActivityOptionsCompat activityOptionsCompat =
                    ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity) context,
                                    imageView,
                                    "image");
            Intent intent = new Intent(context, ImageZoom.class);
            intent.putExtra("image", data.get(getAdapterPosition()).getImage());
            context.startActivity(intent, activityOptionsCompat.toBundle());
        }
    }
}
