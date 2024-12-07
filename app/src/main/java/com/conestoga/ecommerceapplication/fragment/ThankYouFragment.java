package com.conestoga.ecommerceapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.listener.ToolbarTitleListener;

public class ThankYouFragment extends Fragment {

    private Button backToProductsButton;
    private Button viewOrdersButton;

    public ThankYouFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thank_you, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backToProductsButton = view.findViewById(R.id.backToProductsButton);
        viewOrdersButton = view.findViewById(R.id.viewOrdersButton);

        // 返回到产品页面
        backToProductsButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProductListFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // 查看我的订单
        viewOrdersButton.setOnClickListener(v -> {
            Fragment orderListFragment = new OrderListFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, orderListFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarTitleListener) {
            ((ToolbarTitleListener) getActivity()).updateToolbarTitle(getString(R.string.order_success));
        }
    }
}