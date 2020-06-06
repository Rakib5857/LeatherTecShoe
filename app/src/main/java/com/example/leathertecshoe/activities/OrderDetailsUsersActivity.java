package com.example.leathertecshoe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.leathertecshoe.R;
import com.example.leathertecshoe.adapters.AdapterCartItem;
import com.example.leathertecshoe.adapters.AdapterOrderUser;
import com.example.leathertecshoe.adapters.AdapterOrderedItem;
import com.example.leathertecshoe.models.ModelCartItem;
import com.example.leathertecshoe.models.ModelOrderedItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailsUsersActivity extends AppCompatActivity {

    private String orderTo, orderId;

    private ImageButton backBtn;
    private TextView orderIdTv, dateTv, orderStatusTv, shopNameTv, totalItemsTv, amountTv,
            addressTv;
    private RecyclerView itemsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderedItem> orderedItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_users);

        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        itemsRv = findViewById(R.id.itemsRv);
        backBtn = findViewById(R.id.backBtn);

        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopInfo();
        loadOrderDetails();
        loadOrderedItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadOrderedItems() {
        orderedItemArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        orderedItemArrayList.clear();
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);
                            orderedItemArrayList.add(modelOrderedItem);
                        }
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailsUsersActivity.this, orderedItemArrayList);
                        itemsRv.setAdapter(adapterOrderedItem);

                        totalItemsTv.setText(""+dataSnapshot.getChildrenCount());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadOrderDetails() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String orderBy = "" + dataSnapshot.child("orderBy").getValue();
                        String orderCost = "" + dataSnapshot.child("orderCost").getValue();
                        String orderId = "" + dataSnapshot.child("orderId").getValue();
                        String orderStatus = "" + dataSnapshot.child("orderStatus").getValue();
                        String orderTime = "" + dataSnapshot.child("orderTime").getValue();
                        String orderTo = "" + dataSnapshot.child("orderTo").getValue();
                        String deliveryFee = "" + dataSnapshot.child("deliveryFee").getValue();
                        String latitude = "" + dataSnapshot.child("latitude").getValue();
                        String longitude = "" + dataSnapshot.child("longitude").getValue();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formateDate = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

                        if (orderStatus.equals("In Progress")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.sky));
                        } else if (orderStatus.equals("Completed")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.green));
                        } else if (orderStatus.equals("Cancelled")) {
                            orderStatusTv.setTextColor(getResources().getColor(R.color.red));
                        }

                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("৳" + orderCost + "[Including delivery fee ৳" + deliveryFee + "]");
                        dateTv.setText(formateDate);

                        findAddress(latitude, longitude);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

    }

    private void findAddress(String latitude, String longitude) {


        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        Context context;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            String address = addresses.get(0).getAddressLine(0);
            addressTv.setText(address);
        } catch (Exception e) {

        }

    }

    private void loadShopInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String name = "" + dataSnapshot.child("shopName").getValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
    }
}
