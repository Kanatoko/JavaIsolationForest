package example;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.jumperz.ds.iforest.MIFModelBuilder;
import net.jumperz.ds.iforest.MIFPOJOWriter;

/**
 * POJOを生成する例
 */
public class Example3
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

	//POJO生成
	MIFPOJOWriter pw = new MIFPOJOWriter();
	String javaCode = pw.getCode( "work", "model1", builder.getTreeList(), builder.getSubSampleSize() );
	p( javaCode );

	//POJO保存
	try( OutputStream out = new FileOutputStream( "work/model1.java" ))
	{
		out.write( javaCode.getBytes() );
	}
}

public static void p( Object o )
{
	System.out.println( o );
}

}