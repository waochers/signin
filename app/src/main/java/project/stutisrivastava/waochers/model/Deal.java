package project.stutisrivastava.waochers.model;

/**
 * Created by vardan on 6/6/16.
 */
public class Deal {
    private String price_percentage;
    private String deal_id;
    private String discount_percentage;
    private String item_name;
    private String is_active;
    private String category_id;
    private String valid_for_days;
    private String category_name;

    public Deal(String price_percentage, String deal_id, String discount_percentage, String item_name, String is_active, String category_id, String valid_for_days, String category_name) {
        this.price_percentage = price_percentage;
        this.deal_id = deal_id;
        this.discount_percentage = discount_percentage;
        this.item_name = item_name;
        this.is_active = is_active;
        this.category_id = category_id;
        this.valid_for_days = valid_for_days;
        this.category_name = category_name;
    }

    public Deal() {
    }

    public String getPrice_percentage() {
        return price_percentage;
    }

    public void setPrice_percentage(String price_percentage) {
        this.price_percentage = price_percentage;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getDiscount_percentage() {
        return discount_percentage;
    }

    public void setDiscount_percentage(String discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getValid_for_days() {
        return valid_for_days;
    }

    public void setValid_for_days(String valid_for_days) {
        this.valid_for_days = valid_for_days;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
