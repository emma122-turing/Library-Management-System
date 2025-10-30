package com.StudentLibrary.Studentlibrary.DTO;

public class BookAnalyticsDTO {

    private String name;
    private long count;

    public BookAnalyticsDTO(String name, long count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "BookAnalyticsDTO{" +
                "name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
