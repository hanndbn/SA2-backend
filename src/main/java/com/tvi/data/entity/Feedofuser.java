package com.tvi.data.entity;  
/**
 *
 * @author CAFEITVN.COM
 */

@Deprecated
    public class Feedofuser    {

    public final long uid;

    public long fid;

    public int readed;

    public Feedofuser(long uid_in ,long fid_in ,int readed_in  )  {
        this.uid = uid_in;
        this.fid = fid_in;
        this.readed = readed_in;
    }
}