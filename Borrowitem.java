package EducationBorrowingSystem;

public class Borrowitem {
    private String itemId;
    private String itemName;
    private String category;
    private int quantity;

    public Borrowitem(String itemId, String itemName, String category, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "[" + itemId + "]  " + itemName + "  \u00B7  " + category + "  (Stock: " + quantity + ")";
    }
}