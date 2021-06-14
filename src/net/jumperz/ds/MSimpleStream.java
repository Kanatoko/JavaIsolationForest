package net.jumperz.ds;

import java.math.*;

/*
 * calculate variance/average online(stream)
 */
public class MSimpleStream
{
private long count = 0;
private BigDecimal sum = new BigDecimal( 0.0 );
private BigDecimal sum2 = new BigDecimal( 0.0 );
private double min = Double.MAX_VALUE;
private double max = 0;

private long minIndex = 0;
private long maxIndex = 0;

public static final int scale = 10;

public void update( final double d )
{
	sum = sum.add( new BigDecimal( d ) );
	sum2 = sum2.add( new BigDecimal( d ).multiply( new BigDecimal( d ) ) );

	if( d < min )
	{
		min = d;
		minIndex = count;
	}

	if( d > max )
	{
		max = d;
		maxIndex = count;
	}
	++count;
}

public long getMinIndex()
{
	return minIndex;
}

public long getMaxIndex()
{
	return maxIndex;
}

/*
 * updateを無かったことにする
 * 主に外れ値の処理で使うことを想定
 * min/maxは不正確な値になる可能性がある
 */
public void remove( final double d )
{
	--count;
	sum = sum.subtract( new BigDecimal( d ) );
	sum2 = sum2.subtract( new BigDecimal( d ).multiply( new BigDecimal( d ) ) );
}

public BigDecimal getAverage()
{
	if( count == 0 )
	{
		return new BigDecimal( 0 );
	}
	else
	{
		return sum.divide( new BigDecimal( count ), scale, BigDecimal.ROUND_HALF_DOWN );
	}
}

public BigDecimal getVariance()
{
	if( count == 0 )
	{
		return new BigDecimal( 0 );
	}
	else
	{
		final BigDecimal average = getAverage();
		return sum2.divide( new BigDecimal( count ), scale, BigDecimal.ROUND_HALF_DOWN ).subtract( average.multiply( average ) );
	}
}

public double getMax()
{
	return max;
}

public double getMin()
{
	if( count == 0 )
	{
		return 0;
	}
	return min;
}

public double getSum()
{
	return sum.doubleValue();
}

public String toString()
{
	return getAverage() + " : " + getVariance();
}

public long getCount()
{
	return count;
}

}