package com.example.caramel.persist;

public enum Category {

    EMPTY(0, "Без категории"),
    KOREA(1, "Корея"),
    ALL(2, "Все");

    private int id;
    private final String name;

    Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static Category getCategoryById(int id) {
        for (Category category : Category.values()) {
            if (category.id == id) {
                return category;
            }
        }
        return null;
    }
}
