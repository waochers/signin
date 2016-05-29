package project.stutisrivastava.waochers.model;

/**
 * Created by vardan on 3/31/16.
 */
public class Shops {
    private String shopImage;
    private String shopAddress;
    private String shopName;
    private String minDiscount;

    public Shops(String shopImage, String shopAddress, String shopName, String minDiscount) {
        this.shopImage = shopImage;
        this.shopAddress = shopAddress;
        this.shopName = shopName;
        this.minDiscount = minDiscount;
    }

    public Shops() {

    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMinDiscount() {
        return minDiscount;
    }

    public void setMinDiscount(String minDiscount) {
        this.minDiscount = minDiscount;
    }

}
