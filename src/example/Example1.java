package example;

import java.util.ArrayList;
import java.util.List;

import net.jumperz.ds.iforest.MIFModelBuilder;

/**
 * 2次元のデータの例
 */
public class Example1
{

public static void main( String[] args ) throws Exception
{
	MIFModelBuilder builder = new MIFModelBuilder( 100 );
	List< double[] > data = new ArrayList<>();

	//inlier
	data.add( new double[] { 5, 51 } );
	data.add( new double[] { 4, 50 } );
	data.add( new double[] { 4, 60 } );
	data.add( new double[] { 10, 52 } );
	data.add( new double[] { 8, 48 } );
	data.add( new double[] { 6, 47 } );
	data.add( new double[] { 1, 59 } );
	data.add( new double[] { 6, 50 } );
	data.add( new double[] { 9, 52 } );
	data.add( new double[] { 11, 40 } );
	data.add( new double[] { 2, 42 } );
	data.add( new double[] { 5, 65 } );

	//outlier
	data.add( new double[] { 20, 10 } );
	data.add( new double[] { 30, 90 } );

	//学習
	builder.build( data );

	//学習した結果を使い、いくつかのinlierとoutlierのスコアを求める

	//inlier
	p( "inliers:" );
	p( builder.getScore( new double[] { 4, 52 } ) );
	p( builder.getScore( new double[] { 5, 52 } ) );
	p( builder.getScore( new double[] { 4, 50 } ) );
	p( builder.getScore( new double[] { 6, 51 } ) );

	//outlier
	p( "\noutliers:" );
	p( builder.getScore( new double[] { 70, 80 } ) );
	p( builder.getScore( new double[] { 13, 100 } ) );
}

public static void p( Object o )
{
	System.out.println( o );
}

}