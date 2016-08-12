package com.tvi.apply.util.excel;

import com.tvi.apply.util.file.IFileHelper;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelExporter implements IExcelExporter
{
	private IFileHelper filehpr;

	public ExcelExporter(IFileHelper filehpr)
	{
		this.filehpr = filehpr;
	}

	@Override
	public String export(List<Object> o, String filename)
	{
		String rfilepath = filehpr.saveFile(null, ".xls", filename);
		String filepath = filehpr.convertToAbsolute(rfilepath);
		try
		{
			WritableWorkbook workbook = Workbook.createWorkbook(new File(filepath));

			WritableSheet sheet = workbook.createSheet("sheet1", 0);
			Class<?> c = o.get(0).getClass();
			Field[] fields = c.getDeclaredFields();

			for (int i = 0; i < o.size(); i++)
			{
				int k = 0;
				for (Field f : fields)
				{

					f.setAccessible(true);
					Object proper = f.get(o.get(i));
					sheet.addCell(new Label(k, 0, f.getName()));

					if (f.getClass().isArray())
					{
						Object[] arr = (Object[]) proper;

						String strprop = "";
						for (Object ob : arr)
						{
							strprop += ob.toString();
						}
						sheet.addCell(new Label(k, (i + 1), strprop));
					} else if (f.getType().getSimpleName().equals("String"))
					{
						sheet.addCell(new Label(k, (i + 1), proper.toString()));
					} else if (f.getType().getSimpleName().equals("int"))
					{
						sheet.addCell(new jxl.write.Number(k, (i + 1), Integer.parseInt(proper.toString())));
					} else if (f.getType().getSimpleName().equals("float"))
					{
						sheet.addCell(new jxl.write.Number(k, (i + 1), Float.parseFloat(proper.toString())));
					} else if (f.getType().getSimpleName().equals(" double"))
					{
						sheet.addCell(new jxl.write.Number(k, (i + 1), Double.parseDouble(proper.toString())));
					}
					k++;
				}
			}
			workbook.write();
			workbook.close();
			return rfilepath;
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String export(List<Object> o, String filename, Map<String, String> name)
	{
		String rfilepath = filehpr.saveFile(null, ".xls", filename);
		String filepath = filehpr.convertToAbsolute(rfilepath);
		try
		{
			WritableWorkbook workbook = Workbook.createWorkbook(new File(filepath));

			WritableSheet sheet = workbook.createSheet("sheet1", 0);
			Class<?> c = o.get(0).getClass();
			Field[] fields = c.getDeclaredFields();
			String[] ignore = {"jobid", "junk","star","color","attachment","englishteststate","iqteststate",
					"quanlified","spec"};;

			for (int i = 0; i < o.size(); i++)
			{
				int k = 0;
				for (Field f : fields)
				{
					f.setAccessible(true);
					if(!Arrays.asList(ignore).contains(f.getName()))
					{
						Object proper = f.get(o.get(i));
						sheet.addCell(new Label(k, 0, name.get(f.getName())));

						if (f.getClass().isArray())
						{
							Object[] arr = (Object[]) proper;

							String strprop = "";
							for (Object ob : arr)
							{
								strprop += ob.toString();
							}
							sheet.addCell(new Label(k, (i + 1), strprop));
						} else if (f.getType().getSimpleName().equals("String"))
						{
							sheet.addCell(new Label(k, (i + 1), proper.toString()));
						} else if (f.getType().getSimpleName().equals("int"))
						{
							if(f.getName() == "resumestatus"){
								String status ="";
								switch (Integer.parseInt(proper.toString())){
									case 0:{
										status = "Chưa phỏng vấn";
										break;
									}
									case 1:{
										status = "Chọn để phỏng vấn";
										break;
									}
									case 2:{
										status = "Đã phỏng vấn";
										break;
									}
									case 3:{
										status = "Đã xác nhận";
										break;
									}
									case 4:{
										status = "Loại";
										break;
									}
								}
								sheet.addCell(new Label(k, (i + 1), status));
							}else sheet.addCell(new jxl.write.Number(k, (i + 1), Integer.parseInt(proper.toString())));
						} else if (f.getType().getSimpleName().equals("float"))
						{
							sheet.addCell(new jxl.write.Number(k, (i + 1), Float.parseFloat(proper.toString())));
						} else if (f.getType().getSimpleName().equals(" double"))
						{
							sheet.addCell(new jxl.write.Number(k, (i + 1), Double.parseDouble(proper.toString())));
						} else if(f.getType().getSimpleName().equals("Calendar"))
						{
//							sheet.addCell(new Label(k, (i + 1), currentDate));
						} else if(f.getType().getSimpleName().equals("boolean"))
						{
							String text = "";
							if(f.getName() == "star" || f.getName() == "junk"){
								if(proper.equals(true)) text = "Có";
							}else if(f.getName() == "gender"){
								text = "Nữ";
								if(proper.equals(true)) text = "Nam";
							}
							sheet.addCell(new Label(k, (i + 1), text));
						}
						k++;
					}
				}
			}
			workbook.write();
			workbook.close();
			return rfilepath;
		} catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
