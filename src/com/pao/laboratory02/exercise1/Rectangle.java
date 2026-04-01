package com.pao.laboratory02.exercise1;

/**
 * TODO: Implementează Rectangle extends Shape.
 * - Atribute: private double width, height
 * - Constructor: super("Rectangle"), this.width, this.height
 * - area() = width * height
 * - perimeter() = 2 * (width + height)
 */
public class Rectangle extends Shape {

    private double width;
    private double height;

    public Rectangle(double width, double height) {
        super("Rectangle");
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        return 2 * (width + height);
    }
}