package com.podcastcatalog;

class MyItem {

    private final String name;

    public MyItem(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyItem myItem = (MyItem) o;

        return name != null ? name.equals(myItem.name) : myItem.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MyItem{" +
                "name='" + name + '\'' +
                '}';
    }
}
