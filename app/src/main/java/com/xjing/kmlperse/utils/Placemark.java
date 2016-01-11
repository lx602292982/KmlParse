package com.xjing.kmlperse.utils;

import java.util.List;

/**
 * Created by vashisthg on 23/03/14.
 */
public class Placemark {

    private String name;
    private String description;
    private List<Point> point;

    public Placemark() {

    }

    public Placemark(String name, List<Point> point, String description) {
        this.name = name;
        this.point = point;
        this.description = description;
    }


    public static class PlacemarkBuilder {
        private String name;
        private List<Point> point;
        private String description;

        public PlacemarkBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public PlacemarkBuilder setPoint(List<Point> point) {
            this.point = point;
            return this;
        }

        public PlacemarkBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Placemark createPlacemark() {
            return new Placemark(name, point, description);
        }

        @Override
        public String toString() {
            return "PlacemarkBuilder{" +
                    "name='" + name + '\'' +
                    ", point=" + point +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setPoint(List<Point> point) {
        this.point = point;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", point=" + point +
                '}';
    }
}
