package net.jumperz.ds.iforest;

import java.io.Serializable;

public abstract class MIFModel
implements Serializable
{
protected int subSampleSize;
public abstract double getScore( final double[] data );

}