package com.example.th03.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.th03.model.Product;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Bảng products
    private static final String TABLE_PRODUCTS = "products";
    private static final String COL_PROD_ID = "id";
    private static final String COL_PROD_NAME = "name";
    private static final String COL_PROD_PRICE = "price";
    private static final String COL_PROD_QUANTITY = "quantity";
    private static final String COL_PROD_CATEGORY = "category";
    private static final String COL_PROD_ACTIVE = "active";
    private static final String COL_PROD_DESC = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng users
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        // 2. Tạo bảng products
        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PROD_NAME + " TEXT NOT NULL, " +
                COL_PROD_PRICE + " INTEGER NOT NULL, " +
                COL_PROD_QUANTITY + " INTEGER NOT NULL, " +
                COL_PROD_CATEGORY + " TEXT NOT NULL, " +
                COL_PROD_ACTIVE + " INTEGER NOT NULL, " +
                COL_PROD_DESC + " TEXT)";
        db.execSQL(createProductsTable);

        // 3. Chèn tài khoản mẫu: admin / 123456
        insertSampleUser(db, "admin", "123456");

        // 4. Chèn 3 sản phẩm mẫu
        insertSampleProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", 1, "Chuột không dây kết nối Bluetooth/USB ổn định"));
        insertSampleProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", 1, "Bàn phím cơ switch xanh, có đèn nền"));
        insertSampleProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", 1, "Sổ tay bìa cứng 200 trang giấy dày"));
    }

    private void insertSampleUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        db.insert(TABLE_USERS, null, values);
    }

    private void insertSampleProduct(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COL_PROD_NAME, p.getName());
        values.put(COL_PROD_PRICE, p.getPrice());
        values.put(COL_PROD_QUANTITY, p.getQuantity());
        values.put(COL_PROD_CATEGORY, p.getCategory());
        values.put(COL_PROD_ACTIVE, p.getActive());
        values.put(COL_PROD_DESC, p.getDescription());
        db.insert(TABLE_PRODUCTS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // 1. Kiểm tra đăng nhập
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // 2. Thêm tài khoản mới
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    // 3. Thêm sản phẩm, trả về id vừa thêm
    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROD_NAME, p.getName());
        values.put(COL_PROD_PRICE, p.getPrice());
        values.put(COL_PROD_QUANTITY, p.getQuantity());
        values.put(COL_PROD_CATEGORY, p.getCategory());
        values.put(COL_PROD_ACTIVE, p.getActive());
        values.put(COL_PROD_DESC, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    // 4. Lấy toàn bộ sản phẩm
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COL_PROD_ID + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NAME));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_QUANTITY));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_CATEGORY));
                int active = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ACTIVE));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_DESC));

                list.add(new Product(id, name, price, quantity, category, active, desc));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 5. Lấy 1 sản phẩm theo id
    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COL_PROD_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Product product = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NAME));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_QUANTITY));
            String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_CATEGORY));
            int active = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ACTIVE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_DESC));

            product = new Product(id, name, price, quantity, category, active, desc);
        }
        cursor.close();
        return product;
    }

    // 6. Cập nhật số lượng
    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROD_QUANTITY, quantity);
        return db.update(TABLE_PRODUCTS, values, COL_PROD_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 7. Xóa sản phẩm theo id
    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COL_PROD_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // 8. Tổng giá trị kho: SUM(price * quantity)
    public long getTotalValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COL_PROD_PRICE + " * " + COL_PROD_QUANTITY + ") FROM " + TABLE_PRODUCTS, null);
        long total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getLong(0);
        }
        cursor.close();
        return total;
    }

    // 9. Đếm số sản phẩm sắp hết hàng (quantity < 5)
    public int countLowStock() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_PROD_QUANTITY + " < ?", new String[]{"5"});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}