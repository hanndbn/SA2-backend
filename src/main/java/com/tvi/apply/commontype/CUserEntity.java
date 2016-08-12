package com.tvi.apply.commontype;

import com.tvi.common.entity.UnitEntity;
import java.util.Date;
import java.util.List;

public class CUserEntity
{

	public final String fullname;
	public final long uid;
	public final  List<UnitEntity> units;
	public final Date ctime;

	public CUserEntity(String fullname, long uid, List<UnitEntity> units, Date ctime)
	{
		this.uid = uid;
		this.units = units;
		this.fullname = fullname;
		this.ctime = ctime;
	}

}
