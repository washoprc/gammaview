/*
    GAMMA Viewer - FilterLoader : Class for loading filters dinamically
    Copyright (C) 2002-2003  Tsuda Eisuke


    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


    GAMMA Viewer - FilterLoader : �ե��륿����¹Ի����ɤ��뤿��Υ��饹
    Copyright (C) 2002-2003  ���ıѲ�


    ���Υץ����ϥե꡼���եȥ������Ǥ������ʤ��Ϥ���򡢥ե꡼����
    �ȥ��������Ĥˤ�ä�ȯ�Ԥ��줿 GNU ���̸������ѵ��������(�С�����
    ��2������˾�ˤ�äƤϤ���ʹߤΥС������Τ����ɤ줫)��������
    �β��Ǻ����ۤޤ��ϲ��Ѥ��뤳�Ȥ��Ǥ��ޤ���

    ���Υץ�����ͭ�ѤǤ��뤳�Ȥ��ä����ۤ���ޤ�����*������̵��
    ��* �Ǥ������Ȳ�ǽ�����ݾڤ��������Ū�ؤ�Ŭ�����ϡ������˼����줿
    ��Τ�ޤ�����¸�ߤ��ޤ��󡣾ܤ�����GNU ���̸������ѵ���������
    ������������

    ���ʤ��Ϥ��Υץ����ȶ��ˡ�GNU ���̸������ѵ���������ʣ��ʪ��
    ����������ä��Ϥ��Ǥ����⤷������äƤ��ʤ���С��ե꡼���եȥ���
    �����Ĥޤ����ᤷ�Ƥ�������(����� the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)��
*/

package gammaviewer.Data;

import gammaviewer.Data.AbstractFilter;
import gammaviewer.Data.Filter.*;

import gammaviewer.*;
import java.util.*;
import java.io.*;


public class FilterLoader extends ClassLoader implements ViewerConstants
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  Hashtable FilterList = new Hashtable();


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------


  public FilterLoader()
  {
    this.loadFilter();
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------

/* MAC */
/*
  // Purpose   : loadClass method overwrite for java 1.1
  // Argument  : void
  // Return    : void
  public Class loadClass( String name ) throws ClassNotFoundException
  { return this.loadClass( name, false ); }
  public Class loadClass( String name, boolean resolve ) throws ClassNotFoundException
  {
    Class autoClass = super.findLoadedClass( name );
    if( autoClass == null )
      autoClass = super.findSystemClass( name );
    if( resolve && autoClass != null )
      super.resolveClass( autoClass );

    return autoClass;
  }
*/
/* End Of MAC */

  // Purpose   : Get filters from some directories
  // Argument  : void
  // Return    : void
  private void loadFilter()
  {
    String autoHome = this.CURRENTDIR;
    String[] autoDirs = { autoHome + File.separator + "filter",
                          autoHome + File.separator + "Data" + File.separator + "filter",
                          autoHome + File.separator +
                          "gammaviewer" + File.separator + "Data" + File.separator + "filter",
                          autoHome + File.separator +
                          "classes" + File.separator +
                          "gammaviewer" + File.separator +
                          "Data" + File.separator +
                          "Filter" };

    String[] autoFilterList = null;
    String autoFilterClassPath = null;
    String autoFilterClassName = null;
    String autoFilterHeader = "gammaviewer.Data.Filter.";
    int autoPos = 0;

    try
    {
      for( int i = 0; i < autoDirs.length; i++ )
      {
        File autoDiractory = new File( autoDirs[i] );
        // Check there is a directory and filters in
        if( autoDiractory.exists() )
        {
          autoFilterList = autoDiractory.list();
          for( int j = 0; j < autoFilterList.length; j++ )
          {
            // get filter class name without ".java"
            autoPos = autoFilterList[j].lastIndexOf( "." );
            autoFilterClassName = autoFilterHeader + autoFilterList[j].substring( 0, autoPos );
            autoFilterClassPath = autoDiractory.getPath() + autoFilterList[j];

            if( !autoFilterList[j].equals( "AbstractFilter.class" ) )
            {
              // Read class
              Class autoClass = super.loadClass( autoFilterClassName );
              super.resolveClass( autoClass );
              AbstractFilter autoFilter = (AbstractFilter)autoClass.newInstance();
              // Add to hash table
              FilterList.put( autoFilter.getFilterName(), autoFilter );
            } // If "AbstractFilter"

          } // for
        } // if exists
      } // for

    } // try
    catch( Exception autoExcpt )
    {
      // If something happen, add manually
      AbstractFilter autoFilter = new DMCCFilter();
      FilterList.put( autoFilter.getFilterName(), autoFilter );
      autoFilter = new NLFilter();
      FilterList.put( autoFilter.getFilterName(), autoFilter );
      autoFilter = new NormalFilter10();
      FilterList.put( autoFilter.getFilterName(), autoFilter );
      autoFilter = new NormalFilter12();
      FilterList.put( autoFilter.getFilterName(), autoFilter );
      autoFilter = new NormalFilter16();
      FilterList.put( autoFilter.getFilterName(), autoFilter );
      autoFilter = new NormalFilter8();
      FilterList.put( autoFilter.getFilterName(), autoFilter );
    }
  }


  // Purpose   : Get filter name list
  // Argument  : void
  // Return    : Vector of filter name
  public Vector getFilterNameList()
  {
    Vector autoFilterNameList = new Vector();

    // Get keys
    for( Enumeration autoKeys = FilterList.keys(); autoKeys.hasMoreElements(); )
      autoFilterNameList.addElement( autoKeys.nextElement() );

    // Return sorted vector
    return ViewerDatas.quickSort( autoFilterNameList );
  }


  // Purpose   : Get filter itself
  // Argument  : Filter name
  // Return    : Filter instance
  public AbstractFilter getFilter( Object autoFilterName ) throws InfomationException
  {
    // For support oldversion NL Filter
    String autoFilterNameStr = autoFilterName.toString();
    if( autoFilterNameStr.equals( "NLCC Filter(new)" ) ||
        autoFilterNameStr.equals( "NLCC Filter(old)" ) ||
        autoFilterNameStr.equals( "NL Filter(new)" )   ||
        autoFilterNameStr.equals( "NL Filter(old)" ) )
      autoFilterName = "NL Filter";

    AbstractFilter autoFilter = (AbstractFilter)FilterList.get( autoFilterName );

    if( autoFilter == null )
      throw new InfomationException( "���ꤵ�줿�ե��륿������������ޤ���" );
    else
      return autoFilter;
  }
}