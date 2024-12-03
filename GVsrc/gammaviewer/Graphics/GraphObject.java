/*
    GAMMA Viewer - GraphObject : Class really drawing graphics
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


    GAMMA Viewer - GraphObject : グラフを実際に描画するクラス
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

/*
    This GraphObject was modified by TAKAYUKI KOBAYASHI at July 6th, 2006.
    The reasion of modification is drawing all values in graph without 
    steps and drawing the x value when "Ctrl+Left Click" is done.
    
    The original source code was applyed the GPL lisence and copyright 
    is owned by Eisuke Tsuda. This modified source code also applyed GPL
    lisence and copyright of modified part is owned by Takayuki Kobayashi.

    GAMMA Viewr - GraphObject (Modified)
    Copyright (C) 2006 Takayuki Kobayashi
*/


package gammaviewer.Graphics;

import gammaviewer.Data.PortData;
import gammaviewer.Data.PortInfomation;

import java.util.*;
import java.text.DecimalFormat;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GraphObject extends JPanel
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // Graphics manager
  GraphicsManager GrpMgr = null;

  // Data
  PortData ShowPortData = null;

  // Start and end position
  Dimension StartPosition = new Dimension( 1, 1 );
  Dimension EndPosition = new Dimension( 1, 1 );

  // Places of data to show
  Dimension ObjectSize   = new Dimension( 1, 1 );    // Size of this object
  Dimension FrameSize    = new Dimension( 1, 1 );    // Size of frame rect
  Dimension Offset       = new Dimension( 1, 1 );    // Origin offset
  Dimension StartTimePos = new Dimension( 1, 1 );    // Position of start time
  Dimension EndTimePos   = new Dimension( 1, 1 );    // Position of end time
  Dimension MaxValuePos  = new Dimension( 1, 1 );    // Position of max value
  Dimension MinValuePos  = new Dimension( 1, 1 );    // Position of min value
  Dimension TitlePos     = new Dimension( 1, 1 );    // Position of title

  // Font size
  int FontSize = 12;

  // If want to lock of infomation position
  boolean LockPlace = false;


  //-----------------------------------------
  /*  Constructors  */
  //-----------------------------------------

  public GraphObject( GraphicsManager autoGrpMgr, PortData autoPortData )
  {
    GrpMgr = autoGrpMgr;
    ShowPortData = autoPortData;
    this.setBackground( Color.white );

    addMouseListener( new MouseAdapter()
    {
      public void mousePressed( MouseEvent e )
      {
        if( e.isControlDown() )
      	  getValue( e );
        else
          changeViewMode();
      }
    });
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Tell change view mode to graphics manager
  // Argument  : Clecked GraphObject
  // Return    : void
  public void changeViewMode()
  {
    GrpMgr.changeViewMode( this );
  }


  // Purpose   : Lock or unlock infomation position
  //             These methods are used when printing
  // Argument  : void
  // Return    : void
  public void lockInfomationPosition()    { LockPlace = true; }
  public void unlockInfomationPosition()  { LockPlace = false; }


  // Purpose   : To set object size.  When this mothod is called,
  //             Usually lockInfomationPosition() is also called.
  //             These methods are used when printing
  // Argument  : void
  // Return    : void
  public void setObjectSize( Dimension autoSize )
  {
    ObjectSize = autoSize;
  }


  // Purpose   : Arrangment of place of infomations
  // Argument  : Device size ( size of writing out device size )
  //             Normally, this is same getSize(), sometime printer size
  // Return    : void
  public void setInfomationPosition()
  {
    // If this object size is locked, not set getSize() to ObjectSize.
    // ObjectSize is already set.
    if( ! LockPlace )
      ObjectSize = getSize();

    // Set font size to default 12 point
    FontSize = 12;

    //----------------------------------------------//
    /* height setting                               */
    //----------------------------------------------//

    // If the FrameSize.height / 10 is smaller than font size
    // Then offset height will be font size
    if( ObjectSize.height / 10 < FontSize )
    {
      Offset.height       = FontSize;
      FrameSize.height    = ObjectSize.height - 2 * FontSize;
      StartTimePos.height = ObjectSize.height;
      EndTimePos.height   = ObjectSize.height;
      MaxValuePos.height  = Offset.height + FontSize;
      MinValuePos.height  = Offset.height + FrameSize.height;
      TitlePos.height     = FontSize;
    }

    // In this pattarn, frame size is 8/10 of whole size,
    // And offset will be 1/10 of whole size
    else
    {
      Offset.height       = ObjectSize.height / 10;

      // If offset is more than two times bigger than font size,
      // then change font size
      if( Offset.height >= 2 * FontSize )
        // Create half of offset size of font
        FontSize = Offset.height / 2;

      FrameSize.height    = ObjectSize.height - 2 * Offset.height;
      int autoPosOffset   = ( Offset.height - FontSize ) / 2;
      StartTimePos.height = ObjectSize.height - autoPosOffset;
      EndTimePos.height   = ObjectSize.height - autoPosOffset;
      MaxValuePos.height  = Offset.height + FontSize;
      MinValuePos.height  = Offset.height + FrameSize.height;
      TitlePos.height     = autoPosOffset + FontSize;
    }

    //----------------------------------------------//
    /* Width settig                                 */
    //----------------------------------------------//

    // If the size.width / 10 is smaller than two times of font size
    // Then offset width will be two times of font size
    if( ObjectSize.width / 10 < 2 * FontSize )
    {
      FrameSize.width    = ObjectSize.width - 4 * FontSize;
      Offset.width       = 2 * FontSize;
      StartTimePos.width = Offset.width;
      EndTimePos.width   = FrameSize.width;
      MaxValuePos.width  = Offset.width / 10;
      MinValuePos.width  = Offset.width / 10;
      TitlePos.width     = Offset.width;
    }
    // In this pattarn, frame size is 8/10 of whole size,
    // And offset will be 1/10 of whole size
    else
    {
      FrameSize.width    = ObjectSize.width * 4 / 5;
      Offset.width       = ObjectSize.width / 10;
      StartTimePos.width = Offset.width;
      EndTimePos.width   = Offset.width + FrameSize.width - 2 * FontSize;
      MaxValuePos.width  = Offset.width - 2 * FontSize;
      MinValuePos.width  = Offset.width - 2 * FontSize;
      TitlePos.width     = Offset.width;
    }
  }

  // Purpose   : TO show the value at clicked
  // Argument  : Mouse event that has the clicked point
  // Return    : void
  public void getValue( MouseEvent e )
  {
    // Get clicked X position
    int autoX = e.getX();

    // get data array
    double[] autoDatas = ShowPortData.getDataY();
    if( autoDatas == null )
      return;

    // Get datas
    PortInfomation autoPort = ShowPortData.getPortInfo();
    double autoStart    = autoPort.getStartTime();
    double autoEnd      = autoPort.getEndTime();
    double autoSampling = autoPort.getSamplingTime() / 1000;
    double autoTrigger  = autoPort.getTriggerTime();

    double autoTimeWidth = autoEnd - autoStart;
    final double autoWidthRatio = FrameSize.width / autoTimeWidth;

    // If clicked outside of data
    if( autoX < StartPosition.width || autoX > EndPosition.width )
      return;

    // Get graphics
    Graphics autoGraphics = getGraphics();

    // Change font size and color to red
    Color autoOld = autoGraphics.getColor();
    autoGraphics.setColor( Color.red );
    Font autoOldFont = autoGraphics.getFont();
    autoGraphics.setFont( new Font( autoOldFont.getName(), Font.PLAIN, this.FontSize ) );

    // translation
    int autoTranlatedX = autoX - Offset.width;

    // Get index of data from the infomation of clicked point
    //
    // The reason why can get by under procidure,
    // because, this is reverse solving that to get X position from index
    //
    // From this.paint( Graphics g )
    // {
    // ...
    //   x1 = i     * autoSampling * autoWidthRatio + autoXOffset;
    // ...
    //
    // Where, x1 is the position of X, i is index.
    // We can also say
    //
    // i = ( x1 - autoXOffset ) / autoSampling / autoWidthRatio
    //   and
    // autoWidthRatio = autoFrameSize.width / autoTimeWidth
    //
    // therefore,
    // i = ( x1 - autoXOffset ) * autoTimeWidth / autoFrameSize.width / autoSampling

    double autoXOffset = 0;
    if( autoTrigger > autoStart )
      autoXOffset = autoTrigger - autoStart;

    int autoDataIndex = (int)( ( autoTranlatedX - autoXOffset ) /
                              autoWidthRatio / autoSampling );

    // Number format
    DecimalFormat autoDataFormat = new DecimalFormat();
    autoDataFormat.setMaximumFractionDigits( 4 );

    // Get data
    double autoData = ShowPortData.getDataY()[ autoDataIndex ];

    // Draw line
    autoGraphics.drawLine( autoX, Offset.height,
                           autoX, Offset.height + FrameSize.height );

    // Show data
    autoGraphics.drawString( autoDataFormat.format( autoData ), autoX, Offset.height );
	// modified by T. Kobayashi 2006/07/06
	// draw x value as well as y value
	autoData = autoStart + autoTranlatedX / autoWidthRatio;
	autoGraphics.drawString( autoDataFormat.format( autoData ), autoX, Offset.height + FrameSize.height );

    // Reset color and font
    autoGraphics.setColor( autoOld );
    autoGraphics.setFont( autoOldFont );
  }


  // Purpose   : Over written paint method
  // Argument  : Component graphics object
  // Return    : void
  public void paint( Graphics g )
  {
    super.paint(g);

    // Set infomation position
    this.setInfomationPosition();

    // Get port infomation
    PortInfomation autoPortInfo = ShowPortData.getPortInfo();

    // Get basic data
    double autoStart          = autoPortInfo.getStartTime();
    double autoEnd            = autoPortInfo.getEndTime();
    double autoTimeWidth      = autoEnd - autoStart;
    // Change unit to milli second
    double autoSampling       = autoPortInfo.getSamplingTime() / 1000.0;
    double autoTrigger        = autoPortInfo.getTriggerTime();
    double autoMax            = autoPortInfo.getMaxValue();
    double autoMin            = autoPortInfo.getMinValue();
    double autoDrawMax        = autoPortInfo.getMaxDrawValue();
    double autoDrawMin        = autoPortInfo.getMinDrawValue();
    double autoValueRange     = autoMax - autoMin;
    double autoDrawValueRange = autoDrawMax - autoDrawMin;

    //----------------------------------------------//
    /* Show Basic Datas and line                    */
    //----------------------------------------------//

    // First change font
    Font autoOldFont = g.getFont();
    // Create new size font
    g.setFont( new Font( autoOldFont.getName(), Font.PLAIN, FontSize ) );

    // Set value color
    g.setColor( GrpMgr.getData().getValueColor() );

    // Write datas
    g.drawString( String.valueOf( autoStart ),   StartTimePos.width, StartTimePos.height );
    g.drawString( String.valueOf( autoEnd ),     EndTimePos.width,   EndTimePos.height );
    g.drawString( String.valueOf( autoDrawMax ), MaxValuePos.width,  MaxValuePos.height );
    g.drawString( String.valueOf( autoDrawMin ), MinValuePos.width,  MinValuePos.height );
    g.drawString( ShowPortData.getTitle(),       TitlePos.width,     TitlePos.height );

    // Draw Graph rect region
    g.setColor( GrpMgr.getData().getFrameColor() );
    g.drawRect( Offset.width,     Offset.height,
                FrameSize.width,  FrameSize.height );

    // Draw cross lines
    g.setColor( GrpMgr.getData().getCrossColor() );
    // Holizntal
    g.drawLine( Offset.width,                   Offset.height + FrameSize.height / 2,
                Offset.width + FrameSize.width, Offset.height + FrameSize.height / 2 );
    // Virtical
    g.drawLine( Offset.width + FrameSize.width / 2, Offset.height,
                Offset.width + FrameSize.width / 2, Offset.height + FrameSize.height );



    //----------------------------------------------//
    /* Draw port data                               */
    //----------------------------------------------//

    // Set data color
    g.setColor( GrpMgr.getData().getDataColor() );

    // If no data ( ex, in the case of "File not found" )
    if( ShowPortData.getDataY() == null )
      g.drawString( ShowPortData.getErrorInfo(),
                    Offset.width  + FrameSize.width  / 2,
                    Offset.height + FrameSize.height / 2 );

    else // If there is a data to draw
    {
      // Virtical and horizental ratios
      final double autoHeightRatio = FrameSize.height / autoDrawValueRange;
      final double autoWidthRatio  = FrameSize.width  / autoTimeWidth;

      double[] autoData = ShowPortData.getDataY();


      // Get offsets
      // Offset is a start of plot
      //         _____________________________
      //         |       /     |              |
      //         |      / \    |              |
      //        here ->--------+--------------|
      //         |          \  |              |
      //         |            \|              |

      double autoXOffset = Offset.width;
      if( autoTrigger > autoStart )
        autoXOffset += (autoTrigger - autoStart) * autoWidthRatio;

      final double autoDrawValueRatio = autoDrawMax / ( autoDrawMax - autoDrawMin );
      double autoYOffset = Offset.height + (int)( FrameSize.height * autoDrawValueRatio );

      // Set Offset
      StartPosition.width  = (int)autoXOffset;
      StartPosition.height = (int)autoYOffset;

/*
*	step was modified for all sampling datas by T. Kobayashi (2006/07/06)
*/
      // Step drawing
/*
      int autoStep = (int)( autoData.length / FrameSize.width ) / 2;
      if( autoStep < 1 )
        autoStep = 1;
*/
      int autoStep = 1;

      // Draw line from ( x1, y1 ) to ( x2, y2 ).
      double x1 = 0, x2 = 0, y1 = 0, y2 = 0;

      int i = 0;
      for( i = 0; i < autoData.length - autoStep; i += autoStep )
      {
        x1 =  i              * autoSampling * autoWidthRatio + autoXOffset;
        x2 = (i + autoStep ) * autoSampling * autoWidthRatio + autoXOffset;

        // In Java graphics, down derection is positive,
        // so y1 and y2 is reverse of data value
        y1 = - autoData[i]            * autoHeightRatio + autoYOffset;
        y2 = - autoData[i + autoStep] * autoHeightRatio + autoYOffset;

        g.drawLine( (int)x1, (int)y1, (int)x2, (int)y2 );
      }

      // Set position of end of plot
      if( i >= autoData.length - autoStep )
        i = autoData.length - autoStep;
      EndPosition.width =  (int)( i * autoSampling * autoWidthRatio  + autoXOffset );
      EndPosition.height = (int)( -   autoData[i]  * autoHeightRatio + autoYOffset );
    }

    // Reset font
    g.setFont( autoOldFont );
  }
}

