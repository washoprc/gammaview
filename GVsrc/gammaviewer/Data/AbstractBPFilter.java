/*
    GAMMA Viewer - AbstractBPFilter : Utilities for BP signals
    Copyright (C) 2002-2003  Takemura Yuichirou, Kojima Atsushi, Tsuda Eisuke


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


    GAMMA Viewer - AbstractBPFilter : BPポート用ユーティリティクラス
    Copyright (C) 2002-2003  武村祐一郎、小島有志、津田英介


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


package gammaviewer.Data;

import java.util.Vector;

public abstract class AbstractBPFilter implements AbstractFilter
{

  public AbstractBPFilter() {}


  // Purpose   : Divide 8 of channel datas from ports
  // Argument  : Port raw data
  // Return    : Datas of 8 channel
  protected Vector ports2Channel( Vector autoPortData )
  {
    // This will be returned.
    Vector autoChannelData = new Vector( 8 );

    int i = 0, j = 0, k = 0, l = 0;

    // Size of a port, this may 8192.
    int autoSizeOfAPort        = ((int[])autoPortData.elementAt( 0 )).length;
    // Size of a channel data, this may be 8192 / 8 * 12 = 12288.
    int autoSizeOfAChannelData = autoPortData.size() * autoSizeOfAPort / 8;

    // Loop each channel, this may be 8 times
    for( i = 0; i < 8; i++ )
    {
      l = 0;
      int[] autoChannel = new int[ autoSizeOfAChannelData ];

      // Maybe this loop is 12 times of loop
      for( j = 0; j < autoPortData.size(); j++ )
      {
        // Get current port.
        int[] autoDataOfAPort = (int[])autoPortData.elementAt( j );
        for( k = i; k < autoSizeOfAPort; k += 8 )
          autoChannel[l++] = autoDataOfAPort[k];
      }
      autoChannelData.addElement( autoChannel );
    }
    return autoChannelData;
  }



  // Purpose   : Get index that has max value
  // Argument  : Index array
  // Return    : Index that has max
  // (but if those are 0 or 11, then change to 1 or 10 because of least-squares method)
  protected int searchMax( double[] autoIndexes )
  {
    int autoMaxIndex = 1;
    for( int i = 0; i < autoIndexes.length; i++ )
      if( autoIndexes[autoMaxIndex] < autoIndexes[i] )
        autoMaxIndex = i;

    return autoMaxIndex;
  }



  // Least-squares method implementation.
  // This code is copyrighted by Kojima Atsushi
  //
  // Argument  : 3 x-values continuity and each values
  // Return    : The x value at peak of distribution
  protected double leastSquares( int ch1, int ch2, int ch3, double cu1, double cu2, double cu3 )
  {
    double sx,sy,sx2,sx3,sx4,sxy,sx2y,minchan,n,e,det;
    double a,b,c;
    double maxcur;
    double maxchan;
    double[] x=new double[3];
    double[] y=new double[3];
    int i;
    n=3;
    e=1.0e-20;
    sx=0.0;
    sy=0.0;
    sx2=0.0;
    sx3=0.0;
    sx4=0.0;
    sxy=0.0;
    sx2y=0.0;
    x[0]=ch1;
    x[1]=ch2;
    x[2]=ch3;
    y[0]=cu1;
    y[1]=cu2;
    y[2]=cu3;
    for(i=0;i<=2;i++){
      sx=sx+x[i];
      sy=sy+y[i];
      sx2=sx2+x[i]*x[i];
      sx3=sx3+x[i]*x[i]*x[i];
      sx4=sx4+x[i]*x[i]*x[i]*x[i];
      sxy=sxy+x[i]*y[i];
      sx2y=sx2y+x[i]*x[i]*y[i];
    }
    det=n*(sx2*sx4-sx3*sx3)+sx*(sx2*sx3-sx*sx4)+sx2*(sx*sx3-sx2*sx2);
    if(Math.sqrt(det*det)<=1.0e-6){
      det=1.0e-6;
    }
    e=e*Math.sqrt(sx*sx)/n;
    if(Math.sqrt(det*det)<=e){
      maxchan=0.0;
      maxcur=0.0;
    }
    else
    {
      c=(sy*(sx2*sx4-sx3*sx3)+sx*(sx2y*sx3-sxy*sx4)+sx2*(sxy*sx3-sx2y*sx2))/n;
      b=(n*(sxy*sx4-sx2y*sx3)+sy*(sx2*sx3-sx*sx4)+sx2*(sx2y*sx-sxy*sx2))/det;
      a=(n*(sx2y*sx2-sxy*sx3)+sx*(sxy*sx2-sx2y*sx)+sy*(sx*sx3-sx2*sx2))/det;
      //y=ax2+bx+c
      if(Math.sqrt(a*a)<=1.0e-6){
        a=1.0-6;
      }
      maxchan=-b/(2.0*a);
      maxcur=c-b*b/(4.0*a);
    }
    return maxchan;
  }


  // Purpose   : I do not know what is doing in this method
  // Argument  : void
  // Return    : void
  public int[] flagcal( double[] elsweep )
  {
    Vector flagVector = new Vector(100);
    int i,j,start;
    j=0;
    start=0;
    for( i = 0; i < 12287; i++ )
    {
      if( start == 0 && ( elsweep[i] > ( elsweep[0] + elsweep[1] ) / 2 + 10.0 ) )
      {
        start = 1;
        flagVector.addElement( new Integer(i) );
      }
      if( start == 1 )
      {
        switch(j)
        {
          case 0:
            if( elsweep[i] > elsweep[i+1] && elsweep[i] > 100.0 )
            {
              flagVector.addElement( new Integer(i) );
              j = 1;
            }
            break;
          case 1:
            if( elsweep[i] < elsweep[i+1] && elsweep[i] > 100.0 )
            {
              flagVector.addElement( new Integer(i) );
              j = 0;
            }
            break;
        }
      }
    }

// Save to int array
    int[] sweepflag = new int[ flagVector.size() ];
    for( i = 0; i < sweepflag.length; i++ )
      sweepflag[i] = ((Integer)flagVector.elementAt(i)).intValue();

    return sweepflag;
  }


  // Purpose   : Get infomation about unit
  // Argument  : void
  // Return    : Unit name of return value by this filter
  public double[] getAveragedArray( double[] autoArray )
  {
    int autoNewArraySize  = 0;
    // If odd number
    if( autoArray.length % 2 == 1 )
      autoNewArraySize = autoArray.length / 2;
    else // Even nubmer
      autoNewArraySize = autoArray.length / 2 - 1;

    double[] autoNewArray = new double[ autoNewArraySize ];
    for( int i = 0; i < autoNewArraySize; i++ )
      autoNewArray[i] = ( autoArray[ 2*i ]
                        + autoArray[ 2*i + 1]
                        + autoArray[ 2*i + 2] ) / 3.0;

    return autoNewArray;
  }

  // Purpose   : Get infomation about unit
  // Argument  : void
  // Return    : Unit name of return value by this filter
  public String getUnit()             { return "V"; }
}