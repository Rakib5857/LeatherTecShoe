package com.example.leathertecshoe.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.leathertecshoe.FilterProduct;
import com.example.leathertecshoe.FilterProductUser;
import com.example.leathertecshoe.R;
import com.example.leathertecshoe.activities.EditProductActivity;
import com.example.leathertecshoe.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

        private Context context;
        public ArrayList<ModelProduct> productsList, filterList;
        private FilterProduct filter;

        public AdapterProductSeller(Context context,ArrayList<ModelProduct>productsList) {
            this.context = context;
            this.productsList = productsList;
            this.filterList = productsList;
        }
        @NonNull
        @Override
        public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller, parent,false);
            return new HolderProductSeller(view);
        }

        @Override
        public void onBindViewHolder (@NonNull HolderProductSeller holder, int position) {
            final ModelProduct modelProduct = productsList.get(position);
            String id = modelProduct.getProductId();
            String uid = modelProduct.getUid();
            String discountAvailable = modelProduct.getDiscountAvailable();
            String discountNote = modelProduct.getDiscountNote();
            String discountPrice = modelProduct.getDiscountPrice();
            String productCategory = modelProduct.getProductCategory();
            String productDescription = modelProduct.getProductDescription();
            String icon = modelProduct.getProductIcon();
            String size = modelProduct.getProductQuantity();
            String title = modelProduct.getProductTitle();
            String timestamp = modelProduct.getTimestamp();
            String originalPrice = modelProduct.getOriginalPrice();

            holder.titleTv.setText(title);
            holder.sizeTv.setText(size);
            holder.discountedNoteTv.setText(discountNote);
            holder.discountedPriceTv.setText("৳"+discountPrice);
            holder.originalPriceTv.setText("৳"+ originalPrice);
            if (discountAvailable.equals("true")){
                holder.discountedPriceTv.setVisibility(View.VISIBLE);
                holder.discountedNoteTv.setVisibility(View.VISIBLE);
                holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else {
                holder.discountedPriceTv.setVisibility(View.GONE);
                holder.discountedNoteTv.setVisibility(View.GONE);
                holder.originalPriceTv.setPaintFlags(0);

            }
            try{
                Picasso.get().load(icon).placeholder(R.drawable.shoe).into(holder.productIconIv);
            }
            catch (Exception e){
                holder.productIconIv.setImageResource(R.drawable.shoe);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //handle item clicks, show item details (in bottom sheet)
                    detailsBottomSheet(modelProduct);//here modelProduct contains details of clicked product


                }
            });

        }

    private void detailsBottomSheet(ModelProduct modelProduct) {
            //bottom sheet
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_product_detalis_seller, null);
        //set view to bottomSheet
        bottomSheetDialog.setContentView(view);

            //int views of bottomSheet
        ImageButton backBtn = view.findViewById(R.id.backBtn);
        ImageButton deleteBtn = view.findViewById(R.id.deleteBtn);
        ImageButton editBtn = view.findViewById(R.id.editBtn);
        ImageView productIconIv = view.findViewById(R.id.productIconIv);
        TextView discountNoteTv = view.findViewById(R.id.discountNoteTv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView categoryTv = view.findViewById(R.id.categoryTv);
        TextView sizeTv = view.findViewById(R.id.sizeTv);
        TextView discountedPriceTv = view.findViewById(R.id.discountedPriceTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);

        //get data
        final String id = modelProduct.getProductId();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote = modelProduct.getDiscountNote();
        String discountPrice = modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String productDescription = modelProduct.getProductDescription();
        String icon = modelProduct.getProductIcon();
        String size = modelProduct.getProductQuantity();
        final String title = modelProduct.getProductTitle();
        String timestamp = modelProduct.getTimestamp();
        String originalPrice = modelProduct.getOriginalPrice();

        //set data
        titleTv.setText(title);
        descriptionTv.setText(productDescription);
        categoryTv.setText(productCategory);
        sizeTv.setText(size);
        discountNoteTv.setText(discountNote);
        discountedPriceTv.setText("৳"+discountPrice);
        originalPriceTv.setText("৳"+originalPrice);
        if (discountAvailable.equals("true")){
           discountedPriceTv.setVisibility(View.VISIBLE);
           discountNoteTv.setVisibility(View.VISIBLE);
           originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {
            discountedPriceTv.setVisibility(View.GONE);
            discountNoteTv.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.shoe).into(productIconIv);
        }
        catch (Exception e){
            productIconIv.setImageResource(R.drawable.shoe);
        }
        bottomSheetDialog.show();

        //edit click
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditProductActivity.class);
                intent.putExtra("productId",id);
                context.startActivity(intent);

            }
        });
        //delete click
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete product"+title+"?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete
                        deleteProduct(id);
                        bottomSheetDialog.dismiss();


                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //cancel, dismiss dialog
                        dialog.dismiss();
                    }
                })
                .show();


            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

            }
        });




    }

    private void deleteProduct(String id) {
            //delete product using its id
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Products").child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //product delete
                        Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed deleting product
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

    }

    @Override
        public int getItemCount() {
            return productsList.size();
        }

    @Override
    public Filter getFilter() {
            if (filter==null){
                filter = new FilterProduct(this, filterList);
            }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{

            private ImageView productIconIv;
            private TextView discountedNoteTv,titleTv,sizeTv,discountedPriceTv,originalPriceTv;

            public HolderProductSeller(@NonNull View itemView) {
                super(itemView);
                productIconIv = itemView.findViewById(R.id.productIconIv);
                titleTv = itemView.findViewById(R.id.titleTv);
                discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);
                sizeTv = itemView.findViewById(R.id.sizeTv);
                discountedPriceTv = itemView.findViewById(R.id.discountedPriceTv);
                originalPriceTv = itemView.findViewById(R.id.originalPriceTv);
            }
    }
}
