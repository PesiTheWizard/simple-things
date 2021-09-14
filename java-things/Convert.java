public class Convert
{
	public static double lightsecondtometer(double ls)
	{
		return 299792458*ls;
	}
	public static double inchtometer(double inch)
	{
		return 0.0254*inch;
	}
	public static double BRUSfoottometer(double feet)
	{
		return 0.3048*feet;
	}
	public static double danishfoottometer(double df)
	{
		return 139.13*df/443.296;
	}
	public static double yardtometer(double y)
	{
		return y*0.9144;
	}
	public static double BRUSmiletometer(double m)
	{
		return m*1609.344;
	}
	public static double swedishmiletometer(double m)
	{
		return m*10000;
	}
	public static double nauticalmiletometer(double nm)
	{
		return nm*1852;
	}
	public static double AUtokm(double au)
	{
		return au*149597870.7;
	}
	public static double barreltoUSgallon(double b)
	{
		return b*42;
	}
	public static double USgallontocubeinch(double Ug)
	{
		return Ug*231;
	}
	public static double BRgallontocubeinch(double Bg)
	{
		return Bg*277.42;
	}
	public static double USfloztoUSpint(double oz)
	{
		return oz/16.0;
	}
	public static double BRfloztoBRpint(double oz)
	{
		return oz/20.0;
	}
	public static double BRUSlbtokg(double lb)
	{
		return lb*0.45359237;
	}
	//tons
	//cwt
	public static double BRstonetolb(double s)
	{
		return s*14.0;
	}
	public static double BRUSoztolb(double oz)
	{
		return oz/16.0;
	}
	public static double karattogram(double k)
	{
		return k*0.2;
	}
	public static double FtoC(double f)
	{
		return (5.0/9.0)*(f-32);
	}
	public static double CtoF(double c)
	{
		return (9.0/5.0)*c+32;
	}
	public static double CtoK(double c)
	{
		return c+273.15;
	}
	public static void main(String[] args)
	{
		double inp = 1;
		if(args.length > 0)
		{
			inp = Double.parseDouble(args[0]);
		}
		System.out.println("Stand back, I'm going to try SCIENCE!");
		System.out.println(inp+" -> lightsecondtometer -> "+lightsecondtometer(inp));
		System.out.println(inp+" -> inchtometer -> "+inchtometer(inp));
		System.out.println(inp+" -> BRUSfoottometer -> "+BRUSfoottometer(inp));
		System.out.println(inp+" -> danishfoottometer -> "+danishfoottometer(inp));
		System.out.println(inp+" -> yardtometer -> "+yardtometer(inp));
		System.out.println(inp+" -> BRUSmiletometer -> "+BRUSmiletometer(inp));
		System.out.println(inp+" -> swedishmiletometer -> "+swedishmiletometer(inp));
		System.out.println(inp+" -> nauticalmiletometer -> "+nauticalmiletometer(inp));
		System.out.println(inp+" -> AUtokm -> "+AUtokm(inp));
		System.out.println(inp+" -> barreltoUSgallon -> "+barreltoUSgallon(inp));
		System.out.println(inp+" -> USgallontocubeinch -> "+USgallontocubeinch(inp));
		System.out.println(inp+" -> BRgallontocubeinch -> "+BRgallontocubeinch(inp));
		System.out.println(inp+" -> USfloztoUSpint -> "+USfloztoUSpint(inp));
		System.out.println(inp+" -> BRfloztoBRpint -> "+BRfloztoBRpint(inp));
		System.out.println(inp+" -> BRUSlbtokg -> "+BRUSlbtokg(inp));
		System.out.println(inp+" -> BRstonetolb -> "+BRstonetolb(inp));
		System.out.println(inp+" -> BRUSoztolb -> "+BRUSoztolb(inp));
		System.out.println(inp+" -> karattogram -> "+karattogram(inp));
		System.out.println(inp+" -> FtoC -> "+FtoC(inp));
		System.out.println(inp+" -> CtoF -> "+CtoF(inp));
		System.out.println(inp+" -> CtoK -> "+CtoK(inp));
	}
}
