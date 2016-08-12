package com.tvi.common.entity;  
import java.util.Date; 
/**
 *
 * @author ManhNV
 */
@Deprecated
    public class CaptchaEntity    {

    public final long capid;

    public final Date ctime;

    public final String answer;

    public CaptchaEntity(   long capid_in ,Date ctime_in ,String answer_in  )  {
        this.capid = capid_in;
        this.ctime = ctime_in;
        this.answer = answer_in;
    }

}