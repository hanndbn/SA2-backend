package com.tvi.apply.servlet;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Created by Supertrun on 9/22/2015.
 */
public class test {
    public static String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replace("đ", "d").replaceAll(" ","_");
    }
    public static void main(String[] args) {
        System.out.println(unAccent("đông cuối"));
    }
}
