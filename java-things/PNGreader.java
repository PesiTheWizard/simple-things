import java.io.RandomAccessFile;
import java.io.IOException;

public class PNGreader
{
	public static void main(String[] args)
	{
		if(args.length <= 0)
		{
			System.err.println("Filename needed");
			return;
		}
		RandomAccessFile CF;
		long fl;
		try
		{
			CF = new RandomAccessFile(args[0],"r");
			fl = CF.length();
			if(fl<20)//minimum needed for signature+1 chunk
			{
				System.out.println("File shorter than 20 bytes, not enough for PNG sig + 1 chunk");
				CF.close();
				return;
			}
			else
			{System.out.println("Filesize: "+fl+" bytes");}

		}
		catch(IOException e){System.err.println(e);return;}//default one-line catcher
		long curs;
		byte sig[] = new byte[8], chty[] = new byte[4];
		try
		{
			CF.read(sig);
			curs = CF.getFilePointer();
		}
		catch(IOException e){System.err.println(e);return;}
		System.out.print("Signature:\n"+(sig[0]&255));
		for(int i=1;i<8;i++)
		{
			System.out.print("\t"+(sig[i]&255));
		}
		assert curs==8;
		int len, CRC;
		System.out.println("\nExpected:\n137\t80\t78\t71\t13\t10\t26\t10\nLength\tType\tCRC");
		do
		{
			try
			{
				if(fl<curs+8)
				{
					System.out.println((fl-curs)+" bytes until EOF unread");
					CF.close();
					return;
				}
				len = CF.readInt();
				CF.read(chty);
				curs=CF.getFilePointer();
				if(fl<curs+len+4 || len<0)
				{
					System.out.println(len+"\t"+new String(chty));
					System.out.println("*chunk length greater than file remainder or negative*");
					CF.close();
					return;
				}
				CF.seek(curs+len);
				CRC = CF.readInt();
				System.out.println(len+"\t"+new String(chty)+"\t"+CRC);
				curs=CF.getFilePointer();
			}
			catch(IOException e){System.err.println(e);return;}
		}while(fl>curs);
		try{CF.close();}
		catch(IOException e){System.err.println(e);}//thank you for playing Wing Commander
	}
}