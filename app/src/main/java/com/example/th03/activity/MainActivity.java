package com.example.th03.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.th03.R;
import com.example.th03.adapter.ProductAdapter;
import com.example.th03.database.DatabaseHelper;
import com.example.th03.model.Product;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts, tvTotalValue, tvLowStockCount, tvEmptyList;
    private ListView lvProducts;
    private Button btnAddProduct;

    private DatabaseHelper dbHelper;
    private ArrayList<Product> productList;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        lvProducts = findViewById(R.id.lvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        lvProducts.setAdapter(adapter);

        // Chuyển sang màn hình thêm sản phẩm
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        // Nhấn vào 1 dòng sản phẩm -> truyền id sang ProductDetailActivity
        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product selected = productList.get(position);
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("PRODUCT_ID", selected.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        // 1. Lấy lại toàn bộ danh sách sản phẩm từ SQLite
        productList.clear();
        productList.addAll(dbHelper.getAllProducts());
        adapter.notifyDataSetChanged();

        // Xử lý trường hợp danh sách rỗng
        if (productList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            lvProducts.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            lvProducts.setVisibility(View.VISIBLE);
        }

        // 2. Cập nhật khối thống kê
        int totalCount = productList.size();
        long totalValue = dbHelper.getTotalValue();
        int lowStockCount = dbHelper.countLowStock();

        tvTotalProducts.setText(String.valueOf(totalCount));
        tvTotalValue.setText(ProductAdapter.formatCurrency(totalValue));
        tvLowStockCount.setText(String.valueOf(lowStockCount));
    }
}