import java.io.BufferedReader;

class Scell
{
	long cands;//bitmask of candidates
	int fe;//final entry, used with an array
	boolean t;//stamped, aka. considered "found"
	final boolean o;//original
	private final String cellName;//turns out each cell needs a name

	public Scell(long s, String cn) throws Exception
	{//build s in main branch
		if(s==0){throw new Exception("no defining rotten cells!");}
		if(cn==null){throw new Exception("cell must have a non-null name");}
		if(Long.bitCount(s)==1)
		{
			fe = Long.numberOfTrailingZeros(s);
			t = true;
			o = true;
		}
		else
		{
			fe = -1;
			t = false;
			o = false;
		}
		cands = s;
		cellName = cn;
	}
	public String toString()
	{
		return cellName;
	}
}

class Shouse
{
	final Scell[] cellArray;
	private final String houseName;
	long ballot;
	/*
	Implementing killer-cages as houses requires another number;
	can be -1 normally, else the sum of numberOfTrailingZeros;
	for normal sudokus, subtract number of cells from cage-sum
	to synchronize.
	*/

	public Shouse(int f, String n, long unkn)//needs a lot of outside work
	{
		cellArray = new Scell[f];
		houseName = n;
		ballot = unkn;
	}
	public Shouse(Scell[] f, String n, long unkn) throws Exception
	{
		if(f==null){throw new Exception("cell array may not be null");}
		if(n==null){throw new Exception("house must have a non-null name");}
		cellArray = f;
		houseName = n;
		ballot = unkn;
	}
	public boolean remains(long x)
	{
		return ((ballot&x)==x);
	}
	public String toString()
	{
		return houseName;
	}
}

class Cpair//TODO: everything
{//this is ment to simulate thermometers, palindromes, german whispers and white kropki-dots
	final Scell a, b;
	final int gap;
	public Cpair(Scell x, Scell y, int d) throws Exception
	{
		if(x==null||y==null){throw new Exception("no nulls in Cpair");}
		if(x==y){throw new Exception("Cpair must be 2 different cells");}
		a = x;
		b = y;
		gap = d;
	}
}

public class Spuzzle
{
	public static final String d3 = "123456789";//default sudoku sign string
	public static final String d4 = "0123456789ABCDEF";
	public static final String d8 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";//base 64
	public static final long EO1 = 6148914691236517205L;//alternating 1 and 0 with the lowest as 1
	public static final char lr = (char)9472;//left-right
	public static final char rd = (char)9484;//right-down
	public static final char urd = (char)9500;//up-right-down
	public static final char ur = (char)9492;//up-right
	public static final char lrd = (char)9516;//left-right-down
	public static final char X = (char)9532;//all 4 directions
	public static final char ulr = (char)9524;//up-left-right
	public static final char ld = (char)9488;//left-down
	public static final char uld = (char)9508;//up-left-down
	public static final char ul = (char)9496;//up-left
	public static final char ud = (char)9474;//up-down

	final char[] signs;//list of signs from smallest up, length is also how long each house should be
	private final long[] candy;//where each sign is in the bitmask
	final Scell[] rawcells;
	final Shouse[] Houses;

	public Spuzzle(char[] sca, Scell[] cs, Shouse[] hs) throws Exception
	{
		if(sca==null||cs==null||hs==null){throw new Exception("arrays may not be null");}
		if(sca.length<2){throw new Exception("not enough signs");}//it's just a box with a sign in it
		if(sca.length>64){throw new Exception("too many signs");}//long is used for bitmasking
		int i, j;//generic loop-counters
		for(i=1;i<sca.length;i++)
		{
			for(j=0;j<i;j++)
			{
				if(sca[i]==sca[j])
				{throw new Exception("all signs must be different");}
			}
		}
		signs = sca;
		for(i=0;i<hs.length;i++)
		{
			if(hs[i].cellArray.length > sca.length)
			{throw new Exception("houses may not have more cells than there are signs");}
			for(j=0;j<hs[i].cellArray.length;j++)
			{
				if(hs[i].cellArray[j]==null)
				{throw new Exception("houses may not contain null-cells");}
			}
		}
		candy = new long[sca.length];//maybe unneeded?
		for(i=0;i<sca.length;i++)
		{
			candy[i]=1L<<i;//first sign is the "smallest"
		}
		rawcells = cs;
		Houses = hs;
	}

	public static void main(String[] args) throws Exception
	{
		final char[] MyAlphabet = d3.toCharArray();
		final int ncls = 81;//number of cells in a normal sudoku
		BufferedReader in = new BufferedReader(new java.io.InputStreamReader(System.in));
		char inp[] = in.readLine().toCharArray();
		in.close();
		if(inp.length!=ncls)
		{
			System.err.println("need 81 characters in a line");
			return;
		}
		Scell listOfCells[] = new Scell[ncls];
		long A = (1L<<9)-1;//511, the bitmask for an unknown cell in a size-9 alphabet
		int t;
		String cellname;
		Scell Rows[][] = new Scell[9][9];
		Scell Columns[][] = new Scell[9][9];
		Scell Boxes[][] = new Scell[9][9];
		for(int i=0;i<ncls;i++)
		{
			cellname = "c"+(i%9+1)+"r"+(i/9+1);
			t=Character.digit(inp[i],10);
			if(t == -1 || t == 0)
			{
				listOfCells[i] = new Scell(A,cellname);
			}
			else
			{
				t--;
				listOfCells[i] = new Scell(1L<<t,cellname);
			}
			Rows[i/9][i%9] = listOfCells[i];
			Columns[i%9][i/9] = listOfCells[i];
			Boxes[(i/27)*3+((i/3)%3)][((i/9)%3)*3+(i%3)] = listOfCells[i];
		}
		Shouse listOfHouses[] = new Shouse[27];
		for(int i=0;i<27;i++)
		{
			if(i<9)
			{
				listOfHouses[i] = new Shouse(Rows[i],("Row #"+(i+1)),A);
			}
			else if(i<18)
			{
				listOfHouses[i] = new Shouse(Columns[i-9],("Column #"+(i-8)),A);
			}
			else
			{
				listOfHouses[i] = new Shouse(Boxes[i-18],("Box #"+(i-17)),A);
			}
		}
		final String topofboard = ""+rd+lr+lr+lr+lrd+lr+lr+lr+lrd+lr+lr+lr+ld;
		final String middleofboard = ""+urd+lr+lr+lr+X+lr+lr+lr+X+lr+lr+lr+uld;
		final String bottomofboard = ""+ur+lr+lr+lr+ulr+lr+lr+lr+ulr+lr+lr+lr+ul;
		{//Printing board
			System.out.println(topofboard);
			for(int i=0;i<ncls;i++)
			{
				if(i%3==0){System.out.print(ud);}
				System.out.print(listOfCells[i].fe>=0?MyAlphabet[listOfCells[i].fe]:" ");
				if(i>0 && (i%9==8)){System.out.println(ud);}
				if(i<80 && (i%27==26))
				{System.out.println(middleofboard);}
			}
			System.out.println(bottomofboard);
		}//done printing board
		Spuzzle MyPuzzle = new Spuzzle(MyAlphabet,listOfCells,listOfHouses);
		if(!MyPuzzle.baseConfirm()){return;}
		System.out.println(MyPuzzle.countRemainingCells()+" cells unsolved");
		System.out.println(MyPuzzle.countRemainingCandidacies()+" candidacies remain");
		System.out.println(MyPuzzle.countRottenBoroughs()+" rotten boroughs");
		System.out.println(MyPuzzle.countBallots()+" on ballots");
		System.out.println(MyPuzzle.countSoleCandidates()+" easy squares");
		System.out.println();
		System.out.println(MyPuzzle.testSolve()+" findings");
		{//Printing board
			System.out.println(topofboard);
			for(int i=0;i<ncls;i++)
			{
				if(i%3==0){System.out.print(ud);}
				System.out.print(listOfCells[i].fe>=0?MyAlphabet[listOfCells[i].fe]:" ");
				if(i>0 && (i%9==8)){System.out.println(ud);}
				if(i<80 && (i%27==26))
				{System.out.println(middleofboard);}
			}
			System.out.println(bottomofboard);
		}//done printing board
		System.out.println(MyPuzzle.countRemainingCells()+" cells unsolved");
		System.out.println(MyPuzzle.countRemainingCandidacies()+" candidacies remain");
		System.out.println(MyPuzzle.countRottenBoroughs()+" rotten boroughs");
		System.out.println(MyPuzzle.countBallots()+" on ballots");
		System.out.println(MyPuzzle.countSoleCandidates()+" easy squares");
		MyPuzzle.roomservice();
	}

	public boolean confirm(Scell x, int si)//si=signs index
	{
		if(si>=signs.length || si<0)
		{
			System.err.println("ERROR: confirm was called with bad index("+si+" on "+x+")");
			return false;
		}
		if(x==null)
		{
			System.err.println("ERROR: confirm was called on null cell");
			return false;
		}
		long temp = (x.cands&candy[si]);//is bitmask with at most 1 onebit
		if(temp==0)
		{
			System.err.println("ERROR: confirm was called with impossible candidate");
			return false;
		}
		if(x.t)
		{
			System.err.println("ERROR: confirm was called on an already-confirmed cell");
			return false;
		}
		x.fe=si;
		x.cands=temp;
		x.t=true;
		//now let's remove candidacy from all shared houses
		doortodoor:for(int i=0;i<Houses.length;i++)
		{
			if(Houses[i].remains(temp))//the ballot is trusted to save time
			{
				for(int j=0;j<Houses[i].cellArray.length;j++)//check all houses for x
				{
					if(Houses[i].cellArray[j]==x)//if found...
					{
						for(int k=0;k<Houses[i].cellArray.length;k++)//...go through the house again
						{
							if(Houses[i].cellArray[k]!=x)//and everyone else...
							{
								Houses[i].cellArray[k].cands&=(~temp);//...isn't this sign
							}	
						}
						Houses[i].ballot&=(~temp);//mark candidate found in house
						continue doortodoor;//already handled this house
					}
				}
			}
		}
		return true;
	}

	public boolean baseConfirm()//runs pseudo-confirmation on squares marked original
	{//run assuming given original squares didn't go through confirmation
		long mess;
		int hmo;//how many originals? This is to find double-ups in the initial grid
		boolean noIssues = true;
		for(int i=0;i<Houses.length;i++)
		{
			mess = 0;//builds into a candidate-list of original squares
			hmo = 0;
			for(int j=0;j<Houses[i].cellArray.length;j++)
			{
				if(Houses[i].cellArray[j].o)
				{
					hmo++;
					if(Long.bitCount(Houses[i].cellArray[j].cands)==1)
					{
						mess|=Houses[i].cellArray[j].cands;
					}
					else
					{
						System.err.println("ERROR: original square "+Houses[i].cellArray[j]+" not single-candidate");
						return false;
					}
				}
			}
			if(Long.bitCount(mess)!=hmo)
			{
				System.err.println("ALERT: clones detected in "+Houses[i]);
				noIssues = false;
			}
			mess = (~mess);//lists everything except the original squares
			for(int j=0;j<Houses[i].cellArray.length;j++)
			{
				if(!Houses[i].cellArray[j].o)
				{//removes the originals' candidacies from the other squares...
					Houses[i].cellArray[j].cands&=mess;
				}
			}
			Houses[i].ballot&=mess;//...and the house in question
		}
		return noIssues;
	}

	private int testSolve()
	{
		int finds = 0;
		while(true)
		{
			if(fillHouses()){finds++;continue;}
			if(findAnyUC()){finds++;continue;}
			if(findAnySC()){finds++;continue;}
			if(findAnySolePair()){finds++;continue;}
			if(findAnyUniquePair()){finds++;continue;}
			if(findAnySoleTriple()){finds++;continue;}
			if(findAnyUniqueTriple()){finds++;continue;}
			if(findAnySoleQuad()){finds++;continue;}
			if(findAnyUniqueQuad()){finds++;continue;}
			break;
		}
		return finds;
	}

	public boolean fillHouses()
	{
		int i=0, j, temp;
		while(i<Houses.length)
		{
			if(Long.bitCount(Houses[i].ballot)==1)
			{
				for(j=0;j<Houses[i].cellArray.length;j++)
				{
					if(!Houses[i].cellArray[j].t)
					{
						if(Houses[i].cellArray[j].cands == 0L)//rotten borough
						{
							System.err.println("ALERT: rotten borough encountered at "+Houses[i].cellArray[j]+" while trying to fill "+Houses[i]+". Purging ballot.");
							Houses[i].ballot = 0L;//It had 1, now it has none
							return false;
						}
						temp = Long.numberOfTrailingZeros(Houses[i].cellArray[j].cands);
						if(confirm(Houses[i].cellArray[j],temp))
						{
							System.out.println("Filling "+Houses[i]+" : "+Houses[i].cellArray[j]+" is '"+signs[temp]+"'");
							return true;
						}
					}
				}
			}
			i++;
		}
		return false;
	}

	public boolean findSC(Scell x)//checks if cell has SC, confirms
	{
		int temp;
		if(!x.t && Long.bitCount(x.cands)==1)
		{
			temp = Long.numberOfTrailingZeros(x.cands);//known to have 1 bit, temp won't be 64
			if(confirm(x,temp))
			{
				System.out.println("Sole Candidate '"+signs[temp]+"' found at "+x);
				return true;
			}
		}
		return false;
	}

	public boolean findAnySC()
	{
		for(int i=0;i<rawcells.length;i++)
		{
			if(findSC(rawcells[i]))
			{return true;}
		}
		return false;
	}

	public boolean findUC(int Hind,int Cind)//looks for a specific UC in a specific house, confirms
	{//House must be fullsized for this method to work
		long temp = candy[Cind];
		if(Houses[Hind].cellArray.length==signs.length && Houses[Hind].remains(temp))
		{
			int howmany = 0;
			Scell thisone = null;
			for(int i=0;i<Houses[Hind].cellArray.length;i++)
			{
				if((Houses[Hind].cellArray[i].cands&temp)==temp)
				{
					howmany++;
					thisone = Houses[Hind].cellArray[i];
				}
			}
			if(howmany==1)
			{
				if(confirm(thisone,Cind))
				{
					System.out.println("Unique candidate '"+signs[Cind]+"' found in "+Houses[Hind]+" at "+thisone);
					return true;
				}
			}
		}//UC doesn't work when alphabet not filled in house
		return false;
	}

	public boolean findAnyUC()
	{
		for(int i=0;i<Houses.length;i++)
		{
			for(int c=0;c<signs.length;c++)
			{
				if(findUC(i,c))
				{return true;}
			}
		}
		return false;
	}

	public int findSoleSet(int Hind, long mask)//finds set of cells containing only these candidates, removes them from other cells in this house
	{//house does not need to be full-sized
		int totrem=0;
		if(mask!=0 && Houses[Hind].remains(mask))
		{
			final int needed = Long.bitCount(mask);
			int c=0;
			for(int i=0;i<Houses[Hind].cellArray.length;i++)
			{
				//WIP
				if(Houses[Hind].cellArray[i].cands==mask)//needs updating? each cell may not have the full set.
				{c++;}
			}
			if(c>needed)
			{
				System.err.println("ERROR: Too many cells for Sole Set '"+signpost(mask)+"' in "+Houses[Hind]+" (needs "+needed+", found "+c+")");
				return -1;
			}
			else if(c==needed)
			{
				int r=0;
				for(int i=0;i<Houses[Hind].cellArray.length;i++)
				{
					if(Houses[Hind].cellArray[i].cands!=mask)//needs updating with the WIP...
					{
						r+=Long.bitCount(Houses[Hind].cellArray[i].cands&mask);
						Houses[Hind].cellArray[i].cands&=(~mask);
					}
				}
				if(r!=0)
				{
					System.out.println("Sole Set '"+signpost(mask)+"' found in "+Houses[Hind]+" ("+r+(r>1?" candidacies":" candidacy")+" removed)");
					totrem+=r;
				}
			}
		}
		return totrem;
	}

	public boolean findAnySolePair()
	{
		int temp;
		for(int h=0;h<Houses.length;h++)
		{
			for(int i=1;i<signs.length;i++)
			{
				for(int j=0;j<i;j++)
				{
					temp = findSoleSet(h,(candy[i]|candy[j]));
					if(temp!=0){return (temp>0);}
				}
			}
		}
		return false;
	}

	public boolean findAnySoleTriple()
	{
		int temp;
		for(int h=0;h<Houses.length;h++)
		{
			for(int i=2;i<signs.length;i++)
			{
				for(int j=1;j<i;j++)
				{
					for(int k=0;k<j;k++)
					{
						temp = findSoleSet(h,(candy[i]|candy[j]|candy[k]));
						if(temp!=0){return (temp>0);}
					}
				}
			}
		}
		return false;
	}

	public boolean findAnySoleQuad()
	{
		int temp;
		for(int h=0;h<Houses.length;h++)
		{
			for(int i=3;i<signs.length;i++)
			{
				for(int j=2;j<i;j++)
				{
					for(int k=1;k<j;k++)
					{
						for(int l=0;l<k;l++)
						{
							temp = findSoleSet(h,(candy[i]|candy[j]|candy[k]|candy[l]));
							if(temp!=0){return (temp>0);}
						}
					}
				}
			}
		}
		return false;
	}

	/* trying to do the above recursively
	public boolean SoleSetFinder(int n)//n is how many signs in set
	{
		if(n<=0)
		{
			System.err.println("ERROR: SSF called with invalid int ("+n+")");
			return false;
		}
		for(int h=0;h<Houses.length;h++)
		{
			if(Long.bitCount(Houses[h].ballot) > n)//trying to save time
			{
				if(soleSetRec(n-1,signs.length,0L,h))
				{return true;}
			}
		}
		return false;
	}

	private boolean soleSetRec(int n, int a, long mask, int h)//only ever called by SoleSetFinder
	{
		if(n==0)
		{
			int temp;
			for(int i=0;i<a;i++)
			{
				temp = findSoleSet(h,(mask|candy[i]));
				if(temp!=0){return (temp>0);}
			}
		}
		else
		{
			assert n>0;
			for(int i=n;i<a;i++)
			{
				if(soleSetRec(n-1,i,(mask|candy[i])))
				{return true;}
			}
		}
	}
	*/

	public int findUniqueSet(int Hind, long mask)//from the set of cells containing these candidates, removes other candidates
	{//House must be fullsized for this method to work
		int totrem=0;
		if(mask!=0 && Houses[Hind].cellArray.length==signs.length && Houses[Hind].remains(mask))
		{
			final int needed = Long.bitCount(mask);
			int c=signs.length;
			for(int i=0;i<Houses[Hind].cellArray.length;i++)
			{
				if((Houses[Hind].cellArray[i].cands&mask)==0)
				{c--;}
			}
			if(c<needed)
			{
				System.err.println("ERROR: Too few cells for set '"+signpost(mask)+"' in "+Houses[Hind]+" (needs "+needed+", has "+c+")");
				return -1;
			}
			else if(c==needed)
			{
				int r=0;
				for(int i=0;i<Houses[Hind].cellArray.length;i++)
				{
					if((Houses[Hind].cellArray[i].cands&mask)!=0)
					{
						r+=Long.bitCount(Houses[Hind].cellArray[i].cands&(~mask));
						Houses[Hind].cellArray[i].cands&=mask;
					}
				}
				if(r!=0)
				{
					System.out.println("Unique Set '"+signpost(mask)+"' found in "+Houses[Hind]+" ("+r+(r>1?" candidacies":" candidacy")+" removed)");
					totrem+=r;
				}
			}
		}
		return totrem;
	}

	public boolean findAnyUniquePair()
	{
		int temp;
		for(int h=0;h<Houses.length;h++)
		{
			for(int i=1;i<signs.length;i++)
			{
				for(int j=0;j<i;j++)
				{
					temp = findUniqueSet(h,(candy[i]|candy[j]));
					if(temp!=0){return (temp>0);}
				}
			}
		}
		return false;
	}

	public boolean findAnyUniqueTriple()
	{
		int temp;
		for(int h=0;h<Houses.length;h++)
		{
			for(int i=2;i<signs.length;i++)
			{
				for(int j=1;j<i;j++)
				{
					for(int k=0;k<j;k++)
					{
						temp = findUniqueSet(h,(candy[i]|candy[j]|candy[k]));
						if(temp!=0){return (temp>0);}
					}
				}
			}
		}
		return false;
	}

	public boolean findAnyUniqueQuad()
	{
		int temp;
		for(int h=0;h<Houses.length;h++)
		{
			for(int i=3;i<signs.length;i++)
			{
				for(int j=2;j<i;j++)
				{
					for(int k=1;k<j;k++)
					{
						for(int l=0;l<k;l++)
						{
							temp = findUniqueSet(h,(candy[i]|candy[j]|candy[k]|candy[l]));
							if(temp!=0){return (temp>0);}
						}
					}
				}
			}
		}
		return false;
	}

	/*
	*/

	public String signpost(long q)//turn bitmask into string
	{
		String skil = "";
		for(int i=0;i<signs.length;i++)
		{
			if((q&candy[i])!=0)
			{skil+=signs[i];}
		}
		return skil;
	}

	public int countRemainingCells()
	{
		int svar=0;
		for(int i=0;i<rawcells.length;i++)
		{
			if(!rawcells[i].t)
			{
				svar++;
			}
		}
		return svar;
	}

	public int countRemainingCandidacies()
	{
		int svar=0;
		for(int i=0;i<rawcells.length;i++)
		{
			if(!rawcells[i].t)
			{
				svar+=Long.bitCount(rawcells[i].cands);
			}
		}
		return svar;
	}

	public int countBallots()
	{
		int svar=0;
		for(int i=0;i<Houses.length;i++)
		{
			svar+=Long.bitCount(Houses[i].ballot);
		}
		return svar;
	}

	public int countRottenBoroughs()
	{
		int svar=0;
		for(int i=0;i<rawcells.length;i++)
		{
			if(Long.bitCount(rawcells[i].cands)==0)
			{svar++;}
		}
		return svar;
	}

	public int countSoleCandidates()
	{
		int svar=0;
		for(int i=0;i<rawcells.length;i++)
		{
			if(!rawcells[i].t && Long.bitCount(rawcells[i].cands)==1)
			{svar++;}
		}
		return svar;
	}

	public void roomservice()//in case of random desync, such as cosmic ray
	{//only use if all houses are full-sized
		int i, j;
		long temp;
		for(i=0;i<Houses.length;i++)
		{
			temp = 0L;
			for(j=0;j<Houses[i].cellArray.length;j++)//builds a pseudo-ballot...
			{
				if(!Houses[i].cellArray[j].t)//...from candidates of unstamped cells
				{
					temp|=Houses[i].cellArray[j].cands;
				}
			}
			if((Houses[i].ballot^temp) != 0)//if different from the actual ballot, then desync detected
			{
				System.err.println("DESYNC in "+Houses[i]+" ('"+signpost(Houses[i].ballot)+"' vs '"+signpost(temp)+"')");
			}//at most 1 message per house
		}
	}
}