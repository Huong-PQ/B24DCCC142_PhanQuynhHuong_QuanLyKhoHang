package com.example.th03.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.th03.R;
import com.example.th03.adapter.ProductAdapter;
import com.example.th03.database.DatabaseHelper;
import com.example.th03.model.Product;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvDetailName, tvDetailCategory, tvDetailStatus, tvDetailPrice;
    private TextView tvDetailQuantity, tvDetailLowStock, tvDetailStockValue, tvDetailDescription;
    private TextView btnBackToolbar;
    private Button btnImportOne, btnSellOne, btnBackDetail, btnDeleteProduct;

    private DatabaseHelper dbHelper;
    private int productId;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);

        // Nhận ID sản phẩm truyền qua Intent
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailLowStock = findViewById(R.id.tvDetailLowStock);
        tvDetailStockValue = findViewById(R.id.tvDetailStockValue);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        btnBackToolbar = findViewById(R.id.btnBackToolbar);
        btnImportOne = findViewById(R.id.btnImportOne);
        btnSellOne = findViewById(R.id.btnSellOne);
        btnBackDetail = findViewById(R.id.btnBackDetail);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        loadProductDetails();

        // Nút + Nhập thêm 1
        btnImportOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentProduct != null) {
                    int newQty = currentProduct.getQuantity() + 1;
                    dbHelper.updateQuantity(productId, newQty);
                    loadProductDetails();
                }
            }
        });

        // Nút - Bán 1
        btnSellOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentProduct != null) {
                    if (currentProduct.getQuantity() == 0) {
                        Toast.makeText(ProductDetailActivity.this, "Hết hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        int newQty = currentProduct.getQuantity() - 1;
                        dbHelper.updateQuantity(productId, newQty);
                        loadProductDetails();
                    }
                }
            }
        });

        // Nút Quay lại
        View.OnClickListener closeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
        btnBackDetail.setOnClickListener(closeListener);
        btnBackToolbar.setOnClickListener(closeListener);

        // Nút Xóa sản phẩm
        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteProduct(productId);
                Toast.makeText(ProductDetailActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadProductDetails() {
        // Truy vấn lại CSDL theo id
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            finish();
            return;
        }

        tvDetailName.setText(currentProduct.getName());
        tvDetailCategory.setText(currentProduct.getCategory());
        tvDetailStatus.setText(currentProduct.getActive() == 1 ? "• Đang kinh doanh" : "• Ngừng kinh doanh");
        tvDetailPrice.setText(ProductAdapter.formatCurrency(currentProduct.getPrice()));
        tvDetailQuantity.setText(String.valueOf(currentProduct.getQuantity()));

        long stockValue = (long) currentProduct.getPrice() * currentProduct.getQuantity();
        tvDetailStockValue.setText(ProductAdapter.formatCurrency(stockValue));

        if (currentProduct.getQuantity() < 5) {
            tvDetailLowStock.setVisibility(View.VISIBLE);
        } else {
            tvDetailLowStock.setVisibility(View.GONE);
        }

        String desc = currentProduct.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            tvDetailDescription.setText("(Không có mô tả)");
        } else {
            tvDetailDescription.setText(desc);
        }
    }
}