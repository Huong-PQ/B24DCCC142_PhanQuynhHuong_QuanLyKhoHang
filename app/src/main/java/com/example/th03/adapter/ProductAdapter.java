package com.example.th03.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.th03.R;
import com.example.th03.model.Product;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> productList;

    public ProductAdapter(Context context, ArrayList<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }

        Product product = productList.get(position);

        TextView tvName = convertView.findViewById(R.id.tvItemName);
        TextView tvQuantity = convertView.findViewById(R.id.tvItemQuantity);
        TextView tvPrice = convertView.findViewById(R.id.tvItemPrice);
        TextView tvCategory = convertView.findViewById(R.id.tvItemCategory);
        TextView tvLowStock = convertView.findViewById(R.id.tvItemLowStock);

        tvName.setText(product.getName());
        tvPrice.setText("Giá bán: " + formatCurrency(product.getPrice()));
        tvCategory.setText(product.getCategory());
        tvQuantity.setText("SL: " + product.getQuantity());

        // Cảnh báo sắp hết hàng nếu số lượng < 5
        if (product.getQuantity() == 0) {
            tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.red_danger));
            tvLowStock.setText("Hết hàng");
            tvLowStock.setVisibility(View.VISIBLE);
        } else if (product.getQuantity() < 5) {
            tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.red_danger));
            tvLowStock.setText("Sắp hết hàng");
            tvLowStock.setVisibility(View.VISIBLE);
        } else {
            tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.text_dark));
            tvLowStock.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static String formatCurrency(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(amount) + " đ";
    }
}