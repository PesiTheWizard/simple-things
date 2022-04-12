import java.io.BufferedReader;
import java.io.IOException;

public class Samurai
{
	private static final String[] houseType = {"Row #","Column #","Box #"};

	private final char[] Alphabet;
	private final Scell[] SamCellList;
	private final Shouse[] SamHouseList;
	private final Spuzzle SamuraiGrid;
	private final int BW, BH, GW, GH;

	public Samurai(String gAlphabet, int boxW, int boxH, int samW, int samH, String[] rawStrings, int bonusHouses) throws Exception
	{
		final char gABarray[] = gAlphabet.toCharArray();
		final int l = gABarray.length;
		if(l < 4 || l > 64)
		{throw new Exception("Bad puzzle size ("+l+")");}
		if(boxW < 2 || boxH < 2)
		{throw new Exception("Bad box sizes");}
		if(samW < 2 || samH < 2)
		{throw new Exception("Bad grid sizes");}
		if(l%boxW != 0 || l%boxH != 0 || boxW*boxH != l)
		{throw new Exception("Bad box fit");}
		final int ipuzc = (samW-1)*(samH-1);//inner puzzle count
		final int tpuzc = (samW*samH+ipuzc);//total puzzle count
		if(tpuzc != rawStrings.length)
		{throw new Exception("Puzzle count mismatch (expected "+tpuzc+", got "+rawStrings.length+")");}
		final int pcc = l*l;//puzzle cell count
		char rawChars[][] = new char[tpuzc][];
		for(int i=0;i<tpuzc;i++)
		{
			rawChars[i] = rawStrings[i].toCharArray();
			if(rawChars[i].length != pcc)
			{throw new Exception("Cell count mismatch in puzzle #"+(i+1)+" (expected "+pcc+", got "+rawChars[i].length+")");}
		}
		final int cakeLayer = samW*2-1;//a row and its subrow, puzzle count
		final int totalCells = (pcc*tpuzc-ipuzc*l*4);//leaving out the 4 corners of each inner puzzle, each with l cells
		final long emptyMask = ( (l==64)?(-1L):((1L<<l)-1) );
		final int cornerBoxIDs[] = {0,(l/boxW)-1,l-boxW,l-1};//top-left, top-right, bottom-left, bottom-right
		Scell cellPile[][] = new Scell[tpuzc][pcc];//keep the inner corner-box cells away until it's time to build the houses
		Scell cellarray[] = new Scell[totalCells];//let's build this at the same time
		Scell tempc;
		int cmcr = 0;//cell-make counter, for safety
		for(int i=0;i<tpuzc;i++)
		{
			for(int j=0,boxID;j<pcc;j++)
			{
				boxID = ((j/l)/boxH)*boxH+(j%l)/boxW;//marks box of working cell, from 0 to l-1 in order
				if(i%cakeLayer >= samW &&
					(boxID==cornerBoxIDs[0] ||
					boxID==cornerBoxIDs[1] ||
					boxID==cornerBoxIDs[2] ||
					boxID==cornerBoxIDs[3])
				)//inner puzzle and corner-box
				{//can't add cells yet, bottom boxes aren't constructed at this point
					continue;//not constructing a new cell
				}
				tempc = null;//made it here? Not in a shared box
				for(int k=0;k<l;k++)
				{
					if(rawChars[i][j]==gABarray[k])
					{
						tempc = new Scell(1L<<k,"P"+(i+1)+"c"+((j%l)+1)+"r"+(j/l+1));
						break;
					}
				}
				if(tempc==null)//meaning not found in the alphabet
				{tempc = new Scell(emptyMask,"P"+(i+1)+"c"+((j%l)+1)+"r"+(j/l+1));}
				cellPile[i][j] = tempc;
				cellarray[cmcr++] = tempc;
			}
		}
		if(cmcr!=totalCells)
		{throw new Exception("I have forgotten how to count! ("+cmcr+" vs "+totalCells+")");}
		Alphabet = gABarray;
		SamCellList = cellarray;//cellarray is now the parameter for Spuzzle constructor
		final int shiftmap[] = {(pcc-(boxH-1)*l-boxW),(pcc-(boxH+1)*l+boxW),-(pcc-(boxH+1)*l+boxW),-(pcc-(boxH-1)*l-boxW)};//maps from shared cell in inner puzzle to corresponding cell in other puzzle
		for(int i=0;i<tpuzc;i++)
		{//filling in the nulls, now that all cells have been constructed
			if(i%cakeLayer < samW)
			{continue;}//only do inner puzzles
			for(int j=0,boxID;j<pcc;j++)
			{
				boxID = ((j/l)/boxH)*boxH+(j%l)/boxW;
				if(boxID==cornerBoxIDs[0])//in top-left box
				{
					cellPile[i][j] = cellPile[i-samW][j+shiftmap[0]];
				}
				else if(boxID==cornerBoxIDs[1])//in top-right box
				{
					cellPile[i][j] = cellPile[i-samW+1][j+shiftmap[1]];
				}
				else if(boxID==cornerBoxIDs[2])//in bottom-left box
				{
					cellPile[i][j] = cellPile[i+samW-1][j+shiftmap[2]];
				}
				else if(boxID==cornerBoxIDs[3])//in bottom-right box
				{
					cellPile[i][j] = cellPile[i+samW][j+shiftmap[3]];
				}//else not in a cornerbox and we don't care
			}
		}//cellPile should have no nulls now
		Shouse moProblems[][] = new Shouse[tpuzc][];
		int houseCount = 0;
		for(int i=0;i<tpuzc;i++)
		{
			moProblems[i] = houseBuilder(cellPile[i],i,boxW,boxH,i%cakeLayer >= samW,cornerBoxIDs,emptyMask,bonusHouses);
			houseCount+=moProblems[i].length;
		}
		Shouse allHouses[] = new Shouse[houseCount];
		int secondaryHouseCount = 0;
		for(int i=0;i<tpuzc;i++)
		{
			for(int j=0;j<moProblems[i].length;j++)
			{
				allHouses[secondaryHouseCount++] = moProblems[i][j];
			}
		}
		if(houseCount != secondaryHouseCount)
		{throw new Exception("Final house count failed ("+houseCount+" vs "+secondaryHouseCount+")");}
		SamHouseList = allHouses;
		BW = boxW;
		BH = boxH;
		GW = samW;
		GH = samH;
		SamuraiGrid = new Spuzzle(Alphabet,SamCellList,SamHouseList);
		if(!SamuraiGrid.baseConfirm()){throw new Exception("baseConfirm error");}
		/*deary me, time for the printstrings
		needs a special case for boxlengths of 2
		*/
	}
	private Shouse[] houseBuilder(Scell[] floorplan, int adr, int bX, int bY, boolean isInner, int[] cbIDs, long eM, int eH) throws Exception
	{
		final int l = Alphabet.length;
		Scell prep[][][] = new Scell[3][l][l];//3 for rows, columns and boxes
		eH = eH&3;//3 = only doing 2 extra houses
		final int extras = Integer.bitCount(eH);
		Scell bns[][] = new Scell[2][l];
		for(int i=0,vDiv,vMod,bID,bRel;i<floorplan.length;i++)
		{
			if(floorplan[i]==null){throw new Exception("cellPile["+adr+"]["+i+"] is actually a null");}
			vDiv = i/l;
			vMod = i%l;
			bID = (vDiv/bY)*bY+(vMod/bX);
			bRel = (vDiv%bY)*bX+(i%bX);
			prep[0][vDiv][vMod] = floorplan[i];//row
			prep[1][vMod][vDiv] = floorplan[i];//column
			prep[2][bID][bRel] = floorplan[i];//boxes generated as normal for now...
			if(vDiv+vMod+1 == l)//rising diagonal
			{bns[0][vDiv] = floorplan[i];}
			if(vDiv==vMod)//falling diagonal
			{bns[1][vDiv] = floorplan[i];}
		}//there's room for expanding into disjoint-box-sets if needed
		if(isInner)
		{//nullify corner boxes
			prep[2][cbIDs[0]]=null;
			prep[2][cbIDs[1]]=null;
			prep[2][cbIDs[2]]=null;
			prep[2][cbIDs[3]]=null;
		}
		final int HC = l*3-(isInner?4:0)+extras;
		Shouse cHL[] = new Shouse[HC];//current house list
		int geC = 0;
		for(int i=0;i<3;i++)
		{
			for(int j=0;j<l;j++)
			{
				if(prep[i][j]!=null)
				{
					cHL[geC++] = new Shouse(prep[i][j],"Puzzle #"+(adr+1)+", "+houseType[i]+(j+1),eM);
				}
			}
		}
		if((eH&1)==1)
		{
			cHL[geC++] = new Shouse(bns[0],"Puzzle #"+(adr+1)+", rising diagonal",eM);
		}
		if((eH&2)==2)
		{
			cHL[geC++] = new Shouse(bns[1],"Puzzle #"+(adr+1)+", falling diagonal",eM);
		}
		if(geC!=HC)
		{throw new Exception("houseBuilder can't count ("+geC+" vs "+HC+")");}
		return cHL;
	}

	public static void main(String args[]) throws Exception
	{
		BufferedReader in = new BufferedReader(new java.io.InputStreamReader(System.in));
		String readAB, bxs[], grds[], rawinp[];
		try
		{
			readAB = in.readLine();
			bxs = in.readLine().split(" ");//box sizes
			grds = in.readLine().split(" ",2);//grid sizes
		}
		catch(IOException e){System.err.println(e);return;}
		int boxX, boxY, samX, samY, cR=0;
		boxX = Integer.parseInt(bxs[0]);
		boxY = Integer.parseInt(bxs[1]);
		if(bxs.length >= 3)
		{cR = Integer.parseInt(bxs[2]);}
		samX = Integer.parseInt(grds[0]);
		samY = Integer.parseInt(grds[1]);
		final int pc=(samX*samY+(samX-1)*(samY-1));//puzzlecount
		rawinp = new String[pc];
		for(int i=0;i<pc;i++)
		{
			try
			{
				rawinp[i] = in.readLine();
			}
			catch(IOException e){System.err.println(e);return;}
		}
		Samurai MySamurai = new Samurai(readAB,boxX,boxY,samX,samY,rawinp,cR);
		System.out.println(MySamurai.SamuraiGrid.Solve(MySamurai.Alphabet.length/2)+" findings");
		System.out.println(MySamurai.SamuraiGrid.countRemainingCells()+" cells unsolved");
		System.out.println(MySamurai.SamuraiGrid.countRottenBoroughs()+" rotten boroughs");
	}
}