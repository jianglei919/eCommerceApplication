package com.conestoga.ecommerceapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.HomeActivity;
import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.adapter.ProductListAdapter;
import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.listener.ToolbarTitleListener;
import com.conestoga.ecommerceapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {

    private final static String TAG = "ProductListFragment";

    private RecyclerView recyclerView;
    private ProductListAdapter adapter;

    private List<Product> productList = new ArrayList<>();

    public ProductListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Pass OnProductClickListener from Activity to Adapter
        HomeActivity activity = (HomeActivity) requireActivity();
        adapter = new ProductListAdapter(activity, productList, requireContext());

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarTitleListener) {
            ((ToolbarTitleListener) getActivity()).updateToolbarTitle(getString(R.string.products));
        }
    }

    private void loadProducts() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference(CollectionName.PRODUCTS.getName());
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Product product = data.getValue(Product.class);
                    productList.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load products");
            }
        });
    }
}