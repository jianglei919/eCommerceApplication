package com.conestoga.ecommerceapplication.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.constant.CommonConstant;
import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.manager.CartManager;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.conestoga.ecommerceapplication.utils.ImageUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private final static String TAG = "CartItemAdapter";

    private List<CartItem> cartItemList;
    private Context context;
    private OnCartChangedListener onCartChangedListener;

    public CartItemAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Log.d(TAG, "Binding view holder for position: " + position);

        CartItem cartItem = cartItemList.get(position);
        Product productItem = cartItem.getProductItem();

        holder.productNameTextView.setText(productItem.getProductName());
        holder.productPriceTextView.setText(String.format("$%.2f", cartItem.getTotalPrice()));
        holder.quantityTextView.setText(String.valueOf(cartItem.getTotalQuantity()));
        ImageUtils.loadImageFromStorage(context, holder.productImageView, productItem.getProductImageUrl(),
                productItem.getProductName(), CommonConstant.IMAGE_THUMBNAIL_TYPE);

        // 减少
        holder.decreaseQuantityButton.setOnClickListener(view -> {
            if (cartItem.getTotalQuantity() > 1) {
                cartItem.setTotalQuantity(cartItem.getTotalQuantity() - 1);
                cartItem.setTotalPrice(cartItem.getProductItem().getPrice() * cartItem.getTotalQuantity());
                updateOrDeleteCartItemFromDatabase(cartItem);
                notifyItemChanged(position); // 刷新当前项
            } else {
                removeCartItemFromDatabase(cartItem);
                cartItemList.remove(position); // 从列表中移除项
                CartManager.getInstance().removeFromCart(productItem.getProductId());
                notifyItemRemoved(position);  // 通知 RecyclerView 项已移除
            }

            // 通知购物车状态发生变化
            if (onCartChangedListener != null) {
                onCartChangedListener.onCartChanged();
            }

            Log.d(TAG, "Cart items: " + cartItemList.toString());
        });

        // 增加
        holder.increaseQuantityButton.setOnClickListener(view -> {
            if (cartItem.getTotalQuantity() >= 9) {
                Toast.makeText(context, "The quantity has exceeded the maximum limit.", Toast.LENGTH_SHORT).show();
            } else {
                cartItem.setTotalQuantity(cartItem.getTotalQuantity() + 1);
                cartItem.setTotalPrice(cartItem.getProductItem().getPrice() * cartItem.getTotalQuantity());
                updateOrDeleteCartItemFromDatabase(cartItem);
                notifyItemChanged(position); // 刷新当前项
            }

            // 通知购物车状态发生变化
            if (onCartChangedListener != null) {
                onCartChangedListener.onCartChanged();
            }

            Log.d(TAG, "Cart items: " + cartItemList.toString());
        });

        holder.removeButton.setOnClickListener(v -> {
            removeCartItemFromDatabase(cartItem);

            cartItemList.remove(position); // 从列表中移除项
            CartManager.getInstance().removeFromCart(productItem.getProductId());
            notifyItemRemoved(position);  // 通知 RecyclerView 项已移除

            // 通知购物车状态发生变化
            if (onCartChangedListener != null) {
                onCartChangedListener.onCartChanged();
            }
        });
    }

    public void removeCartItemFromDatabase(CartItem removedCartItem) {
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference(CollectionName.CART_ITEMS.getName());
        String currentUserId = FirebaseAuthUtils.getCurrentUserId();
        if (!TextUtils.isEmpty(currentUserId)) {
            DatabaseReference productItemRef = cartItemRef.child(currentUserId).child(removedCartItem.getProductItem().getProductId());
            // remove
            productItemRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.i(TAG, "Successful to remove cart item. currentUserId=" + currentUserId + ", productId=" + removedCartItem.getProductItem().getProductId());
                } else {
                    Log.i(TAG, "Failed to remove cart item. currentUserId=" + currentUserId + ", productId=" + removedCartItem.getProductItem().getProductId());
                }
            });
        } else {
            Log.e(TAG, "Failed to get current user id.");
        }
    }

    public void updateOrDeleteCartItemFromDatabase(CartItem newCartItem) {
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference(CollectionName.CART_ITEMS.getName());
        String currentUserId = FirebaseAuthUtils.getCurrentUserId();
        if (!TextUtils.isEmpty(currentUserId)) {
            DatabaseReference productItemRef = cartItemRef.child(currentUserId).child(newCartItem.getProductItem().getProductId());

            // delete
            if (newCartItem.getTotalQuantity() == 0) {
                removeCartItemFromDatabase(newCartItem);
                return;
            }

            // update
            productItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    productItemRef.setValue(newCartItem).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Cart item updated successfully. productId= " + newCartItem.getProductItem().getProductId() + ", currentUserId= " + currentUserId);
                        } else {
                            Log.i(TAG, "Failed to update cart item. productId= " + newCartItem.getProductItem().getProductId() + ", currentUserId= " + currentUserId);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Database error. " + error.getMessage() + ", currentUserId=" + currentUserId + ", productId=" + newCartItem.getProductItem().getProductId());
                }
            });
        } else {
            Log.e(TAG, "Failed to get current user id.");
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView productPriceTextView;
        TextView quantityTextView;
        ImageView productImageView;
        Button decreaseQuantityButton;
        Button increaseQuantityButton;
        Button removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            quantityTextView = itemView.findViewById(R.id.quantity);
            productImageView = itemView.findViewById(R.id.productImage);
            removeButton = itemView.findViewById(R.id.removeButton);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
        }
    }

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public void setOnCartChangedListener(OnCartChangedListener listener) {
        this.onCartChangedListener = listener;
    }
}
