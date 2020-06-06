package com.example.leathertecshoe.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leathertecshoe.R;
import com.example.leathertecshoe.activities.OrderDetailsUsersActivity;
import com.example.leathertecshoe.models.ModelOrderShop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderShop extends RecyclerView.Adapter<AdapterOrderShop.HolderOrderShop>{
    private Context context;
    public ArrayList<ModelOrderShop> orderShopArrayList;

    public AdapterOrderShop(Context context, ArrayList<ModelOrderShop> orderShopArrayList) {
        this.context = context;
        this.orderShopArrayList = orderShopArrayList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller, parent, false);
        return new HolderOrderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderShop holder, int position) {

        ModelOrderShop modelOrderShop = orderShopArrayList.get(position);
        final String orderId = modelOrderShop.getOrderId();
        String orderBy = modelOrderShop.getOrderBy();
        String orderCost = modelOrderShop.getOrderCost();
        String orderStatus = modelOrderShop.getOrderStatus();
        String orderTime = modelOrderShop.getOrderTime();
        final String orderTo = modelOrderShop.getOrderTo();

        loadUserInfo(modelOrderShop, holder);

        holder.amountTv.setText("Amount: ৳:" + orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("OrderID" + orderId);
        if (orderStatus.equals("In Progress")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.sky));
        } else if (orderStatus.equals("Completed")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        } else if (orderStatus.equals("Cancelled")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.red));
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formateDate = DateFormat.format("dd/MM/yyyy", calendar).toString();

        holder.orderDateTv.setText(formateDate);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo", orderTo);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);
            }
        });
    }
    private void loadUserInfo(ModelOrderShop modelOrderShop, final AdapterOrderShop.HolderOrderShop holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderShop.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String email = "" + dataSnapshot.child("email").getValue();
                        holder.emailTv.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderShopArrayList.size();
    }

    class HolderOrderShop extends RecyclerView.ViewHolder{

        private  TextView orderIdTv, orderDateTv, emailTv, amountTv, statusTv;
        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }
}
