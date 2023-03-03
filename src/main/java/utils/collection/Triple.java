package utils.collection;

public class Triple<T1, T2, T3> {
    public T1 element1;
    public T2 element2;
    public T3 element3;

    public Triple(T1 element1, T2 element2, T3 element3) {
        this.element1 = element1;
        this.element2 = element2;
        this.element3 = element3;
    }

    public T1 _1() {
        return element1;
    }
    public T2 _2() {
        return element2;
    }
    public T3 _3() {
        return element3;
    }

    public T1 getElement1() {
        return element1;
    }

    public void setElement1(T1 element1) {
        this.element1 = element1;
    }

    public T2 getElement2() {
        return element2;
    }

    public void setElement2(T2 element2) {
        this.element2 = element2;
    }

    public T3 getElement3() {
        return element3;
    }

    public void setElement3(T3 element3) {
        this.element3 = element3;
    }

    public T1 ge1() {
        return element1;
    }

    public void set1(T1 element1) {
        this.element1 = element1;
    }

    public T2 get2() {
        return element2;
    }

    public void set2(T2 element2) {
        this.element2 = element2;
    }

    public T3 get3() {
        return element3;
    }

    public void set3(T3 element3) {
        this.element3 = element3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;

        Triple triple = (Triple) o;

        if (element1 != null ? !element1.equals(triple.element1) : triple.element1 != null) return false;
        if (element2 != null ? !element2.equals(triple.element2) : triple.element2 != null) return false;
        if (element3 != null ? !element3.equals(triple.element3) : triple.element3 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = element1 != null ? element1.hashCode() : 0;
        result = 31 * result + (element2 != null ? element2.hashCode() : 0);
        result = 31 * result + (element3 != null ? element3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "<" +
               element1 +
               ", " +
               element2 +
               ", " +
               element3 +
               ">";
    }
}
