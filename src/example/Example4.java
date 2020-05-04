package example;

import net.jumperz.ds.iforest.MIFModel;

/**
 * POJOを利用する例
 * (Example3をantで実行後に使用する)
 */
public class Example4
{

public static void main( String[] args ) throws Exception
{
	//POJOをロード
	MIFModel pojoModel = ( MIFModel )(Class.forName( "work.model1" ).newInstance());
	
	//学習した結果を使い、いくつかのinlierとoutlierのスコアを求める

	//inlier
	p( "inliers:" );
	p( pojoModel.getScore( new double[] { 4, 52 } ) );
	p( pojoModel.getScore( new double[] { 5, 52 } ) );
	p( pojoModel.getScore( new double[] { 4, 50 } ) );
	p( pojoModel.getScore( new double[] { 6, 51 } ) );

	//outlier
	p( "\noutliers:" );
	p( pojoModel.getScore( new double[] { 70, 80 } ) );
	p( pojoModel.getScore( new double[] { 13, 100 } ) );
}

public static void p( Object o )
{
	System.out.println( o );
}

}