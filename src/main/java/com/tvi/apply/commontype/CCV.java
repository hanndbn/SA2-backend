
package com.tvi.apply.commontype;

import com.tvi.common.entity.CVEntityV2;

public class CCV extends CVEntityV2
{
	public final String iqtestaccess;
	public final String engtestaccess;
	
	public CCV(CVEntityV2 cv, String iqtestaccess, String engtestaccess)
	{
		super(cv.id, cv.ctime, cv.state, cv.level, cv.filename, cv.name, cv.email, cv.cantesteng, cv.cantestiq, cv.iqtested, cv.engtested, cv.iq, cv.pres, cv.arch, cv.sum, cv.total, cv.pote, cv.eng, cv.receivedmail, cv.iqtestresultmail, cv.engtestresultmail);
		this.iqtestaccess = iqtestaccess;
		this.engtestaccess = engtestaccess;
	}
	
}
