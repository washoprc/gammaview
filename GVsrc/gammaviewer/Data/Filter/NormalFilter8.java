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


    GAMMA Viewer - NormalFilter8 : ８ビットポート用フィルター
    Copyright (C) 2002-2003  津田英介


    このプログラムはフリーソフトウェアです。あなたはこれを、フリーソフ
    トウェア財団によって発行された GNU 一般公衆利用許諾契約書(バージョ
    ン2か、希望によってはそれ以降のバージョンのうちどれか)の定める条件
    の下で再頒布または改変することができます。

    このプログラムは有用であることを願って頒布されますが、*全くの無保
    証* です。商業可能性の保証や特定の目的への適合性は、言外に示された
    ものも含め全く存在しません。詳しくはGNU 一般公衆利用許諾契約書をご
    覧ください。

    あなたはこのプログラムと共に、GNU 一般公衆利用許諾契約書の複製物を
    一部受け取ったはずです。もし受け取っていなければ、フリーソフトウェ
    ア財団まで請求してください(宛先は the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)。
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
  public String getDescription()     { return "通常の8bitのポートを扱います。"; }


  // Purpose   : Get infomation about unit
  // Argument  : void
  // Return    : Unit name of return value by this filter
  public String getUnit()             { return "V"; }
}