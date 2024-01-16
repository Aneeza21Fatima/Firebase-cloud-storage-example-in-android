package com.example.part2_image_to_cloud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context m_context;
    private List<Upload> m_uploads;
    public ImageAdapter(Context context, List<Upload> uploads){
        m_context = context;
        m_uploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(m_context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = m_uploads.get(position);
        holder.textView.setText(uploadCurrent.getName());
        Picasso.with(m_context)
                .load(uploadCurrent.getImage_url())
                .fit()
                .centerCrop()
                .into((holder.imageView));
        int imageWidth = uploadCurrent.getImageWidth();
        int imageHeight = uploadCurrent.getImageHeight();

// Use image dimensions to set layout parameters if available
        if (imageWidth > 0 && imageHeight > 0) {
            ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
            layoutParams.height = calculateScaledHeight(imageWidth, imageHeight, holder.imageView.getWidth());
            holder.imageView.setLayoutParams(layoutParams);
        }



    }
    private int calculateScaledHeight(int originalWidth, int originalHeight, int targetWidth) {
        float scaleFactor = (float) targetWidth / originalWidth;
        return (int) (originalHeight * scaleFactor);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view);
            imageView = itemView.findViewById(R.id.image_view_upload);


        }
    }

    @Override
    public int getItemCount() {
        return m_uploads.size();
    }



}
