package data;

public class GameData {
    public String title;
    public String age;
    public String price;
    public int rowIndex; // Excel row index for reporting

    public GameData(String title, String age, String price, int rowIndex) {
        this.title = title;
        this.age = age;
        this.price = price;
        this.rowIndex = rowIndex;
    }

    public GameData(String title, String age, String price) {
        this(title, age, price, -1); // fallback if rowIndex not set
    }

    @Override
    public String toString() {
        return "Row: " + rowIndex + ", Title: " + title + ", Age: " + age + ", Price: " + price;
    }
}
