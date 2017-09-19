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

    public void setType(int type) {
        this.type = type;
    }

    String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    long getAmount() {
        return amount;
    }

    void setAmount(long amount) {
        this.amount = amount;
    }
}