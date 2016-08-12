/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tvi.apply.commontype;

import com.tvi.common.entity.UnitEntity;
import java.util.Date;

/**
 *
 * @author thanhpk
 */

public class CRequest2 extends CRequest {

    private UnitEntity unit;
    public CRequest2(long rid, String jobdesc, String title, String position, String interest, String requirement, long creator, Date ctime, int quantity, int state, long toalcv, long newcv, UnitEntity unit) {
        super(rid, jobdesc, title, position, interest, requirement, creator, ctime, quantity, state, toalcv, newcv);
        this.unit = unit;
    }
}
