package data;

@lombok.Data
public class GameData {
    public String title;
    public String age;
    public String price;

    public GameData(String title, String age, String price) {
        this.title = title;
        this.age = age;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Age: " + age + ", Price: " + price;
    }
}
