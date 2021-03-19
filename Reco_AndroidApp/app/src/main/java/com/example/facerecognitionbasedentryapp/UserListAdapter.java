package com.example.facerecognitionbasedentryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.Collection;

import static android.content.ContentValues.TAG;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{//} implements Filterable {

   // View view1;
    ArrayList<String> mId,mName,mEmail,mPhoneNumber;
    Context mcontext;

    public UserListAdapter(Context mcontext, ArrayList<String> mId, ArrayList<String> mName, ArrayList<String> mEmail,  ArrayList<String> mPhoneNumber){// boolean is_settings_clicked) {
        this.mId = mId;
        this.mName = mName;
        this.mcontext = mcontext;
        this.mEmail = mEmail;
        this.mPhoneNumber= mPhoneNumber;

        //this.mtextview1_all_ado = new ArrayList<>(mtextview1);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(mcontext).inflate(R.layout.user_list_adapter,parent,false);
        return new ViewHolder(view1);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv1.setBackground(null);
        holder.tv2.setBackground(null);
        holder.tv3.setBackground(null);
        holder.tv1.setText(mName.get(position));
        holder.tv2.setText(mPhoneNumber.get(position));
        holder.tv3.setText(mEmail.get(position));
    }

    @Override
    public int getItemCount() {
        return  mName.size();
    }

    //search filter in toolbar
    /*
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<String> filtered_list_ado;
            if (!isDdoFragment) {
                filtered_list_ado = new ArrayList<>();
                if (constraint.toString().isEmpty()) {
                    filtered_list_ado.addAll(mtextview1_all_ado);
                } else {
                    for (String address_ddo : mtextview1_all_ado) {
                        if (address_ddo.toLowerCase().contains(constraint.toString().toLowerCase().trim())) {
                            filtered_list_ado.add(address_ddo);
                        }//todo add message for no reults found
                    }
                }
            }else {
                filtered_list_ado = new ArrayList<>();
                if (constraint.toString().isEmpty()) {
                    filtered_list_ado.addAll(mtextview1_all);
                } else {
                    for (String address_ddo : mtextview1_all) {
                        if (address_ddo.toLowerCase().contains(constraint.toString().toLowerCase().trim())) {
                            filtered_list_ado.add(address_ddo);
                        }
                    }
                }
            }

            FilterResults filterResults_ddo = new FilterResults();
            filterResults_ddo.count = filtered_list_ado.size();
            filterResults_ddo.values = filtered_list_ado;

            return filterResults_ddo;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            if (!isDdoFragment) {
                mtextview1.clear();
                mtextview1.addAll((Collection<? extends String>) results.values);
                notifyDataSetChanged();
            }else {
                mtextview1.clear();
                mtextview1.addAll((Collection<? extends String>) results.values);
                notifyDataSetChanged();
            }
        }
    };

    public void show_suggestions(ArrayList<String> username) {

        if (!isDdoFragment) {
            this.mtextview1_all_ado = new ArrayList<>(username);
        }
        else {
            this.mtextview1_all = new ArrayList<>(username);
        }
    }

     */
    //end of search suggestions filter

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv1;
        TextView tv2,tv3;
        RelativeLayout relativeLayout;
        TextView districtTextview;

        CheckBox radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv1= itemView.findViewById(R.id.tvuser);
            tv2= itemView.findViewById(R.id.tvinfo);
            tv3= itemView.findViewById(R.id.district_info);
            relativeLayout = itemView.findViewById(R.id.relativeLayout2);
            districtTextview = itemView.findViewById(R.id.district_info);
            radioButton = itemView.findViewById(R.id.offer_select);

        }
        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            //Toast.makeText(mcontext,"Going to ViewRecord with position "+position +" id "+mId.get(position),Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mcontext, ViewRecords.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("onclick", "onClick: ");
            String id = mId.get(position);
            Integer nid= Integer.valueOf(id)-1;
            intent.putExtra("id", nid.toString());
            //Toast.makeText(mcontext,"Going to ViewRecord with id" + nid ,Toast.LENGTH_LONG).show();
            mcontext.startActivity(intent);////not working
        }
    }

}
