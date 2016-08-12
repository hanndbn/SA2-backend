package com.tvi.common.entity;

import java.util.Date;

public class RequiredField {

    public final long id;
    public final String title;
    public final int state;
    public final Date ctime;
    public final long creator;

    public RequiredField(long id, String title, int state, Date ctime, long creator) {
        this.id = id;
        this.title = title;
        this.state = state;
        this.ctime = ctime;
        this.creator = creator;
    }
}
