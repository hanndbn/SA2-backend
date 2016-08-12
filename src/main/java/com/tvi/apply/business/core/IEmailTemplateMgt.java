package com.tvi.apply.business.core;

import com.tvi.apply.data.entity.EEmailTemplate;

import java.util.List;

public interface IEmailTemplateMgt
{
	long createTemplate(EEmailTemplate entity);

	EEmailTemplate readTemplate(long id);

	void deleteTemplate(long id);

	void updateTemplate(EEmailTemplate entity);

	/* liệt kê, sắp xếp theo thứ tự giảm dần ngày tạo*/
	List<EEmailTemplate> listTemplate(String keyword, int n, int p);

	long countTemplate(String keyword);
}
