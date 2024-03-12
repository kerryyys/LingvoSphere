package com.lingvosphere.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lingvosphere.CommentActivity;
import com.lingvosphere.R;
import com.lingvosphere.model.PostModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {
    // The RecyclerView requests views, and binds the views to their data, by calling methods in the adapter.
    private List<PostModel> list;
    static Context context;

    public PostAdapter(List<PostModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    // Inflate the each post_item
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_items, parent, false);
        return new PostHolder(view);
    }

    // Bind data to post_item
    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.usernameTv.setText(list.get(position).getUserName());

        String postDescriptiom = list.get(position).getPostDescription();

        if (!postDescriptiom.equals("no_postText")){
            holder.postDescriptionTv.setVisibility(View.VISIBLE);
            holder.postDescriptionTv.setText(postDescriptiom);
        }

        if (!list.get(position).getImageUrl().equals("no_postImage")){
            Glide.with(context.getApplicationContext())
                    .load(list.get(position).getProfile())
                    .load(list.get(position).getImageUrl())
                    .timeout(7000)
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getProfile())
                .placeholder(R.drawable.me)
                .timeout(6500)
                .into(holder.profileImage);

        holder.clickListener();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        // Each individual element in the list is defined by a view holder object.
        private CircleImageView profileImage;
        private TextView usernameTv, timeTv, postDescriptionTv;
        private ImageView imageView;
        private ImageButton likeBtn, commentBtn, shareBtn;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            imageView = itemView.findViewById(R.id.imageView);
            usernameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
//            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            postDescriptionTv = itemView.findViewById(R.id.postDescriptionTv);
        }

        public void clickListener() {

            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Pass postId to CommentActivity
                    String postId = list.get(getAdapterPosition()).getPostId();
                    Intent intent = new Intent(v.getContext(), CommentActivity.class);
                    intent.putExtra("postId", postId);
                    context.startActivity(intent);
                }
            });
        }
    }

}
