package app.paseico.data.utils;

public class Tuple<T, K> {
    private T firstElement;
    private K secondElement;

    public Tuple() {
    }

    public Tuple(T firstElement, K secondElement) {
        this.firstElement = firstElement;
        this.secondElement = secondElement;
    }

    public T getFirstElement() {
        return firstElement;
    }

    public void setFirstElement(T firstElement) {
        this.firstElement = firstElement;
    }

    public K getSecondElement() {
        return secondElement;
    }

    public void setSecondElement(K secondElement) {
        this.secondElement = secondElement;
    }
}
