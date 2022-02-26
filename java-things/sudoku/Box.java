import java.io.BufferedReader;
import java.io.IOException;

public class Box
{
	private final Spuzzle BoxPuzzle;
	private final int hz;
	private final int vt;
	private final Scell[] BoxCells;
	private final Shouse[] BoxHouses;
	private final char[] BoxAB;
	private final boolean boxless;
	private final String topPrintLine;
	private final String middlePrintLine;
	private final String bottomPrintLine;

	public Box(String RawAB,int hori, int verti, String rawP) throws Exception
	{
		char[] Alphabet = RawAB.toCharArray();
		final int l = Alphabet.length;
		if(l<2 || l>64)
		{throw new Exception("Bad puzzle size");}
		if(hori<0 || verti<0 || hori==1 || verti==1 || ( (hori==0)^(verti==0) ))
		{throw new Exception("Bad box sizes");}
		boolean BXls = false;//boxless
		if(hori==0 && verti==0)
		{
			BXls = true;
		}
		else if(hori*verti!=l || l%hori!=0 || l%verti!=0)
		{throw new Exception("Bad box fit");}
		char[] pca = rawP.toCharArray();
		final int ens = l*l;//expected number of cells
		if(ens != pca.length)
		{throw new Exception("Cell count mismatch (expected "+ens+", got "+pca.length+")");}
		long A = (-1L)>>>(64-l);//bitmask for blank cell
		Scell cellsList[] = new Scell[ens];
		String cellname;
		buildingCellList:for(int i=0;i<cellsList.length;i++)
		{
			cellname = "c"+(i%l+1)+"r"+(i/l+1);
			for(int j=0;j<l;j++)
			{
				if(pca[i]==Alphabet[j])
				{
					cellsList[i] = new Scell(1L<<j,cellname);
					continue buildingCellList;
				}
			}
			//empty cell
			cellsList[i] = new Scell(A,cellname);
		}
		Scell Rows[][] = new Scell[l][l];
		Scell Columns[][] = new Scell[l][l];
		Scell Boxes[][];
		final int houseCount;
		if(BXls)
		{
			Boxes = null;
			houseCount = l*2;
		}
		else
		{
			Boxes = new Scell[l][l];
			houseCount = l*3;
		}
		for(int i=0;i<cellsList.length;i++)
		{
			Rows[i/l][i%l] = cellsList[i];
			Columns[i%l][i/l] = cellsList[i];
			if(!BXls)
			{Boxes[(i/l/verti)*verti+(i%l)/hori][((i/l)%verti)*hori+(i%hori)] = cellsList[i];}
		}
		Shouse houseList[] = new Shouse[houseCount];
		for(int i=0;i<houseCount;i++)
		{
			if(i<l)
			{
				houseList[i] = new Shouse(Rows[i],("Row #"+(i+1)),A);
			}
			else if(i<(l+l))
			{
				houseList[i] = new Shouse(Columns[i-l],("Column #"+(i-l+1)),A);
			}
			else if(!BXls)//just to make sure, shouldn't be reached anyway
			{
				houseList[i] = new Shouse(Boxes[i-l-l],("Box #"+(i-l-l+1)),A);
			}
		}
		//finally here
		BoxCells = cellsList;
		BoxHouses = houseList;
		BoxAB = Alphabet;
		boxless = BXls;
		BoxPuzzle = new Spuzzle(Alphabet,cellsList,houseList);
		hz = hori;
		vt = verti;
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
		Box MyBox = new Box(ab,h,v,p);
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
		System.out.println(topPrintLine);
		if(boxless)
		{
			for(int i=0;i<BoxCells.length;i++)
			{
				if(i%BoxAB.length==0){System.out.print(Spuzzle.ud);}
				System.out.print(BoxCells[i].fe>=0?BoxAB[BoxCells[i].fe]:" ");
				if(i>0 && (i%BoxAB.length==(BoxAB.length-1))){System.out.println(Spuzzle.ud);}
			}
		}
		else//boxiful
		{
			final int q = BoxAB.length*vt;
			for(int i=0;i<BoxCells.length;i++)
			{
				if(i%hz==0){System.out.print(Spuzzle.ud);}
				System.out.print(BoxCells[i].fe>=0?BoxAB[BoxCells[i].fe]:" ");
				if(i>0 && (i%BoxAB.length==(BoxAB.length-1))){System.out.println(Spuzzle.ud);}
				if(i<(BoxCells.length-1) && (i%q==(q-1)))
				{System.out.println(middlePrintLine);}
			}
		}
		System.out.println(bottomPrintLine);
	}
}