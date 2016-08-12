/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.common.util;

import java.util.Date;
import java.util.Random;

/**
 *
 * @author Manh
 */
public class CreateTestV2 {

    public static int[] randomTest(int a_in, int b_in, int c_in, int n_in, int p_in) {
        int k = 0;
        int r = 0;
        int[] bc;
        int[] abc = null;
        for (int i = 0; i <= a_in; i++) {
            bc = findBC(i, p_in, n_in);
            if (bc[0] >= 0 && bc[0] <= b_in && bc[1] >= 0 && bc[1] <= c_in) {
                k += 1;
            }
        }
        if (k == 0) {
            return new int[]{
                -1, -1, -1
            };
        }
        if (k != 0) {
            r = new Random(new Date().getTime()).nextInt(k) + 1;
        } else {
            return null;
        }
        k = 0;
        for (int i = 0; i <= a_in; i++) {
            bc = findBC(i, p_in, n_in);
            if (bc[0] >= 0 && bc[0] <= b_in && bc[1] >= 0 && bc[1] <= c_in) {
                k += 1;
                if (k == r) {
                    abc = new int[]{
                        i, bc[0], bc[1]
                    };
                    break;
                }
            }
        }
        return abc;
    }

    public static int[] findBC(int a, int p, int n) {
        return new int[]{
            3 * n - p - 2 * a, p - 2 * n + a
        };
    }

    public static int countTest(int a_in, int b_in, int c_in, int n_in, int p_in) {
        int k = 0;
        int r = 0;
        int[] bc;
        int[] abc = null;
        for (int i = 0; i <= a_in; i++) {
            bc = findBC(i, p_in, n_in);
            if (bc[0] >= 0 && bc[0] <= b_in && bc[1] >= 0 && bc[1] <= c_in) {
                k += 1;
            }
        }
        return k;
    }

    
}
