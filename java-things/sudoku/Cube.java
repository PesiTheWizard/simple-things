import java.io.BufferedReader;
import java.io.IOException;

public class Cube
{
	private static final String[] houseType = {"Row #","Column #","Box #"};

	private final char[] Alphabet;
	private final int shortLength;//length of a single inner box
	private final Scell[] CubeCells;
	private final Shouse[] CubeHouses;
	private final Spuzzle Cube3D;
	private final String topPrintLine;
	private final String middlePrintLine;
	private final String bottomPrintLine;

	public Cube(String rawAB, String[] rawPF) throws Exception
	{
		final char[] tAlphabet = rawAB.toCharArray();
		final int al = tAlphabet.length;
		if(al<4 || al>64){throw new Exception("Bad puzzle size");}
		final int bl = (int)Math.sqrt(al);
		if(bl*bl != al){throw new Exception("Cube length must be square");}
		if(rawPF.length != al){throw new Exception("Layer count must equal alphabet length");}
		Alphabet = tAlphabet;//                                                                  Line 1 established
		shortLength = bl;//                                                                      Line 2 established
		final int ecc = al*al;//expected cell count per layer
		final int etcc = al*al*al;//expected total cell count
		final long emptyMask = ( (al==64)?(-1L):((1L<<al)-1) );
		final char sas[][] = new char[al][];
		Scell FdivCellArrays[][] = new Scell[al][];
		Shouse FdivBasicHouses[][] = new Shouse[al][];
		int cellCount = 0, houseCount = 0;
		for(int i=0;i<al;i++)
		{
			sas[i] = rawPF[i].toCharArray();
			if(sas[i].length != ecc){throw new Exception("Cell count mismatch in layer "+(i+1)+" (expected "+ecc+", got "+sas[i].length+")");}
			FdivCellArrays[i] = charToCellConverter(sas[i],i,emptyMask);
			cellCount+=FdivCellArrays[i].length;
			FdivBasicHouses[i] = basicHouseMaker(FdivCellArrays[i],i,emptyMask);
			houseCount+=FdivBasicHouses[i].length;
		}
		if(etcc != cellCount){throw new Exception("Midterm cellcount mismatch ("+etcc+" vs "+cellCount+")");}
		Scell fullCellArray[] = new Scell[etcc];
		for(int i=0;i<etcc;i++)
		{//cellCount is etcc in al superarrays, each with ecc cells
			fullCellArray[i] = FdivCellArrays[i/ecc][i%ecc];
		}
		CubeCells = fullCellArray;//                                                             Line 3 established
		if(ecc*3 != houseCount){throw new Exception("Midterm housecount mismatch ("+(ecc*3)+" vs "+houseCount+")");}

		final int cpcl = ecc*shortLength;//cells per cake-layer (needs a better name), same as (etcc/shortLength)
		final int incl = al*shortLength;//cells per inner thingy, same as (ecc/shortLength)
		Scell HCdimH[][][] = new Scell[shortLength][incl][al];//per cake-layer, incl new houses, each with al cells.
		Scell HCdimV[][][] = new Scell[shortLength][incl][al];
		for(int i=0, temp;i<etcc;i++)//doing everything at once
		{
			temp = i%cpcl;//relative layer-position, a.k.a. Cell being worked with.
			HCdimH[i/cpcl][(temp%ecc)/shortLength][(temp%shortLength)+(temp/ecc)*shortLength] = fullCellArray[i];//Horizontal cake-split box-houses, ie. boxes of the dimension above/below
			HCdimV[i/cpcl][((temp%ecc)/incl)*al+(temp%al)][(temp%incl)/al+(temp/ecc)*shortLength] = fullCellArray[i];//Vertical cake-split box-houses, ie. boxes of the dimension to the left/right
		}
		Shouse dimBox[] = new Shouse[ecc*3];//think two Shouse[shortLength][incl]'s; pillars added later
		for(int i=0;i<shortLength;i++)
		{
			for(int j=0, temp;j<incl;j++)
			{
				temp = i*incl+j;
				dimBox[temp] = new Shouse(HCdimH[i][j],"Hbox-outer #"+(i+1)+", inner #"+(j+1),emptyMask);
				dimBox[ecc+temp] = new Shouse(HCdimV[i][j],"Vbox-outer #"+(i+1)+", inner #"+(j+1),emptyMask);
			}
		}//one set of ecc yet to be constructed

		Scell HCpillars[][] = new Scell[ecc][al];//when you drill straight down through the al sudokus, that's a pillar
		for(int i=0;i<etcc;i++)
		{
			HCpillars[i%ecc][i/ecc] = fullCellArray[i];
		}
		for(int i=0, buffer=ecc*2;i<ecc;i++)//doing this seperately makes the math work out
		{
			dimBox[buffer+i] = new Shouse(HCpillars[i],("c"+(i%al+1)+"r"+(i/al+1)+"-pillar"),emptyMask);
		}
		Shouse finHouseArray[] = new Shouse[houseCount+dimBox.length];
		int gc = 0;
		for(int i=0;i<al;i++)
		{
			for(int j=0;j<FdivBasicHouses[i].length;j++)
			{
				finHouseArray[gc] = FdivBasicHouses[i][j];
				gc++;
			}
		}
		for(int i=0;i<dimBox.length;i++)
		{
			finHouseArray[gc] = dimBox[i];
			gc++;
		}
		CubeHouses = finHouseArray;//                                                            Line 4 established
		Cube3D = new Spuzzle(Alphabet,CubeCells,CubeHouses);
		if(!Cube3D.baseConfirm()){throw new Exception("baseConfirm error");}
		//building print strings
		String sS = "";
		for(int i=0;i<shortLength;i++)
		{
			sS+=Spuzzle.lr;
		}
		String tS=Spuzzle.rd+sS, mS=Spuzzle.urd+sS, bS=Spuzzle.ur+sS;
		for(int i=1;i<shortLength;i++)
		{
			tS+=(Spuzzle.lrd+sS);
			mS+=(Spuzzle.X+sS);
			bS+=(Spuzzle.ulr+sS);
		}
		topPrintLine=tS+Spuzzle.ld;
		middlePrintLine=mS+Spuzzle.uld;
		bottomPrintLine=bS+Spuzzle.ul;
	}
	private Scell[] charToCellConverter(char[] rca, int layer, long eMask) throws Exception//only called by constructor
	{//expects Alphabet to be established
		final int y = Alphabet.length;
		Scell cellsList[] = new Scell[rca.length];
		String cellname;
		buildingTheList:for(int i=0;i<rca.length;i++)
		{
			cellname = "L"+(layer+1)+"c"+(i%y+1)+"r"+(i/y+1);
			for(int j=0;j<y;j++)
			{
				if(rca[i]==Alphabet[j])
				{
					cellsList[i] = new Scell(1L<<j,cellname);
					continue buildingTheList;
				}
			}
			//empty cell
			cellsList[i] = new Scell(eMask,cellname);
		}
		return cellsList;
	}
	private Shouse[] basicHouseMaker(Scell[] workingPuzzle, int layer, long eMask) throws Exception//only called by constructor
	{//expects Alphabet and shortLength to be established
		final int y = Alphabet.length;
		Scell tempHC[][][] = new Scell[3][y][y];
		for(int i=0,vDiv=-1,vMod=-1,boxID=-1,boxRel=-1;i<workingPuzzle.length;i++)
		{
			vDiv = i/y;
			vMod = i%y;
			boxID = (vDiv/shortLength)*shortLength+(vMod/shortLength);
			boxRel = (vDiv%shortLength)*shortLength+(i%shortLength);
			tempHC[0][vDiv][vMod] = workingPuzzle[i];//rows
			tempHC[1][vMod][vDiv] = workingPuzzle[i];//columns
			tempHC[2][boxID][boxRel] = workingPuzzle[i];//boxes
		}
		final int nhc = y*3;//rows, columns and boxes
		String housename;
		Shouse houseList[] = new Shouse[nhc];
		for(int i=0;i<nhc;i++)
		{
			housename = "Layer #"+(layer+1)+", "+houseType[i/y]+(i%y+1);
			houseList[i] = new Shouse(tempHC[i/y][i%y],housename,eMask);
		}
		return houseList;
	}

	public static void main(String[] args) throws Exception
	{
		BufferedReader in = new BufferedReader(new java.io.InputStreamReader(System.in));
		String ab, flr[];
		try
		{
			ab = in.readLine();
		}
		catch(IOException e){System.err.println(e);return;}
		final int l = ab.length();
		flr = new String[l];
		for(int i=0;i<l;i++)
		{
			try
			{
				flr[i] = in.readLine();
			}
			catch(IOException e){System.err.println(e);return;}
		}
		Cube MyCube = new Cube(ab,flr);
		MyCube.printBoard();
		System.out.println(MyCube.Cube3D.Solve(MyCube.Alphabet.length/2)+" findings");
		MyCube.printBoard();
		System.out.println(MyCube.Cube3D.countRemainingCells()+" cells unsolved");
		System.out.println(MyCube.Cube3D.countRottenBoroughs()+" rotten boroughs");
	}

	public void printBoard()
	{
		final int bxs = Alphabet.length*Alphabet.length;
		final int LRCC = bxs*shortLength;//layer-row cell count
		StringBuilder op = new StringBuilder();
		for(int a=0, b;a<shortLength;a++)//each layer-row
		{
			b = a*LRCC;//starting index for the layer-row
			for(int i=0;i<shortLength;i++)
			{op.append(topPrintLine);}
			op.append("\n");

			for(int i=0,buf2=b-Alphabet.length;i<Alphabet.length;i++)//think "columns down", b starts at 0
			{
				buf2+=Alphabet.length;//total of i*Alphabet.length within b, first loop brings it up to b
				if(i%shortLength==0 && i>0)
				{
					for(int j=0;j<shortLength;j++)
					{op.append(middlePrintLine);}
					op.append("\n");
				}
				for(int j=0;j<shortLength;j++)
				{//print a row, move an entire layer over, print another row...
					for(int k=0,d=j*bxs+buf2;k<Alphabet.length;k++)//j*bxs+buf2 : index from which Alphabet.length characters are printed
					{
						if(k%shortLength==0)
						{op.append(Spuzzle.ud);}//lines,boxes start with this
						op.append(CubeCells[k+d].t?Alphabet[CubeCells[k+d].fe]:" ");
					}
					op.append(Spuzzle.ud);//line ends with this
				}//each bump of j moves the index a whole layer, each addition to buf2 moves the index one "column" down
				op.append("\n");//have printed Alphabet.length*shortLength chars, end of line
			}

			for(int i=0;i<shortLength;i++)
			{op.append(bottomPrintLine);}
			op.append("\n");
		}
		System.out.print(op);
	}
}