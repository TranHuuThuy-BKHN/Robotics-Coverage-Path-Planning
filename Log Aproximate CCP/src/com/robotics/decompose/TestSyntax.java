package com.robotics.decompose;

import java.util.ArrayList;

public class TestSyntax {
    public static void main(String[] args) {
        ArrayList<Integer> a = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            if (i%4 == 0){
                a.add(-1);
            }
            else a.add(i);
        }
        for (int i = 0; i < a.size() ; i++) {
            System.out.print(a.get(i) + " ");
        }
        System.out.println();
        ArrayList<ArrayList<Integer>> array = new ArrayList<>();
        ArrayList<Integer> t = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            System.out.println(t);
            if(i == a.size() - 1 && t.size() != 0) array.add(t);
            if(a.get(i) == -1){
                System.out.println("Tao t moi");
                array.add(t);
                t = new ArrayList<>();
            }
            else t.add(a.get(i));
        }

        for (int i = 0; i < array.size(); i++) {
            System.out.println(array.get(i));
        }

    }
}
