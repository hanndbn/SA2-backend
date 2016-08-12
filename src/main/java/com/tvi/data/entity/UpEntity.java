package com.tvi.data.entity;

/**
 *
 * @author CAFEITVN.COM
 */


@Deprecated
public class UpEntity {

    public final long uid;

    public final String username;

    public final byte[] password;


    public UpEntity(long uid_in, String username_in, byte[] password_in, int type_in) {
        this.uid = uid_in;
        this.username = username_in;
        this.password = new byte[password_in.length];
        System.arraycopy(password_in, 0, this.password, 0, password_in.length);
    }

}
