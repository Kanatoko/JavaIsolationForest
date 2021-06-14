package net.jumperz.ds.iforest;

import java.util.List;
import java.util.Map;

public class MIFUtil
{

public static final double log2 = Math.log( 2 );

public static double log2( double d )
{
	return Math.log( d ) / log2;
}

/*
 * Isolation Forest論文の式そのまま
 */
public static double c( double n )
{
	if( n <= 1.0 )
	{
		return 1;
	}

	return 2 * (Math.log( n - 1 ) + 0.5772156649) - (2 * (n - 1) / n);
}

/*
 * 一度ser/deserを経由し、MIsolationForestクラスのインスタンスが存在しない状態で評価を行う際に使う
 */
public static double getScore( final double[] data, final List< Map< Integer, double[] > > treeList, final int subSampleSize )
{
	return getScore( data, treeList, subSampleSize, false );
}

public static double getScore( final double[] data, final List< Map< Integer, double[] > > treeList, final int subSampleSize, final boolean returnDepth )
{
	double avgDepth = 0;
	for( final Map< Integer, double[] > tree : treeList )
	{
		avgDepth += getDepth( 0, 0, tree, data );
	}
	avgDepth /= treeList.size();

	if( returnDepth )
	{
		return avgDepth;
	}

	return getScore( avgDepth, subSampleSize );
}

public static double getScore( final double avgDepth, final double subSampleSize )
{
	//論文そのまま
	return 1.0 - Math.pow( 2, -avgDepth / c( subSampleSize ) );
}

/*
 * インデックスの値がindexであるノードの深さをdepthとした場合に、内容がdataのデータの深さを求める
 */
public static int getDepth( final int index, final int depth, final Map< Integer, double[] > tree, final double[] data )
{
	final double[] nodeInfo = tree.get( index );
	if( nodeInfo == null )
	{
		return depth;
	}

	final int selectedFeatureIndex = ( int )nodeInfo[ 0 ];
	final double splitPoint = nodeInfo[ 1 ];

	if( data[ selectedFeatureIndex ] <= splitPoint )
	{
		return getDepth( index * 2 + 1, depth + 1, tree, data );
	}
	else
	{
		return getDepth( index * 2 + 2, depth + 1, tree, data );
	}
}

/*
 * 一度ser/deserを経由し、MIsolationForestクラスのインスタンスが存在しない状態で評価を行う際に使う
 */
public static double getScore( final double[] data, final Map context )
{
	final List< Map< Integer, double[] > > treeList = ( List< Map< Integer, double[] > > )context.get( "treeList" );
	final int subSampleSize = ( Integer )context.get( "subSampleSize" );
	return getScore( data, treeList, subSampleSize );
}

}
