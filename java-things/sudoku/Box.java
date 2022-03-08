import java.io.BufferedReader;
import java.io.IOException;

public class Box
{
	private final char[] BoxAB;
	private final Scell[] BoxCells;
	private final int hz;
	private final int vt;
	private final Shouse[] BoxHouses;
	private final Spuzzle BoxPuzzle;
	private final boolean boxless;
	private final String topPrintLine;
	private final String middlePrintLine;
	private final String bottomPrintLine;

	public Box(String RawAB, int hori, int verti, String rawP, int extraHouseOrders) throws Exception
	{
		final char[] Alphabet = RawAB.toCharArray();
		final int l = Alphabet.length;
		if(l<2 || l>64)
		{throw new Exception("Bad puzzle size");}
		BoxAB = Alphabet;//                                           Line 1 established
		if(hori<0 || verti<0 || hori==1 || verti==1 || ( (hori==0)^(verti==0) ))
		{throw new Exception("Bad box sizes");}
		boolean BXls = false;//boxless
		boolean RBPs = false;//relative box positions
		if(hori==0 && verti==0)
		{
			BXls = true;
		}
		else if(hori*verti!=l || l%hori!=0 || l%verti!=0)
		{throw new Exception("Bad box fit");}
		if((extraHouseOrders&4)==4)
		{
			RBPs = true;
		}
		if(BXls && RBPs)
		{throw new Exception("Relative box positional houses require boxes");}
		final long A = (-1L)>>>(64-l);//bitmask for blank cell
		BoxCells = makeCellList(rawP,A);//                            Line 2 eshablished
		int neighborhoodCount = 2;//Rows and Columns always present
		if(!BXls){neighborhoodCount++;}//Boxes
		if(RBPs){neighborhoodCount++;}//the box-relative-ness, requres boxes
		Scell HouseCats[][][] = new Scell[neighborhoodCount][l][l];
		Scell ehc[][] = new Scell[2][l];//wheather or not this gets included is for later
		for(int i=0,boxID=-1,boxRel=-1,vDiv=-1,vMod=-1;i<BoxCells.length;i++)
		{
			vDiv = i/l;
			vMod = i%l;
			if(vDiv+vMod+1 == l)//rising diagonal
			{
				ehc[0][vDiv] = BoxCells[i];
			}
			if(vDiv == vMod)//falling diagonal
			{
				ehc[1][vDiv] = BoxCells[i];
			}
			if(neighborhoodCount >= 3)//otherwise divisors are 0 and crash
			{
				boxID = (vDiv/verti)*verti+(vMod/hori);
				boxRel = (vDiv%verti)*hori+(i%hori);
			}
			switch(neighborhoodCount)
			{
				case 4://box-relative-ness
					HouseCats[3][boxRel][boxID] = BoxCells[i];
				case 3://plain old boxes
					HouseCats[2][boxID][boxRel] = BoxCells[i];
				case 2://rows and columns
					HouseCats[1][vMod][vDiv] = BoxCells[i];//columns
					HouseCats[0][vDiv][vMod] = BoxCells[i];//rows
					break;
				default:throw new Exception("***Something has gone DISASTEROUSLY wrong***");
			}
		}
		final int houseCount = l*neighborhoodCount;
		final String NeighName[] = {"Row #","Column #","Box #","Disjoint-box-set #"};//make sure this matches switch above
		final String ExtraName[] = {"Rising diagonal","Falling diagonal"};//make sure this matches ifs above
		extraHouseOrders&=3;//clearing all but 2 bits
		Shouse houseList[] = new Shouse[houseCount+Long.bitCount(extraHouseOrders)];//0, 1 or 2 extra
		for(int i=0;i<houseCount;i++)//doing most of it
		{
			houseList[i] = new Shouse(HouseCats[i/l][i%l],NeighName[i/l]+((i%l)+1),A);
		}
		for(int i=houseCount,j=extraHouseOrders,temp=0;i<houseList.length;)//if 0 extra, houseCount==houseList.length
		{
			while((j&1)==0 && j!=0)
			{
				j=j>>>1;
				temp++;
			}
			if(j==0){System.err.println("*Something went wrong with the diagonal melding*");break;}
			houseList[i] = new Shouse(ehc[temp],ExtraName[temp],A);
			i++;
			j=j>>>1;
			temp++;
		}
		//unneeded cleanup
		ehc = null;
		HouseCats = null;
		//finally here
		hz = hori;
		vt = verti;
		BoxHouses = houseList;
		boxless = BXls;
		BoxPuzzle = new Spuzzle(BoxAB,BoxCells,BoxHouses);
		if(!BoxPuzzle.baseConfirm())
		{throw new Exception("baseConfirm error");}
		//building print strings
		String mbs = "", tr, mr, br;
		final int strLim = (boxless?l:hz);
		for(int i=0;i<strLim;i++)
		{mbs=mbs+Spuzzle.lr;}
		tr = mbs;
		mr = mbs;
		br = mbs;
		if(!BXls)
		{
			for(int i=1;i<vt;i++)
			{
				tr=tr+Spuzzle.lrd+mbs;
				mr=mr+Spuzzle.X+mbs;
				br=br+Spuzzle.ulr+mbs;
			}
		}
		topPrintLine = Spuzzle.rd+tr+Spuzzle.ld;
		middlePrintLine = Spuzzle.urd+mr+Spuzzle.uld;
		bottomPrintLine = Spuzzle.ur+br+Spuzzle.ul;
	}

	private Scell[] makeCellList(String rawP,long bcc) throws Exception//only called by constructor
	{//expects BoxAB to be established and valid; and the input(rawP) to go from left to right, top to bottom
		final int l = BoxAB.length;
		final int ens = l*l;//expected number of cells
		char[] pca = rawP.toCharArray();
		if(ens != pca.length)
		{throw new Exception("Cell count mismatch (expected "+ens+", got "+pca.length+")");}
		Scell cellsList[] = new Scell[ens];
		String cellname;
		buildingCellList:for(int i=0;i<ens;i++)
		{
			cellname = "c"+(i%l+1)+"r"+(i/l+1);
			for(int j=0;j<l;j++)
			{
				if(pca[i]==BoxAB[j])
				{
					cellsList[i] = new Scell(1L<<j,cellname);
					continue buildingCellList;
				}
			}
			//empty cell
			cellsList[i] = new Scell(bcc,cellname);
		}
		return cellsList;
	}

	/* this should parse candidacy-specifications. TODO:Everything
	private Scell[] makeSukakuCellList(String rawP,long bcc)//only called by constructor
	{//same as the private function above
		return null;
	}
	*/

	public static void main(String[] args) throws Exception
	{
		BufferedReader in = new BufferedReader(new java.io.InputStreamReader(System.in));
		String ab, p, t[];
		final int h, v, l;
		try
		{
			ab = in.readLine();
			t = in.readLine().split(" ");
			p = in.readLine();
			in.close();
		}
		catch(IOException e){System.err.println(e);return;}
		if(t.length<2)
		{
			System.err.println("Not enough values");
			return;
		}
		h = Integer.parseInt(t[0]);
		v = Integer.parseInt(t[1]);
		int eh;
		if(t.length >= 3)
		{
			try
			{
				eh = Integer.parseInt(t[2]);
			}
			catch(NumberFormatException e)
			{
				eh = 0;
			}
		}
		else
		{eh = 0;}
		Box MyBox = new Box(ab,h,v,p,eh);
		MyBox.printBoard();
		System.out.println(MyBox.solve()+" findings");
		MyBox.printBoard();
		System.out.println(MyBox.BoxPuzzle.countRemainingCells()+" cells unsolved");
		System.out.println(MyBox.BoxPuzzle.countRottenBoroughs()+" rotten boroughs");
	}

	public int solve()
	{
		int finds = 0;
		while(true)
		{
			if(BoxPuzzle.fillHouses()){finds++;continue;}
			if(BoxPuzzle.findAnyUC()){finds++;continue;}
			if(BoxPuzzle.findAnySC()){finds++;continue;}
			if(BoxPuzzle.findAnySolePair()){finds++;continue;}
			if(BoxPuzzle.findAnyUniquePair()){finds++;continue;}
			if(BoxPuzzle.findAnySoleTriple()){finds++;continue;}
			if(BoxPuzzle.findAnyUniqueTriple()){finds++;continue;}
			if(BoxPuzzle.findAnySoleQuad()){finds++;continue;}
			if(BoxPuzzle.findAnyUniqueQuad()){finds++;continue;}
			break;
		}
		return finds;
	}

	public void printBoard()
	{
		final int q = BoxAB.length*vt;
		final int p = (boxless?BoxAB.length:hz);
		System.out.println(topPrintLine);
		System.out.print(Spuzzle.ud);
		System.out.print(BoxCells[0].fe>=0?BoxAB[BoxCells[0].fe]:" ");
		for(int i=1;i<BoxCells.length;i++)
		{
			if(i%p==0){System.out.print(Spuzzle.ud);}
			System.out.print(BoxCells[i].fe>=0?BoxAB[BoxCells[i].fe]:" ");
			if(i%BoxAB.length==(BoxAB.length-1)){System.out.println(Spuzzle.ud);}
			if(!boxless && (i%q==(q-1)) && i<(BoxCells.length-1))
			{System.out.println(middlePrintLine);}
		}
		System.out.println(bottomPrintLine);
	}
}