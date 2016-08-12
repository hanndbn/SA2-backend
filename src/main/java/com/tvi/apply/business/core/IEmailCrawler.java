//thanhpk
package com.tvi.apply.business.core;

import com.tvi.apply.util.mail.IEmailHelper;

public interface IEmailCrawler
{
	public void run(IEmailHelper helper, IEmailMgt mgr, long t);
}
