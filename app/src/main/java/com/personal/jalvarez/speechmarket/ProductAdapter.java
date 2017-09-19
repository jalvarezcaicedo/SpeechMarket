package com.personal.jalvarez.speechmarket;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder> {
    private List<Product> productData;
    private Context context;

    ProductAdapter(List<Product> productData, Context context) {
        this.productData = productData;
        this.context = context;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        final int positionHolder = position;
        holder.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productData.get(positionHolder).setAmount(productData.get(positionHolder).getAmount() + 1);
                notifyDataSetChanged();
            }
        });
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productData.get(positionHolder).getAmount() == 1) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle(context.getString(R.string.title_remove))
                            .setMessage(context.getString(R.string.text_remove))
                            .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    productData.remove(positionHolder);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else {
                    productData.get(positionHolder).setAmount(productData.get(positionHolder).getAmount() - 1);
                    notifyDataSetChanged();
                }
            }
        });
        holder.imgType.setImageResource(productData.get(position).getType());
        holder.prod.setText(productData.get(position).getProductName());
        holder.amount.setText(String.format("x %s", String.valueOf(productData.get(position).getAmount())));
        holder.price.setText(String.format("$ %s", String.valueOf(productData.get(position).getPrice() * productData.get(position).getAmount())));
    }

    public void addAll(ArrayList<Product> data) {
        productData.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        productData.clear();
        notifyDataSetChanged();
    }

    void updateList() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    static class ProductHolder extends RecyclerView.ViewHolder {
        ImageView imgType;
        ImageView imgPlus;
        ImageView imgMinus;
        TextView prod;
        TextView price;
        TextView amount;

        ProductHolder(View v) {
            super(v);
            imgType = v.findViewById(R.id.img_type);
            imgPlus = v.findViewById(R.id.more_prod);
            imgMinus = v.findViewById(R.id.minus_prod);
            prod = v.findViewById(R.id.prod_name);
            price = v.findViewById(R.id.prod_price);
            amount = v.findViewById(R.id.prod_amount);
        }
    }
}
