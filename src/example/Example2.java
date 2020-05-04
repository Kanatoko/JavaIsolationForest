package example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.jumperz.ds.iforest.MIFModelBuilder;
import net.jumperz.ds.iforest.MIFUtil;

/**
 * 学習(モデル構築)と評価(使用)を別の場所でやる例
 */
public class Example2
{

public static void main( String[] args ) throws Exception
{
	Map context = buildAndGetContext();

	//inlier
	p( "inliers:" );
	p( MIFUtil.getScore( new double[] { 4, 52 }, context ) );
	p( MIFUtil.getScore( new double[] { 5, 52 }, context ) );
	p( MIFUtil.getScore( new double[] { 4, 50 }, context ) );
	p( MIFUtil.getScore( new double[] { 6, 51 }, context ) );

	//outlier
	p( "\noutliers:" );
	p( MIFUtil.getScore( new double[] { 70, 80 }, context ) );
	p( MIFUtil.getScore( new double[] { 13, 100 }, context ) );
}

public static Map buildAndGetContext() throws Exception
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

	return builder.getContext();
}

public static void p( Object o )
{
	System.out.println( o );
}

}