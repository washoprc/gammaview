/*
    GAMMA Viewer - NormalFilter8 : Filter for 8-bit port
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


    GAMMA Viewer - NormalFilter8 : ���ӥåȥݡ����ѥե��륿��
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


package gammaviewer.Data.Filter;

import gammaviewer.Data.PortInfomation;
import gammaviewer.Data.AbstractFilter;

import java.util.*;


public class NormalFilter8 implements AbstractFilter
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  String FilterName = "NormalFilter(8bit)";


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public NormalFilter8()   {}


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : To apply filter
  // Argument  : Port infomation and raw data
  // Return    : Applied data
  //
  // FOR DETAIL, See basic interface "AbstractFilter"
  //
  public double[] applyFilter( PortInfomation autoPortInfo, Vector autoRawData )
  {
    if( autoRawData.size() < 1 )
      return null;

    int[] autoRawDataArray = (int[])autoRawData.elementAt( 0 );

    if( autoRawDataArray == null )
      return null;

    double[] autoAppliedData = new double[ autoRawDataArray.length ];

    final double autoDataSizeBit = 256.0;
    final double autoMaxValue = autoPortInfo.getMaxValue();
    final double autoMinValue = autoPortInfo.getMinValue();
    final double autoRange = autoMaxValue - autoMinValue;

    for( int i = 0; i < autoAppliedData.length; i++ )
      autoAppliedData[i] = ( autoRawDataArray[i] * autoRange / autoDataSizeBit )
                           + autoMinValue;

    return autoAppliedData;
  }


  // Purpose   : Get filter name
  //
  // FOR DETAIL, See basic interface "AbstractFilter"
  //
  public String getFilterName() { return FilterName; }


  // Purpose   : Get description
  //
  // FOR DETAIL, See basic interface "AbstractFilter"
  //
  public String getDescription()     { return "�̾��8bit�Υݡ��Ȥ򰷤��ޤ���"; }


  // Purpose   : Get infomation about unit
  // Argument  : void
  // Return    : Unit name of return value by this filter
  public String getUnit()             { return "V"; }
}