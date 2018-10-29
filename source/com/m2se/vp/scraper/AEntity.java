package com.m2se.vp.scraper;

import org.apache.log4j.Logger;

public abstract class AEntity
{
	protected static Logger LOG = Logger.getRootLogger();
	
	public abstract String getKey();
	
}
