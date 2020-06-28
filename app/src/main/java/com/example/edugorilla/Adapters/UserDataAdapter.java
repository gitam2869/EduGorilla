package com.example.edugorilla.Adapters;

import android.media.MediaRouter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edugorilla.ModelClasses.UserDataInfo;
import com.example.edugorilla.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.ViewHolder>
{

    private ArrayList<UserDataInfo> userDataInfoArrayList;

    public UserDataAdapter(ArrayList<UserDataInfo> userDataInfoArrayList)
    {
        this.userDataInfoArrayList = userDataInfoArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.all_userdata_list, parent,false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position)
    {
        UserDataInfo userDataInfo = userDataInfoArrayList.get(position);

        holder.textViewId.setText("Id : "+userDataInfo.getId());
        holder.textViewName.setText("Name : "+userDataInfo.getName());
        holder.textViewEmail.setText("Email Id : "+userDataInfo.getEmail());

    }

    @Override
    public int getItemCount()
    {
        return userDataInfoArrayList.size();
    }


    //create variable

    private OnItemClickListener mListener;

    //interface

    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        MaterialCardView materialCardAllNews;
        TextView textViewId;
        TextView textViewName;
        TextView textViewEmail;


        public ViewHolder(@NonNull View itemView,  final OnItemClickListener listener)
        {
            super(itemView);

            materialCardAllNews = itemView.findViewById(R.id.idCardViewAllNews);
            textViewEmail = itemView.findViewById(R.id.idTextViewEmailAllUserData);
            textViewName = itemView.findViewById(R.id.idTextViewNameAllUserData);
            textViewId = itemView.findViewById(R.id.idTextViewIdAllUserData);

            materialCardAllNews.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

//            textViewSource.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    if(listener != null)
//                    {
//                        int position = getAdapterPosition();
//
//                        if(position != RecyclerView.NO_POSITION)
//                        {
//                            listener.onItemClick1(position);
//                        }
//                    }
//                }
//            });
        }
    }
}
