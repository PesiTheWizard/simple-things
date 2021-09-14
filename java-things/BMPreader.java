import java.io.*;

public class BMPreader
{
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("*Filename missing*");
			System.exit(0);
		}
		byte[] bmn = new byte[2];//magic number
		int intFilesize = 0;//size in bytes
		byte[] res = new byte[4];//reserved
		int intOffset = 0;//offset
		int intDIBsize = 0;//size of DIB header
		int intWidth = 0;//bitmap width in pixels
		int intHeight = 0;//bitmap height in pixels
		boolean fuxoredHeader = false;
		short shortPlanes = 0;//number of planes (1 is the only legal value)
		short shortBPP = 0;//bits per pixel (2^N)
		int intCompressionMethod = -1;
		int intImageSize = 0;
		int intHResolution = -1;
		int intVResolution = -1;
		int intPaletteSize = -1;//number of colors in the color palette (0 defaults to 2^N)
		int intImportantColors = -1;
		RandomAccessFile CF;
		try
		{CF = new RandomAccessFile(args[0],"r");}
		catch(Exception e)
		{
			System.err.println("**Exception**");
			System.err.println(e);
			return;
		}
		System.out.println("reading file...");
		try
		{
			System.out.println("Size of file (OS)     : "+CF.length());
			CF.seek(0);
			CF.read(bmn);
			intFilesize = Integer.reverseBytes(CF.readInt());
			CF.read(res);
			intOffset = Integer.reverseBytes(CF.readInt());//Last BMP header entry (BMP header is always 14 bytes)
			intDIBsize = Integer.reverseBytes(CF.readInt());//First DIB header entry
			if (intDIBsize == 12)// OS/2
			{
				intWidth = Short.reverseBytes(CF.readShort());
				intHeight = Short.reverseBytes(CF.readShort());
				shortPlanes = Short.reverseBytes(CF.readShort());
				shortBPP = Short.reverseBytes(CF.readShort());
			}
			else if (intDIBsize == 40 || intDIBsize == 64 || intDIBsize == 52 || intDIBsize == 56 || intDIBsize == 108 || intDIBsize == 124)
			{
				intWidth = Integer.reverseBytes(CF.readInt());
				intHeight = Integer.reverseBytes(CF.readInt());
				shortPlanes = Short.reverseBytes(CF.readShort());
				shortBPP = Short.reverseBytes(CF.readShort());
				intCompressionMethod = Integer.reverseBytes(CF.readInt());
				intImageSize = Integer.reverseBytes(CF.readInt());
				intHResolution = Integer.reverseBytes(CF.readInt());
				intVResolution = Integer.reverseBytes(CF.readInt());
				intPaletteSize = Integer.reverseBytes(CF.readInt());
				intImportantColors = Integer.reverseBytes(CF.readInt());
			}
			else
			{
				fuxoredHeader = true;
			}
		}
		catch(IOException e)
		{
			System.err.println("**IOException**");
			System.err.println(e);
		}
		try{CF.close();}//.getFilePointer() to save the offset for .seek() later
		catch(IOException e)
		{
			System.err.println("**IOException closing file**");
			System.err.println(e);
		}
		String mn = new String(bmn);
		System.out.println("-------------------------");
		System.out.println("***BMP File Header***");
		System.out.println("Magic number          : " + mn);
		System.out.println("Size of file          : " + intFilesize);
		System.out.println("reserved              : " + res[0] + " " + res[1] + " " + res[2] + " " + res[3]);
		System.out.println("offset                : " + intOffset);
		System.out.println("***DIB Header***");
		System.out.print("Size of header        : " + intDIBsize);
		if (intDIBsize == 12)
		{
			System.out.println("(OS/2 V1)");
		}
		else if (intDIBsize == 40)
		{
			System.out.println("(Windows V3)");
		}
		else if (intDIBsize == 52)
		{
			System.out.println("(Windows V3 w/RGB masks [undocumented])");
		}
		else if (intDIBsize == 56)
		{
			System.out.println("(Windows V3 w/RGB masks and alpha channel [undocumented])");
		}
		else if (intDIBsize == 64)
		{
			System.out.println("(OS/2 V2)");
		}
		else if (intDIBsize == 108)
		{
			System.out.println("(Windows V4)");
		}
		else if (intDIBsize == 124)
		{
			System.out.println("(Windows V5)");
		}
		else
		{
			System.out.println("(unknown)");
		}
		if (!fuxoredHeader)
		{
			System.out.println("bitmap width          : " + intWidth);
			System.out.println("bitmap height         : " + intHeight);
			System.out.println("color planes          : " + shortPlanes);
			System.out.println("bits per pixel        : " + shortBPP);
			if (intDIBsize == 40 || intDIBsize == 52 || intDIBsize == 56 || intDIBsize == 108 || intDIBsize == 124)
			{
				System.out.println("Compression method    : " + intCompressionMethod);
				System.out.println("Image data size       : " + intImageSize);
				System.out.println("Horizontal resolution : " + intHResolution);
				System.out.println("Vertical resolution   : " + intVResolution);
				System.out.println("color palette size    : " + intPaletteSize);
				System.out.println("Important colors      : " + intImportantColors);
				System.out.println("-------------------------");
				System.out.println("Row size padding : " + (intImageSize / (shortBPP / 8) / intHeight - intWidth) + " bytes (maybe)");
			}
		}
		else
		{
			System.out.println("*DIB header reports an unfamiliar size.*");
		}
	}
}
