public class SimpleThings
{
	public static long StringDivider(long d, char[] s)
	{
		int theend = s.length;
		int currpos = 0;
		long re = 0;
		short gef;
		while(currpos<theend)
		{
			gef = 0;
			re = re*10+Character.digit(s[currpos],10);
			if(d>re)
			{
				s[currpos] = '0';
				currpos++;
				continue;
			}
			while(re>=d)
			{
				re=re-d;
				gef++;
			}
			s[currpos] = Character.forDigit(gef,10);
			currpos++;
		}
		return re;
	}

	public static int Nimsum(int[] p)
	{
		int nimsum = 0;
		for(int i=0;i<p.length;i++)
		{
			nimsum = nimsum ^ p[i];
		}
		return nimsum;
	}

	public static int Pseudorandom(int x, int n, int s, int m)
	{
		return ((x%m)*n+(s%m)) % m;
	}

	public static int cbs1(int[] a, int s, int g, int j, boolean lth)
	{
		if(a.length <= 1 || g <= 0 || j == 0)
		{
			return -1;
		}
		int swaps = 0;
		int temp;
		while(s+g<a.length && s>=0)
		{
			if( ((a[s]<a[s+g]) ^ lth) && (a[s]!=a[s+g]) )
			{
				temp = a[s];
				a[s] = a[s+g];
				a[s+g] = temp;
				swaps++;
			}
			s+=j;
		}
		return swaps;
	}

	public static void main(String[] args)
	{
		System.out.println("Testing StringDivider:");
		String s70 = "1234567890111112222233333444445555566666777778888899999000001234567890";
		char[] sa = s70.toCharArray();
		System.out.println("Expected remainder:115585380");
		System.out.println("Expected result:0000000010000000000900009008280082875357754255756463736383909992993590");
		System.out.println("         Remainder:"+StringDivider(123456789L,sa));
		System.out.println("         Result:" + new String(sa));

		System.out.println("\nTesting Nimsum:");
		int n[] = new int[10];
		for(int i=0;i<n.length;i++)
		{
			n[i] = (int)(Math.random()*100);
			System.out.print(n[i] + " ");
		}
		System.out.println("\nNimsum="+Nimsum(n));
		System.out.println(cbs1(n,0,1,2,true)+" swaps made");
		for(int i=0;i<n.length;i++)
		{
			System.out.print(n[i] + " ");
		}
	}
}
