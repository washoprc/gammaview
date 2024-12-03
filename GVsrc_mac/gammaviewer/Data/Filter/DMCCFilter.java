/*
    GAMMA Viewer - DMFilter : Filter for Diamag
    Copyright (C) 2002-2003  Yamaguchi yusuke, Tsuda Eisuke


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


    GAMMA Viewer - DMFilter : Diamag�p�̃t�B���^�[
    Copyright (C) 2002-2003  �R���T���@�Óc�p��


    ���̃v���O�����̓t���[�\�t�g�E�F�A�ł��B���Ȃ��͂�����A�t���[�\�t
    �g�E�F�A���c�ɂ���Ĕ��s���ꂽ GNU ��ʌ��O���p�����_��(�o�[�W��
    ��2���A��]�ɂ���Ă͂���ȍ~�̃o�[�W�����̂����ǂꂩ)�̒�߂����
    �̉��ōĔЕz�܂��͉��ς��邱�Ƃ��ł��܂��B

    ���̃v���O�����͗L�p�ł��邱�Ƃ�����ĔЕz����܂����A*�S���̖���
    ��* �ł��B���Ɖ\���̕ۏ؂����̖ړI�ւ̓K�����́A���O�Ɏ����ꂽ
    ���̂��܂ߑS�����݂��܂���B�ڂ�����GNU ��ʌ��O���p�����_�񏑂���
    �����������B

    ���Ȃ��͂��̃v���O�����Ƌ��ɁAGNU ��ʌ��O���p�����_�񏑂̕�������
    �ꕔ�󂯎�����͂��ł��B�����󂯎���Ă��Ȃ���΁A�t���[�\�t�g�E�F
    �A���c�܂Ő������Ă�������(����� the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA)�B
*/

/*
    This DMFilter was modified by TAKAYUKI KOBAYASHI at July 8th, 2006.
    The reasion of modification is missing expression of unit of DM. 
    
    The original source code was applyed the GPL lisence and copyright 
    is owned by Eisuke Tsuda. This modified source code also applyed GPL
    lisence and copyright of modified part is owned by Takayuki Kobayashi.

    GAMMA Viewr - DMFilter (Modified)
    Copyright (C) 2006 Takayuki Kobayashi
*/

package gammaviewer.Data.Filter;

import gammaviewer.Data.PortInfomation;
import gammaviewer.Data.AbstractFilter;

import java.util.Vector;


public class DMCCFilter implements AbstractFilter
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  String FilterName = "Diamag Filter";


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public DMCCFilter()   {}


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : To apply filter
  // Argument  : Port infomation and raw data
  // Return    : Applied data
  //
  // FOR DETAIL, See basic interface "AbstractFilter"
  //
  public double[] applyFilter(PortInfomation autoPortInfo, Vector autoRawData)
  {
    if( autoRawData.size() < 2 )
      return null;
    int[] autoInnerCandidate = (int[])autoRawData.elementAt( 0 );
    int[] autoOuterCandidate = (int[])autoRawData.elementAt( 1 );

    if( ( autoInnerCandidate == null ) || ( autoOuterCandidate ==null ) )
      return null;

    double[] autoInner = new double[ autoInnerCandidate.length ];
    double[] autoOuter = new double[ autoOuterCandidate.length ];

    int i = 0;
    final double autoMaxValue = autoPortInfo.getMaxValue();
    final double autoMinValue = autoPortInfo.getMinValue();
    final double autoRange = autoMaxValue - autoMinValue;

    for( i = 0; i < autoInner.length; i++ )
      autoInner[i] = ( autoInnerCandidate[i] - 2048.0 ) * autoRange / 4096.0 / 10.0;
    for( i = 0; i < autoOuter.length; i++ )
      autoOuter[i] = ( autoOuterCandidate[i] - 2048.0 ) * autoRange / 4096.0 / 10.0;
    // Last 10 is amp gain, first 10 is +-5 volt.

    // Sampling time in 'ms'
    final double autoSampling = autoPortInfo.getSamplingTime() / 1000;

    // Ripple ratio
    double[] autoRipple = new double[ autoInner.length ];
    double[] autoRatio = new double[ autoInner.length ];

    for( i = 0; i < autoRatio.length; i++ )
    {
      autoRipple[i] = autoOuter[i] - autoInner[i];
//      if( Math.abs( autoRipple[i] ) <= 1E-9 )
//        autoRipple[i] = 1;
      autoRatio[i] = autoInner[i] / autoRipple[i];
    }

    // Get ratio average until 50ms ( 0 - 50 ms average )
    double autoRatioAverage = 0;
    int autoDataAmountUntilStartTime = (int)(autoPortInfo.getStartTime() / autoSampling);

    for( i = 0; i < autoDataAmountUntilStartTime; i++ )
    {
      if( Math.abs( autoRipple[i] ) != 0.0 )
        autoRatioAverage += Math.abs( autoRatio[i] );
      else
        autoRatioAverage += 1;
    }
    autoRatioAverage /= autoDataAmountUntilStartTime;

    // Exclude ripple signal ( Still include some offset )
    double[] autoDiamagSignal = new double[ autoInner.length ];
    for( i = 0; i < autoDiamagSignal.length; i++ )
      autoDiamagSignal[i] = autoInner[i] - autoRatioAverage * autoRipple[i];

    // Exclude 'Offset'

    double autoAmountOfTheEndOfPlasmaShot = autoPortInfo.getEndTime() / autoSampling;

    double autoOffset = 0;
    for( i = 0; i < autoAmountOfTheEndOfPlasmaShot; i++ )
      autoOffset += autoDiamagSignal[i];
    autoOffset /= autoAmountOfTheEndOfPlasmaShot;

    for( i = 0; i < autoDiamagSignal.length; i++ )
      autoDiamagSignal[i] -= autoOffset;

    // Get 'Real' Diamag
    double[] autoDiamag = new double[ autoDiamagSignal.length ];
    // Sampling time in 'sec'
    final double autoSamplingSecond = autoPortInfo.getSamplingTime() / 1000 / 1000;
    // Diamag coil's turn
    final double autoTurn = 10;

    // Integrate for t(Time)
    int j = 0;
    for( i = 0; i < autoDiamag.length; i++ )
    {
      autoDiamag[i] = 0;
      for( j = 0; j <= i; j++ )
        autoDiamag[i] += ( autoDiamagSignal[j] * autoSamplingSecond ) / autoTurn;
    }

    for( i = 0; i < autoDiamag.length; i++ )
      autoDiamag[i] = Math.abs( autoDiamag[i]) * 1E4;

    return autoDiamag;
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
  public String getDescription()      { return "Diamag�p�̃t�B���^�[�ł��B"; }


  // Purpose   : Get infomation about unit
  // Argument  : void
  // Return    : Unit name of return value by this filter
  // This unit was changed by T. Kobayashi (2006/07/08)
  // E-6 -> E-4
  public String getUnit()             { return "E-4 Wb"; }
}