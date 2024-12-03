/*
    GAMMA Viewer - NLFilter : Filter for NL
    Copyright (C) 2002-2003  Microwave group and Tsuda Eisuke


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


    GAMMA Viewer - NLFilter : NL用フィルター
    Copyright (C) 2002-2003  マイクロ波グループと津田英介


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

/*
    This NLFilter was modified by TAKAYUKI KOBAYASHI at July 6th, 2006.
    The reasion of modification is missing calculation of new type of 
    NLFilter for MJCxx ports.
    
    The original source code was applyed the GPL lisence and copyright 
    is owned by Eisuke Tsuda. This modified source code also applyed GPL
    lisence and copyright of modified part is owned by Takayuki Kobayashi.

    GAMMA Viewr - NL Filter (Modified)
    Copyright (C) 2006 Takayuki Kobayashi
*/

package gammaviewer.Data.Filter;

import gammaviewer.Data.PortInfomation;
import gammaviewer.Data.AbstractFilter;

import java.util.*;


public class NLFilter implements AbstractFilter
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  String FilterName = "NL Filter";


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public NLFilter()   {}


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : To apply filter
  // Argument  : Port infomation and raw data
  // Return    : Applied data
  //
  // FOR DETAIL, See basic interface "AbstractFilter"
  //
  // This function was modfied by T. Kobayashi particaly (2006/07/06).
  // Main purpose is correction for new type of NL.
  public double[] applyFilter( PortInfomation autoPortInfo, Vector autoRawData )
  {
    int[] autoData1   = null;
    int[] autoData2 = null;
    double[] autoSine = null;
    double[] autoCosine = null;


    // This is new type of NL
    if( autoRawData.size() >= 2 )
    {
      int autoDepth  = 4095;
      double autoMaxVal = 5.0;
      double autoMinVal = -5.0;
      double autoValRatio = ( autoMaxVal - autoMinVal ) / autoDepth;
      
      // Set sine and cosine
      autoData1 = (int[])autoRawData.elementAt( 0 );
      autoData2 = (int[])autoRawData.elementAt( 1 );

      if( ( autoData1 == null ) || ( autoData2 ==null ) )
        return null;
      
      autoSine = new double[ autoData1.length ];
      autoCosine = new double[ autoData2.length ];
      
      if( autoData1.length != autoData2.length)
        return null;

      int i = 0;
      for( i = 0; i < autoData1.length; i++ )
      {
      	autoSine[i] = autoData1[i] * autoValRatio + autoMinVal;
      	autoCosine[i] = autoData2[i] * autoValRatio + autoMinVal;
      }
    }
    else if( autoRawData.size() == 1 )
    {
      int[] autoData = (int[])autoRawData.elementAt( 0 );
      if( autoData == null )
        return null;

      autoSine   = new double[ autoData.length ];
      autoCosine = new double[ autoData.length ];

      // First get sine and cosine signal
      // Head 8 bit is sine signal and tail 8bit is cosine signal
      //    | sine ||cosine|
      //    0100100101011100

      int i = 0;
      for( i = 0; i < autoData.length; i++ )
      {
        autoSine[i]   = ( autoData[i] / 256 ) - 128;
        autoCosine[i] = ( autoData[i] % 256 ) - 128;
      }
    }
    else
      return null;

    // Create NLCC data array
    double[] autoNLCC = new double[ autoSine.length ];

    int i = 0;
    double kuri_cc = 0;
    double scdum_cc = 0;
    double sc_cc = 0;

    for( i = 0; i < autoNLCC.length; i++ )
    {
      sc_cc = Math.atan2( (double)autoSine[i], (double)autoCosine[i] );

      if( ( scdum_cc * sc_cc ) < 0 && sc_cc < -2.0 )
        kuri_cc++;
      if( ( scdum_cc * sc_cc ) < 0 && sc_cc > 2.0 )
        kuri_cc--;

      scdum_cc	= sc_cc;
      sc_cc = sc_cc + kuri_cc * 2 * Math.PI;
      autoNLCC[i] = sc_cc * 5.2 / ( 2 * Math.PI );
    }

    // Maybe offset....
    double autoOffset = autoNLCC[0];
    for( i = 0; i < autoNLCC.length; i++ )
      autoNLCC[i] -= autoOffset;

    return autoNLCC;
  }


  // Purpose   : Get filter name
  //
  // FOR DETAIL, See basic interface "AbstractFilter"
  //
  public String getFilterName()      { return FilterName; }


  // Purpose   : Get description
  //
  // FOR DETAIL, See basic interface "AbstractFilter"
  //
  //----
  // This description was particaly modified by T. Kobayashi (2006/07/06)
  //	新しい(MJCxx)	->	Old/New兼用
  public String getDescription()      { return "NLCCのOld/New兼用タイプのフィルターです。"; }


  // Purpose   : Get infomation about unit
  // Argument  : void
  // Return    : Unit name of return value by this filter
  public String getUnit()             { return "E13 /cm^2"; }
}