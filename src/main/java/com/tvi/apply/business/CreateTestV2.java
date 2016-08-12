package com.tvi.apply.business;

import java.util.Date;
import java.util.Random;

public class CreateTestV2
{
	public static int[] randomTest(int a, int b, int c, int n, int p)
	{
		int k = 0;
		int r = 0;
		int[] bc;
		int[] abc = null;
		for (int i = 0; i <= a; i++)
		{
			bc = findBC(i, p, n);
			if (bc[0] >= 0 && bc[0] <= b && bc[1] >= 0 && bc[1] <= c)
			{
				k += 1;
			}
		}
		if (k == 0)
		{
			return new int[]{-1, -1, -1};
		}
		if (k != 0)
		{
			r = new Random(new Date().getTime()).nextInt(k) + 1;
		} else
		{
			return null;
		}
		k = 0;
		for (int i = 0; i <= a; i++)
		{
			bc = findBC(i, p, n);
			if (bc[0] >= 0 && bc[0] <= b && bc[1] >= 0 && bc[1] <= c)
			{
				k += 1;
				if (k == r)
				{
					abc = new int[]{i, bc[0], bc[1]};
					break;
				}
			}
		}
		return abc;
	}

	public static int[] findBC(int a, int p, int n)
	{
		return new int[]{3 * n - p - 2 * a, p - 2 * n + a};
	}

	public static int countTest(int a, int b, int c, int n, int p)
	{
		int k = 0;
		int r = 0;
		int[] bc;
		int[] abc = null;
		for (int i = 0; i <= a; i++)
		{
			bc = findBC(i, p, n);
			if (bc[0] >= 0 && bc[0] <= b && bc[1] >= 0 && bc[1] <= c)
			{
				k += 1;
			}
		}
		return k;
	}
}
