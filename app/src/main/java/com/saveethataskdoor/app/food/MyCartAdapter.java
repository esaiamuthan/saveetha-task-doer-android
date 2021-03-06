package com.saveethataskdoor.app.food;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.database.DataBaseHandler;
import com.saveethataskdoor.app.model.Cart;
import com.saveethataskdoor.app.model.Product;

import java.util.ArrayList;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {

    ArrayList<Cart> cartList = new ArrayList<>();
    private DataBaseHandler dataBaseHandler;
    private OnCartListener onCartCountListener;

    public MyCartAdapter(ArrayList<Cart> cartList,
                         DataBaseHandler dataBaseHandler,
                         OnCartListener onCartCountListener
    ) {
        this.cartList = cartList;
        this.dataBaseHandler = dataBaseHandler;
        this.onCartCountListener = onCartCountListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Cart product = cartList.get(position);
        holder.bindView(product);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onCartCountListener.onCartDelete(product, position);
                return false;
            }
        });

        holder.add.setOnClickListener(v -> {
            holder.linearLayoutadd.setVisibility(View.GONE);
            holder.linearlayoutproductadd.setVisibility(View.VISIBLE);
            holder.productcount.setText("1");

            Product product1 = new Product();
            product1.setId(product.getId());
            ;
            product1.setName(product.getName());
            ;
            product1.setAmount(product.getAmount());
            ;
            product1.setStoreId(product.getStoreId());
            ;
            product1.setStorename(product.getStorename());
            ;
            dataBaseHandler.insertCart(
                    product1, 1
            );

            onCartCountListener.onCartCountUpdate();
        });
        holder.plus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.productcount.getText().toString());
            int latestQuantity = quantity + 1;
            holder.productcount.setText(String.valueOf(latestQuantity));
            dataBaseHandler.updateQuantity(String.valueOf(product.getId()),
                    holder.productcount.getText().toString());
            onCartCountListener.onCartCountUpdate();
        });
        holder.minus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.productcount.getText().toString());
            holder.productcount.setText(String.valueOf(quantity - 1));
            if (quantity > 1) {
                dataBaseHandler.updateQuantity(String.valueOf(product.getId()),
                        holder.productcount.getText().toString());
            } else if (quantity == 1) {
                holder.linearlayoutproductadd.setVisibility(View.GONE);
                holder.linearLayoutadd.setVisibility(View.VISIBLE);
                dataBaseHandler.removeProduct(String.valueOf(product.getId()));
            }
            onCartCountListener.onCartCountUpdate();
        });

        int cartCount = dataBaseHandler.productCount(String.valueOf(product.getId()));
        if (cartCount > 0) {
            holder.productcount.setText(String.valueOf(cartCount));

            holder.linearLayoutadd.setVisibility(View.GONE);
            holder.linearlayoutproductadd.setVisibility(View.VISIBLE);
        } else {

            holder.linearLayoutadd.setVisibility(View.VISIBLE);
            holder.linearlayoutproductadd.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void notifyData(int position) {
        cartList.remove(position);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvAmount;

        LinearLayout linearLayoutadd, linearlayoutproductadd;

        TextView add, productcount, plus, minus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Cart product) {
            tvName.setText(product.getName());
            tvAmount.setText("Rs." + product.getAmount());

            add = itemView.findViewById(R.id.tvProductAdd);
            plus = itemView.findViewById(R.id.tvProductPlus);
            minus = itemView.findViewById(R.id.tvProductMinus);
            productcount = itemView.findViewById(R.id.tvProductCount);
            linearLayoutadd = itemView.findViewById(R.id.linearProductAdd);
            linearlayoutproductadd = itemView.findViewById(R.id.linearProductAddContent);
        }
    }
}
