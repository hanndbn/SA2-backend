/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tvi.data.entity;

/**
 *
 * @author Manh
 */
public class UpEntity2 {

    public final long uid;

    public final String username;

    public final byte[] password;


    public UpEntity2(long uid_in, String username_in, byte[] password_in) {
        this.uid = uid_in;
        this.username = username_in;
        this.password = new byte[password_in.length];
        System.arraycopy(password_in, 0, this.password, 0, password_in.length);
    }



}
