package com.delixi.price;

/**
 * 描述：电器配件
 * Created by zhaohl on 2016-2-23.
 */
public class ElectricParts {
    private int id;
    private String material_num;
    private String description;
    private String stock_properties;
    private float tax_price;
    private float pcs;
    private float primary_price;
    private float adjust_price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaterial_num() {
        return material_num;
    }

    public void setMaterial_num(String material_num) {
        this.material_num = material_num;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStock_properties() {
        return stock_properties;
    }

    public void setStock_properties(String stock_properties) {
        this.stock_properties = stock_properties;
    }

    public float getTax_price() {
        return tax_price;
    }

    public void setTax_price(float tax_price) {
        this.tax_price = tax_price;
    }

    public float getPcs() {
        return pcs;
    }

    public void setPcs(float pcs) {
        this.pcs = pcs;
    }

    public float getPrimary_price() {
        return primary_price;
    }

    public void setPrimary_price(float primary_price) {
        this.primary_price = primary_price;
    }

    public float getAdjust_price() {
        return adjust_price;
    }

    public void setAdjust_price(float adjust_price) {
        this.adjust_price = adjust_price;
    }
}
