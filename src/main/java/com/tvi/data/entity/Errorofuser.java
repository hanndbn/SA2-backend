package com.tvi.data.entity; 
/**
 *
 * @author CAFEITVN.COM
 */

@Deprecated
    public class Errorofuser    {

    public final long eid;

    public final long uid;

    public final int readed;

    public Errorofuser(   long eid_in ,long uid_in ,int readed_in  )  {
        this.eid = eid_in;
        this.uid = uid_in;
        this.readed = readed_in;
    }

}