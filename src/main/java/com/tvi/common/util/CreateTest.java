/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.common.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Manh
 */
@Deprecated
public class CreateTest {
public static int[] getRandomQuestion(int[] list_in, int n_in, int p_in, int k) {
        int n = n_in;
        int p = p_in;
        int value = -1;
        int[] tmp = new int[list_in.length];
        int tm;
        for (tm = 0; tm < list_in.length; tm++) {
            tmp[tm] = 0;
        }
        Random ran = new Random();
        if (n_in < 1 || p_in < n_in || n <= 0 || p <= 0) {
            return tmp;
        }
        if (n == 1 && list_in.length > p_in) {
            if (list_in[p_in] >= 1) {
                return new int[]{p_in};
            }
        } else if (n == 1 && list_in.length < p_in) {
            return null;
        } else {
            int j = 0;
            while (j < 80000) {
                int i ;
                for (i = 0; i < list_in.length; i++) {
                    if (ran.nextBoolean()) continue;
                    while (true) {
                        if (list_in[i] + 1 < n) {
                            value = ran.nextInt(list_in[i] + 1);
                        } else {
                            value = ran.nextInt(n);
                        }
                        if ((value * (i + 1)) < p) {
                            n = n - value;
                            p = p - value * (i + 1);
                            tmp[i] = value;
                            break;
                        }
                    }
                    // Lua chon xong duoc mot value
                    if (p <= i) {
                        break;
                    }
                    // Khi p < i thi khong con lua chon them nua
                }// end for 
                // Thoa man sai so

                // Ket qua chap nhan duoc
                if (n == 1 && p < list_in.length) {
                    if (tmp[p] < list_in[p]) {
                        tmp[p] += 1;
                        p = 0;
                        n = 0;
                    }
                    if (n != 0)
                    for (int l = 0 ; l < Math.abs(k); l++)
                    if (tmp[p-k] < list_in[p-k]) {
                        tmp[p-k] += 1;
                        p = 0;
                        n = 0;
                    }
                    
                    if (n != 0)
                    for (int l = 0 ; l < Math.abs(k); l++)
                    if (tmp[p+k] < list_in[p+k]) {
                        tmp[p+k] += 1;
                        p = 0;
                        n = 0;
                    }
                    
                }
                if (n == 0 && p == 0) {
                    break;
                }
                n = n_in;
                p = p_in;
                value = 0;
                for (tm = 0; tm < list_in.length; tm++) {
                    tmp[tm] = 0;
                }
                j++;
            }

        }
        return tmp;
    }
}