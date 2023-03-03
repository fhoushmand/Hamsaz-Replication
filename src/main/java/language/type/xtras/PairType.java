package main.java.language.type.xtras;


import main.java.language.type.Type;

public class PairType {// implements Type {
   Type left;
   Type right;

   public PairType(Type left, Type right) {
      this.left = left;
      this.right = right;
   }


   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PairType pairType = (PairType) o;

      if (!left.equals(pairType.left)) return false;
      return right.equals(pairType.right);
   }

   @Override
   public int hashCode() {
      int result = left.hashCode();
      result = 31 * result + right.hashCode();
      return result;
   }
}
