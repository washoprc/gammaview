/*
    GAMMA Viewer - AbstractFilter : Abstract interface for filters
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


    GAMMA Viewer - AbstractFilter : �ե��륿���δ��ܥ��󥿡��ե�����
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

import gammaviewer.Data.PortInfomation;
import java.util.Vector;


//abstract public class AbstractFilter
abstract public interface AbstractFilter
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

/*  public AbstractFilter( String autoName, String autoDescription )
  {
    FilterName = autoName;
    Description = autoDescription;
  }
*/


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : To apply each filter to raw port data
  // Argument  : Port Infomation to aplly and raw data
  //
  // NOTE : "autoRawData" has all raw data, so can get raw data by
  //           autoRawData.elementat( index )
  // ex,) DMCC -> DMC500 and DMC600
  //         autoRawData.get( "DMC500" )   returns raw data of DMC500
  //         autoRawData.get( "DMC600" )   returns DMC600
  //
  // Return    : Filter-applied data
  abstract public double[] applyFilter( PortInfomation autoPortInfo, Vector autoRawData );
//  abstract public Vector applyFilter( PortInfomation autoPortInfo, Vector autoRawData );


  // Purpose   : Get filter name
  // Argument  : void
  // Return    : Filter name
//  public String getFilterName()   { return FilterName; }
  abstract public String getFilterName();


  // Purpose   : To infome description of this filter
  // Argument  : void
  // Return    : Description of this filter
//  public String getDescription()  { return Description; }
  abstract public String getDescription();


  // Purpose   : Get infomation about unit
  //             Normally, this is V
  // Argument  : void
  // Return    : Unit name of return value by this filter
//  public String getUnit()         { return "V" };
  abstract public String getUnit();

/*
  // Purpose   : Get usual x arrays
  // Argument  : Port infomation
  // Return    : Array of x values
  protected double[] getNormalXArray( PortInfomation autoPortInfo )
  {
    double autoSampling = autoPortInfo.getMaxValue();
    double autoTrigger  = autoPortInfo.getMinValue();
    double autoRange = autoMaxValue - autoMinValue;

    // First get array size

    double[] autoXArray = new double[ autoArraySize ];

    return autoXArray;
  }
  */
}