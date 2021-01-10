package net.jumperz.ds.iforest;

import java.util.List;
import java.util.Map;

/*
 * モデルからPOJOを生成するクラス
 */
public class MIFPOJOWriter
{
public String getCode( final String packageName, final String className, List< Map< Integer, double[] > > treeList, final int subSampleSize ) throws Exception
{
	final StringBuilder b = new StringBuilder();

	b.append( "package " );
	b.append( packageName );
	b.append( ";\n" );
	b.append( "public class " );
	b.append( className );
	b.append( " extends net.jumperz.ds.iforest.MIFModel" );
	b.append( "\n{\n" );

	b.append( "public " );
	b.append( className );
	b.append( "()\n{\nsubSampleSize = " );
	b.append( subSampleSize );
	b.append( ";\n}\n" );

	b.append( "public double getScore( final double[] data )\n{\ndouble depth = 0;\n" );
	for( int i = 0; i < treeList.size(); ++i )
	{
		b.append( "depth += getDepth" );
		b.append( i );
		b.append( "( data );\n" );
	}

	b.append( "final double avgDepth = depth / " );
	b.append( treeList.size() );
	b.append( ";\n" );

	b.append( "double score = net.jumperz.ds.iforest.MIFUtil.getScore( avgDepth, subSampleSize );\n" );
	b.append( "return score;\n" );
	b.append( "}\n" );

	for( int i = 0; i < treeList.size(); ++i )
	{
		b.append( "private static double getDepth" );
		b.append( i );
		b.append( "( final double[] data )\n{\ndouble depth = 0;\n" );

		processNode( 0, treeList.get( i ), 0, b );
		b.append( "return depth;\n}\n" );
	}

	b.append( "}" );

	return b.toString();
}

public String getCodeForLargeTree( final String packageName, final String className, List< Map< Integer, double[] > > treeList, final int subSampleSize ) throws Exception
{
	final StringBuilder b = new StringBuilder();

	b.append( "package " );
	b.append( packageName );
	b.append( ";\n" );
	b.append( "import net.jumperz.ds.iforest.*;\n" );
	b.append( "public class " );
	b.append( className );
	b.append( " extends MIFModel" );
	b.append( "\n{\n" );

	b.append( "private MIFModel[] models = new MIFModel[]{\n" );

	for( int i = 0; i < treeList.size(); ++i )
	{
		b.append( "new MIFModel() {\n" );
		b.append( "@Override\n" );
		b.append( "public double getScore( double[] data )\n" );
		b.append( "{\ndouble depth = 0;\n" );
		processNode( 0, treeList.get( i ), 0, b );
		b.append( "return depth;\n}\n" );
		b.append( "},\n" );
	}

	b.append( "};\n\n" );

	b.append( "public " );
	b.append( className );
	b.append( "()\n{\nsubSampleSize = " );
	b.append( subSampleSize );
	b.append( ";\n}\n\n" );

	b.append( "public double getScore( final double[] data )\n{\ndouble depth = 0;\n" );
	b.append( "for( MIFModel model : models )\n" );
	b.append( "{\n" );
	b.append( "	depth += model.getScore( data );\n" );
	b.append( "}\n" );
	b.append( "final double avgDepth = depth / " );
	b.append( treeList.size() );
	b.append( ";\n" );
	b.append( "double score = net.jumperz.ds.iforest.MIFUtil.getScore( avgDepth, subSampleSize );\n" );
	b.append( "return score;\n" );
	b.append( "}\n\n" );

	b.append( "}" );

	return b.toString();
}

private static void processNode( final int index, final Map< Integer, double[] > tree, final int depth, final StringBuilder buf )
{
	final double[] nodeInfo = tree.get( index );
	if( nodeInfo == null )
	{
		addSpace( depth, buf );
		buf.append( "depth += " );
		buf.append( depth );
		buf.append( ";\n" );
	}
	else if( !tree.containsKey( index * 2 + 1 ) && !tree.containsKey( index * 2 + 2 ) )
	{
		addSpace( depth, buf );
		buf.append( "depth += " );
		buf.append( depth + 1 );
		buf.append( ";\n" );
	}
	else
	{
		final int selectedFeatureIndex = ( int )nodeInfo[ 0 ];
		final double splitPoint = nodeInfo[ 1 ];

		addSpace( depth, buf );
		buf.append( "if( data[ " );
		buf.append( selectedFeatureIndex );
		buf.append( " ] <= " );
		buf.append( splitPoint );
		buf.append( " )\n" );
		addSpace( depth, buf );
		buf.append( "{" );
		buf.append( "\n" );

		processNode( index * 2 + 1, tree, depth + 1, buf );

		addSpace( depth, buf );
		buf.append( "}\n" );
		addSpace( depth, buf );
		buf.append( "else\n" );
		addSpace( depth, buf );
		buf.append( "{\n" );

		processNode( index * 2 + 2, tree, depth + 1, buf );

		addSpace( depth, buf );
		buf.append( "}\n" );
	}
}

private static void addSpace( final int depth, final StringBuilder buf )
{
	for( int i = 0; i < depth; ++i )
	{
		buf.append( '\t' );
	}
}

}