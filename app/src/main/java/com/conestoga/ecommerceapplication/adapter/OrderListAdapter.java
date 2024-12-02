package com.conestoga.ecommerceapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.OrderDetailActivity;
import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.model.Order;
import com.conestoga.ecommerceapplication.utils.DateTimeUtils;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrderListAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdTextView.append(order.getOrderId());
        holder.orderStatusTextView.append(order.getStatus());
        holder.totalPriceTextView.append(String.format("$%.2f", order.getTotalPrice()));
        holder.orderTimeTextView.append(DateTimeUtils.formatTimestamp(order.getOrderTime()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderData", order);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView orderIdTextView;
        private TextView orderStatusTextView;
        private TextView totalPriceTextView;
        private TextView orderTimeTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderId);
            orderStatusTextView = itemView.findViewById(R.id.orderStatus);
            totalPriceTextView = itemView.findViewById(R.id.totalPrice);
            orderTimeTextView = itemView.findViewById(R.id.orderTime);
        }
    }
}
