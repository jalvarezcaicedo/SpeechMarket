package com.personal.jalvarez.speechmarket;


class Product {
    private int type;
    private String productName;
    private long price;
    private long amount;

    public Product() {
    }

    Product(int type, String productName, long price, long amount) {
        this.type = type;
        this.productName = productName;
        this.price = price;
        this.amount = amount;
    }

    int getType() {
        return type;
    }

    String getProductName() {
        return productName;
    }

    long getPrice() {
        return price;
    }

    long getAmount() {
        return amount;
    }

    void setAmount(long amount) {
        this.amount = amount;
    }
}