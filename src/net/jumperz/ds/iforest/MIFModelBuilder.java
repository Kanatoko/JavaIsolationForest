package net.jumperz.ds.iforest;

import java.util.*;
import java.util.stream.IntStream;

public class MIFModelBuilder
{
private Set< Integer > ignoredFeatureSet = new HashSet<>();
private final int treeNumber;
private int subSampleSize = 256;
private List< Map< Integer, double[] > > treeList;
private final Random rand = new Random();
private Exception ex;

public MIFModelBuilder(final int treeNumber)
{
	this.treeNumber = treeNumber;
	treeList = new ArrayList<>( treeNumber );
}

public void setSubSampleSize( final int i )
{
	this.subSampleSize = i;
}

public int getSubSampleSize()
{
	return this.subSampleSize;
}

public MIFModelBuilder()
{
	this.treeNumber = 100;
	treeList = new ArrayList<>( treeNumber );
}

/*
 * 学習に使用しない特徴を追加する
 */
public void ignore( final int index )
{
	ignoredFeatureSet.add( index );
}

private int buildNode( final List< double[] > data )
{
	try
	{
		//深さの最大値を定義する
		final int maxDepth = ( int )Math.ceil( MIFUtil.log2( subSampleSize ) );

		//木は具体的なデータ構造としてはMapで表現する。各ノードについて、キーにはインデックス番号(rootが0)、値にはselectedFeatureIndexとsplitPointによって構成される長さ2のdouble[]を入れる
		final Map< Integer, double[] > tree = new HashMap<>( subSampleSize );
		buildNode( getSubSample( data, subSampleSize ), 0, 0, maxDepth, tree );
		synchronized( treeList )
		{
			treeList.add( tree );
		}
		return 0;
	}
	catch( Exception e )
	{
		e.printStackTrace();
		ex = e;
		return -1;
	}
}

public void build( final List< double[] > data ) throws Exception
{
	//必要な場合はサンプルサイズを調整
	if( data.size() < subSampleSize )
	{
		subSampleSize = data.size();
	}

	//並列処理で木を作る
	final int[] result = IntStream.range( 0, treeNumber ).parallel().map( x -> buildNode( data ) ).toArray();

	//例外が発生したかどうかをチェック
	for( int i : result )
	{
		if( i != 0 && ex != null )
		{
			throw ex;
		}
	}
}

private void buildNode( final List< double[] > data, final int index, final int depth, final int maxDepth, final Map< Integer, double[] > tree ) throws Exception
{
	if( depth == maxDepth + 1 || data.size() == 0 )
	{
		//これ以上は枝を伸ばさない
		return;
	}

	final int featureSize = data.get( 0 ).length;

	//各特徴の最大値と最小値を取得する
	double[] minArray = Arrays.copyOf( data.get( 0 ), featureSize );
	double[] maxArray = Arrays.copyOf( data.get( 0 ), featureSize );
	for( int featureIndex = 0; featureIndex < featureSize; ++featureIndex )
	{
		if( !ignoredFeatureSet.contains( featureIndex ) )
		{
			for( final double[] eachData : data )
			{
				double value = eachData[ featureIndex ];
				minArray[ featureIndex ] = Math.min( minArray[ featureIndex ], value );
				maxArray[ featureIndex ] = Math.max( maxArray[ featureIndex ], value );
			}
		}
	}

	//使う特徴を決める(cardinalityが1の特徴は無視する)
	final List< Integer > activeFeatureList = new ArrayList<>();
	for( int featureIndex = 0; featureIndex < featureSize; ++featureIndex )
	{
		if( !ignoredFeatureSet.contains( featureIndex ) )
		{
			if( minArray[ featureIndex ] != maxArray[ featureIndex ] )
			{
				activeFeatureList.add( featureIndex );
			}
		}
	}

	if( activeFeatureList.isEmpty() )
	{
		//全ての特徴で値が1種類しか存在しない場合
		return;
	}

	int selectedFeatureIndex = activeFeatureList.get( rand.nextInt( activeFeatureList.size() ) );

	//全てのデータについて、それぞれless/greaterのどちらかに分ける
	final List< double[] > lessList = new ArrayList<>( data.size() / 2 );
	final List< double[] > greaterList = new ArrayList<>( data.size() / 2 );
	final double min = minArray[ selectedFeatureIndex ];
	final double max = maxArray[ selectedFeatureIndex ];
	final double splitPoint = min + (max - min) * rand.nextDouble();

	for( final double[] eachData : data )
	{
		if( eachData[ selectedFeatureIndex ] <= splitPoint )
		{
			lessList.add( eachData );
		}
		else
		{
			greaterList.add( eachData );
		}
	}

	//このノードの情報をnodeInfoに入れ、indexをキーに、木の一部として保存する
	final double[] nodeInfo = new double[] { selectedFeatureIndex, splitPoint };
	tree.put( index, nodeInfo );

	//左側(小さい方)のノードを生成 indexは親のindex*2+1になる
	buildNode( lessList, index * 2 + 1, depth + 1, maxDepth, tree );

	//右側(大きい方)のノードを生成 indexは親のindex*2+2になる
	buildNode( greaterList, index * 2 + 2, depth + 1, maxDepth, tree );

	return;
}

public List< double[] > getSubSample( final List< double[] > orig, final int subSampleSize ) throws Exception
{
	if( orig.size() <= subSampleSize )
	{
		return new ArrayList( orig );
	}

	/*
	final List< double[] > list = new ArrayList( orig );
	Collections.shuffle( list );
	return list.subList( 0, subSampleSize );
	*/

	final Random r = new Random();
	final List< double[] > list = new ArrayList( subSampleSize );
	final int size = orig.size();
	for( int i = 0; i < subSampleSize; ++i )
	{
		list.add( orig.get( r.nextInt( size ) ) );
	}
	return list;
}

/*
 * あるデータ(data)のスコアを求める
 * 学習したMIsolationForestクラスのインスタンスを使い、学習に続いて評価を行う際に使う
 */
public double getScore( final double[] data )
{
	return MIFUtil.getScore( data, treeList, subSampleSize );
}

public List< Map< Integer, double[] > > getTreeList()
{
	return treeList;
}

/*
 * 学習後の状態を一度保存して、のちに別の場所で使う際に使用
 * ser/deser用のJavaインスタンス(HashMap)を生成する
 * treeListとsubSampleSizeだけがあれば評価を行うことができる
 * (ignoredFeatureSetはtreeListのどのノードにも登場しない、という形で実質的に反映されているので保存する必要はない)
 */
public Map getContext()
{
	final Map map = new HashMap();
	map.put( "treeList", treeList );
	map.put( "subSampleSize", subSampleSize );
	return map;
}

}