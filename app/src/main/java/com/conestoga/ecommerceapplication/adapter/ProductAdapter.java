package com.conestoga.ecommerceapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.ProductDetailActivity;
import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.constant.CommonConstant;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.ImageUtils;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productNameTextView.setText(product.getProductName());
        holder.productPriceTextView.setText(String.format("$%.2f", product.getPrice()));
        holder.productDescriptionTextView.setText(product.getDescription());

        // loading product images
        ImageUtils.loadImageFromStorage(context, holder.productImageImageView, product.getProductImageUrl(),
                product.getProductName(), CommonConstant.IMAGE_THUMBNAIL_TYPE);

        // clicked the cardView goes to the ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", product.getProductId());
            intent.putExtra("productName", product.getProductName());
            intent.putExtra("productImageUrl", product.getProductImageUrl());
            intent.putExtra("productImageDetailUrl", product.getProductImageDetailUrl());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("store", product.getStore());
            intent.putExtra("description", product.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView productDescriptionTextView;
        ImageView productImageImageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            productDescriptionTextView = itemView.findViewById(R.id.productDescription);
            productImageImageView = itemView.findViewById(R.id.productImage);
        }
    }
}
