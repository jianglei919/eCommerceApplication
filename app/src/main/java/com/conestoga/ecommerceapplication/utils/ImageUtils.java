package com.conestoga.ecommerceapplication.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.constant.CommonConstant;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageUtils {

    private final static String TAG = "ImageUtils";

    private static final FirebaseStorage STORAGE = FirebaseStorage.getInstance();

    public static Integer getThumbnailImageResourceByName(String name) {
        int resourceId = R.drawable.placeholder_image;
        switch (name) {
            case "Apple":
                resourceId = R.drawable.product_apple_thumbnail;
                break;
            case "Banana":
                resourceId = R.drawable.product_banana_thumbnail;
                break;
            case "Grape":
                resourceId = R.drawable.product_grape_thumbnail;
                break;
            case "Orange":
                resourceId = R.drawable.product_orange_thumbnail;
                break;
            case "Peach":
                resourceId = R.drawable.product_peach_thumbnail;
                break;
            case "Pear":
                resourceId = R.drawable.product_pear_thumbnail;
                break;
            case "Pineapple":
                resourceId = R.drawable.product_pineapple_thumbnail;
                break;
            case "Watermelon":
                resourceId = R.drawable.product_watermelon_thumbnail;
                break;
            default:

        }
        return resourceId;
    }

    public static Integer getDetailImageResourceByName(String name) {
        int resourceId = R.drawable.placeholder_image;
        switch (name) {
            case "Apple":
                resourceId = R.drawable.product_apple_detail;
                break;
            case "Banana":
                resourceId = R.drawable.product_banana_detail;
                break;
            case "Grape":
                resourceId = R.drawable.product_grape_detail;
                break;
            case "Orange":
                resourceId = R.drawable.product_orange_detail;
                break;
            case "Peach":
                resourceId = R.drawable.product_peach_detail;
                break;
            case "Pear":
                resourceId = R.drawable.product_pear_detail;
                break;
            case "Pineapple":
                resourceId = R.drawable.product_pineapple_detail;
                break;
            case "Watermelon":
                resourceId = R.drawable.product_watermelon_detail;
                break;
            default:

        }
        return resourceId;
    }

    public static Integer getImageResource(String productName, String type) {
        switch (type) {
            case CommonConstant.IMAGE_DETAIL_TYPE:
                return getDetailImageResourceByName(productName);
            case CommonConstant.IMAGE_THUMBNAIL_TYPE:
                return getThumbnailImageResourceByName(productName);
        }
        return R.drawable.placeholder_image;
    }

    /**
     * load image from firebase storage
     *
     * @param context
     * @param imageView
     * @param imageUri  - Storage uri: gs://ecommerceapplication-ba926.firebasestorage.app/product_pear_thumbnail.jpg
     * @param name
     * @param type
     */
    public static void loadImageFromStorage(Context context, ImageView imageView, String imageUri, String name, String type) {
        if (TextUtils.isEmpty(imageUri)) {
            // 如果 URI 为空，则加载占位符或默认图片
            Glide.with(context)
                    .load(getImageResource(name, type))
                    .error(getImageResource(name, type))
                    .into(imageView);
            Log.e(TAG, "Invalid imageUri: " + imageUri);
            return;
        }
        StorageReference storageReference = STORAGE.getReferenceFromUrl(imageUri);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Use Glide loading image
            Glide.with(context)
                    .load(uri)
//                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .placeholder(R.drawable.placeholder_image) // loading placeholder image
                    .error(getImageResource(name, type))
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // process error
            Toast.makeText(context, "Failed to load image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * load image by Glide
     *
     * @param context
     * @param imageView
     * @param imageUrl  - http link
     */
    public static void loadImageByGlide(Context context, ImageView imageView, String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(imageView);
    }
}
