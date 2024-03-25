package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**Перебор всех функций F(x_1, x_2, x_3) = (f_1, f_2, f_3), f_i(x_1, x_2, x_3)**/
public class Main {

    private static final int k = 3;

    private static final int n = 2 << (k - 1); //aka 8

    /**Возвращает следующий булев вектор, в коде Грея**/
    private static int getNextVector(int i) {
        return (i ^ (i >> 1)) % n;
    }

    /**Скалярное произведение x и y**/
    private static int scalarMult(int x, int y) {
        int res = 0;
        for (int i = 0b1; i < n; i <<= 1) {
            res ^= ((i & x) / i)& ((i & y) / i);
        }
        return res;
    }

    /**Возвращает преобразование Уолша-Адамара функции f, заданной в виде вектора**/
    private static int walshHadamardTransform(int a, int f) {
        int sum = 0, mask = 0b1;
        for (int x = 0; x < n; x++) {
            if(scalarMult(x, a) == (f & mask) / (mask)) {
                sum += 1;
            } else {
                sum -= 1;
            }
            mask <<= 1;
        }
        return sum;
    }

    /**Возвращает нелинейность функции f, заданной в виде вектора**/
    private static int getNonlinearity(int f) {
        int c = 0, a;
        for (int i = 0; i < n; i++) {
            a = Math.abs(walshHadamardTransform(i, f));
            if(a > c) {
                c = a;
            }
        }
        return (1 << (k - 1)) - c / 2;
    }

    /**Добавляет 0 вначале для удобства**/
    private static String checkFunc(String f) {
        StringBuilder fBuilder = new StringBuilder();
        for (int i = f.length(); i < n; i++) {
            fBuilder.append(0);
        }
        fBuilder.append(f);
        return fBuilder.toString();
    }

    /**Написать перестановку в читаемом виде**/
    private static int[] getPermutation(int x, int y, int z) {
        int[] res = new int[n];
        int mask = 0b1;
        for (int i = 0; i < n; i++) {
            res[n - i - 1] = (mask & x) / mask * 4 + (mask & y) / mask * 2 +  (mask & z) / mask;
            mask <<= 1;
        }
        return res;
    }

    /**Проверить перестановку на корректность и вывести в случае корректности**/
    private static boolean checkPermutation(int[] perm) {
        int[] arr = new int[n];         //Проверка сортировкой подсчетом
        for (int i = 0; i < n; i++) {
            arr[perm[i]]++;
        }
        for (int i = 0; i < n; i++) {
            if(arr[i] != 1) {
                return false;
            }
        }
        for (int term : perm) {
            System.out.print(term + " ");
        }
        System.out.println();
        return true;
    }

    public static void main(String[] args) {
        int nonLinear;
        ArrayList<Integer> functions = new ArrayList<>(57);
        //Перебираем вектора функций
        for (int f = 0; f < (1 << n); f++) {
            nonLinear = getNonlinearity(f);
            if(Integer.bitCount(f) == n / 2 && nonLinear > 0) {
                System.out.print(checkFunc(Integer.toBinaryString(f)));
                System.out.print(" Нелинейность " + nonLinear);
                System.out.println();
                functions.add(f);
            }
        }

        class IntegerPair {
            private int val1;
            private int val2;

            public IntegerPair(int val1, int val2) {
                this.val1 = val1;
                this.val2 = val2;
            }
            public int getVal1() {
                return val1;
            }
            public void setVal1(int val1) {
                this.val1 = val1;
            }
            public int getVal2() {
                return val2;
            }
            public void setVal2(int val2) {
                this.val2 = val2;
            }

            @Override
            public int hashCode() {
                return Objects.hash(getVal1(), getVal2());
            }
        }
        HashMap<Integer, IntegerPair> map = new HashMap<>(56 * 56 + 1);
        IntegerPair pair;
        int val1, val2;
        int[] permutation;
        HashSet<int[]> set = new HashSet<>(5000);
        for(int i = functions.get(0); i < functions.size(); i++) {
            for (int j = 0; j < i; j++) {
                val1 = functions.get(i);
                val2 = functions.get(j);
                pair = new IntegerPair(val1, val2);
                if(map.containsKey(val1 ^ val2)) {
                    pair = map.get(val1 ^ val2);
//                    System.out.printf("Решение x = %s, y = %s, z = %s, t = %s\n", checkFunc(Integer.toBinaryString(val1)),
//                            checkFunc(Integer.toBinaryString(val2)), checkFunc(Integer.toBinaryString(pair.getVal1())),
//                            checkFunc(Integer.toBinaryString(pair.getVal2())));
//                    System.out.println("Перестановка");
                    permutation = getPermutation(val1, val2, pair.getVal1());
                    if(checkPermutation(permutation)) {
                        set.add(permutation);
                    }
                } else {
                    map.put(val1 ^ val2, pair);
                }
            }
        }
        for (int[] array: set) {
            for (int el: array) {
                System.out.print(el + " ");
            }
            System.out.println();
        }
        System.out.println("Total " + set.size());
    }
}