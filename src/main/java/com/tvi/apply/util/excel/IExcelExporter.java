package com.tvi.apply.util.excel;

import java.util.List;
import java.util.Map;

public interface IExcelExporter
{
	String export(List<Object> objs, String filename);
	String export(List<Object> objs, String filename, Map<String, String> name);
}
