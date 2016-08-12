package com.tvi.data.entity;  
/**
 *
 * @author CAFEITVN.COM
 */

@Deprecated
    public    class TpEntity    {

    public final long uid;

    public final String thirdpartyid;

    public TpEntity( long uid_in ,String thirdpartyid_in  )  {
        this.uid = uid_in;
        this.thirdpartyid = thirdpartyid_in;
    }

}