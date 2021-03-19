package com.example.facerecognitionbasedentryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class ViewRecordAdapter extends RecyclerView.Adapter<ViewRecordAdapter.ViewHolder>{//} implements Filterable {

   // View view1;
    ArrayList<String> entry;
    Context mcontext;

    public ViewRecordAdapter(Context mcontext, ArrayList<String> entry){// boolean is_settings_clicked) {

        this.entry = entry;
        this.mcontext= mcontext;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(mcontext).inflate(R.layout.view_record_adapter,parent,false);
        return new ViewHolder(view1);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv1.setBackground(null);
        holder.tv2.setBackground(null);
        //holder.tv1.setText(entry.get(position));

        ////
        String dateString = entry.get(position);
        String[] separated = dateString.split("T");
        //holder.tv1.setText(separated[0]); // this will contain "2016-10-02"
        //separated[1];

        String fromDateFormat = "yyyy-MM-dd";
        String fromdate =separated[0];
        String CheckFormat = "dd MMM yyyy";
        String dateStringFrom;
        DateFormat FromDF = new SimpleDateFormat(fromDateFormat);
        FromDF.setLenient(false);  // this is important!
        Date FromDate = null;
        try {
            FromDate = FromDF.parse(fromdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateStringFrom = new SimpleDateFormat(CheckFormat).format(FromDate);
        String xy = dateStringFrom.toString();
        String substr2 = separated[1].substring(0,5);

        holder.tv1.setText(xy);
       holder.tv2.setText(substr2);
    }

    @Override
    public int getItemCount() {
        return  entry.size();
        //return 5;
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

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv1,tv2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            tv1= itemView.findViewById(R.id.textView9);
            tv2= itemView.findViewById(R.id.textView10);

        }
    }

}
