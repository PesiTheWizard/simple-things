import java.util.Scanner;
import java.util.Arrays;

class Card implements Comparable<Card>
{
	final char suit;
	final int value;
	public Card(String x) throws Exception
	{
		char s = x.charAt(0);
		if(s=='h'|s=='s'|s=='d'|s=='c')
		{
			suit=s;
		}
		else
		{
			throw new Exception();
		}
		value=Integer.parseInt(x.substring(1,x.length()));
	}
	public int compareTo(Card x)
	{
		int bla = this.value-x.value;
		if(bla==0)
		{bla=this.suit-x.suit;}
		return bla;
	}
	public String toString()
	{
		return (""+suit+value);
	}
}

public class Hands
{
	public static void main(String[] args)
	{
		final String ranknames[] = {"Pig","High card      ","One pair       ","Two pairs      ","three of a kind","straight       ","flush          ","full house     ","four of a kind ","straight flush ","five of a kind ","dead mans hand "};
		Scanner s = new Scanner(System.in);
		String inp[] = s.nextLine().split(" ");
		if(inp.length<5)
		{
			System.err.println("too few cards for a poker hand");
			return;
		}
		Card deck[] = new Card[inp.length];
		for(int i=0;i<inp.length;i++)
		{
			try
			{
				deck[i] = new Card(inp[i]);
			}
			catch(Exception e)
			{
				System.err.println("bad card format");
				return;
			}
		}
		Arrays.sort(deck);
		Card ca, cb, cc, cd, ce;
		int score[] = new int[12], cheat[] = new int[2];
		for(int i=4;i<deck.length;i++)
		{
			ce = deck[i];
			for(int j=3;j<i;j++)
			{
				cd = deck[j];
				for(int k=2;k<j;k++)
				{
					cc = deck[k];
					for(int l=1;l<k;l++)
					{
						cb = deck[l];
						for(int m=0;m<l;m++)
						{
							ca = deck[m];
							score[handRank(ca,cb,cc,cd,ce)]++;
							cheat[cheatin(ca,cb,cc,cd,ce)]++;
						}
					}
				}
			}
		}
		int sumofall=cheat[0]+cheat[1];
		for(int i=11;i>0;i--)
		{
			System.out.printf("%s:%d(%.10f)\n",ranknames[i],score[i],(score[i]/(double)sumofall));
			//System.out.println(ranknames[i]+":"+score[i]+"("+(score[i]/(double)sumofall)+")");
		}
		System.out.println("total hands:"+sumofall);
		System.out.printf("cheatin:%d(%.10f)",cheat[1],(cheat[1]/(double)sumofall));
		//System.out.println("cheatin:"+cheat[1]+"("+(cheat[1]/(double)sumofall)+")");
	}
	static int handRank(Card a, Card b, Card c, Card d, Card e)
	{//requires sorted from a to e
		if(a.suit=='c'&&b.suit=='s'&&c.suit=='c'&&d.suit=='s'&&e.suit=='d'&&a.value==1&&b.value==1&&c.value==8&&d.value==8&&e.value==11)
		{
			return 11;//DMH
		}
		if(a.value==e.value)//sorted by value first
		{
			return 10;//5 of a kind
		}
		if(a.suit==b.suit&&b.suit==c.suit&&c.suit==d.suit&&d.suit==e.suit)
		{//at least a flush
			if(a.value+1==b.value&&b.value+1==c.value&&c.value+1==d.value&&d.value+1==e.value)//also a straight?
			{
				return 9;//straight flush
			}
			else
			{
				return 6;//normal flush
			}
		}
		if(a.value+1==b.value&&b.value+1==c.value&&c.value+1==d.value&&d.value+1==e.value)
		{//already checked for straight flush
			return 5;//normal straight
		}
		if(a.value==d.value||b.value==e.value)
		{//already checked for 5 of a kind
			return 8;//4 of a kind
		}
		if(a.value==c.value)
		{//at least 3 of a kind
			if(d.value==e.value)
			{
				return 7;//full house
			}
			else
			{
				return 4;//3 of a kind
			}
		}
		if(c.value==e.value)
		{
			if(a.value==b.value)
			{
				return 7;//full house
			}
			else
			{
				return 4;//3 of a kind
			}
		}
		if(b.value==d.value)
		{
			return 4;//the other 3 of a kind
		}
		if(a.value==b.value)
		{//at least one pair
			if(c.value==d.value||d.value==e.value)
			{
				return 3;//two pairs
			}
			else
			{
				return 2;//one pair
			}
		}
		if(b.value==c.value)
		{//at least one pair
			if(d.value==e.value)
			{
				return 3;//two pairs
			}
			else
			{
				return 2;//one pair
			}
		}
		if(c.value==d.value||d.value==e.value)
		{//everything else already checked
			return 2;//one pair
		}
		return 1;//High card
	}
	static int cheatin(Card a, Card b, Card c, Card d, Card e)
	{//also requires sorted from a to e
		if(a.suit==b.suit&&a.value==b.value)
		{return 1;}
		if(b.suit==c.suit&&b.value==c.value)
		{return 1;}
		if(c.suit==d.suit&&c.value==d.value)
		{return 1;}
		if(d.suit==e.suit&&d.value==e.value)
		{return 1;}
		return 0;
	}
}
/*
11 = dead man's hand
10 = 5 of a kind
9  = straight flush
8  = 4 of a kind
7  = full house
6  = flush
5  = straight
4  = 3 of a kind
3  = 2 Pair
2  = 1 Pair
1  = High Card
*/