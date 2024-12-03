/*
    GAMMA Viewer - GraphicsManager : Management class arround Graphics
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


    GAMMA Viewer - GraphicsManager : グラフィックス関連制御クラス
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

package gammaviewer.Graphics;

import gammaviewer.*;
import gammaviewer.Data.*;
import gammaviewer.UI.*;
import gammaviewer.Net.*;

import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class GraphicsManager extends JPanel
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Reference to data class
  ViewerDatas Datas = null;

  // Managers
  WindowManager  WndMgr = null;
  NetworkManager NetMgr = null;

  Vector GraphicsObjects = new Vector();

  // Mode
  boolean SingleMode = false;

  // Row and Column
  int Row    = 1;
  int Column = 1;

  // Print settings
  boolean Printing = false;


  //-----------------------------------------
  /*  Constructors  */
  //-----------------------------------------

  public GraphicsManager( ViewerDatas autoData )
  {
    Datas = autoData;
    // Set background to white
    setBackground( Color.white );
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : Initialization
  // Argument  : Other managers
  // Return    : void
  public void init( WindowManager w, NetworkManager n )
  {
    WndMgr = w;
    NetMgr = n;
  }


  // Purpose   : finalization
  // Argument  : void
  // Return    : void
  public void finalize()
  {
  }


  // Purpose   : To get data contener
  // Argument  : void
  // Return    : The only data class of this application
  public ViewerDatas getData()      { return Datas; }


  // Purpose   : Call when download is finished
  // Argument  : void
  // Return    : void
  public void draw()
  {
    // Create graph objects
    int i = 0;
    Vector autoShotData = Datas.getShotData();

    // Replace data objects into new ones
    GraphicsObjects.removeAllElements();
    for( i = 0; i < autoShotData.size(); i++ )
      GraphicsObjects.addElement( new GraphObject( this, (PortData)autoShotData.elementAt(i) ) );

    // remove all components
    removeAll();

    // Detarmine row and column
    this.matrixManegiment();

    // Set layout
    setLayout( new GridLayout( Row, Column ) );

    for( i = 0; i < GraphicsObjects.size(); i++ )
      add( (GraphObject)GraphicsObjects.elementAt(i) );

    // Repaint
    this.updateUI();
    this.invalidate();
  }


  // Purpose   : For printing
  // Argument  : Graphics object and page format and index
  // Return    : int
  public void printToDevice( Graphics g, Dimension autoDeviceSize )
  {
    // Imaging size if 8 / 10 of paper size
    Dimension autoPrintableSize = new Dimension( autoDeviceSize.width  * 9 / 10,
                                                 autoDeviceSize.height * 9 / 10 );

    // Translate with offset
    g.translate( autoDeviceSize.width / 20, autoDeviceSize.height / 20 );

    // Offset width and height
    int autoObjectWidth  = autoPrintableSize.width  / Column;
    int autoObjectHeight = autoPrintableSize.height / Row;

    Vector autoShotData = Datas.getShotData();

    // Draw each objects
    Printing = true;
    int i = 0, j = 0;
    for( i = 0; i < Row; i++ )
    {
      for( j = 0; j < Column; j++ )
      {
        // Create new copy of graphics
        Graphics autoTemporary = g.create();
        // Translate for each origin on the paper
        autoTemporary.translate( autoObjectWidth * j, autoObjectHeight * i );
        // And draw
        GraphObject autoObject = new GraphObject( this, (PortData)autoShotData.elementAt( i * Column + j ) );
        autoObject.setObjectSize( new Dimension( autoObjectWidth, autoObjectHeight ) );
        autoObject.lockInfomationPosition();
        autoObject.print( autoTemporary );
      }
    }
    Printing = false;
  }


  // Purpose   : Management of row and column of GraphicsObjects
  // Argument  : void
  // Return    : void
  public void matrixManegiment()
  {
    // Get shot and port sizes
    int autoPortSize = this.getData().getCurrentMode().getPortList().size();
    int autoShotSize = this.getData().getShotData().size() / autoPortSize;
    // The reason why do such a thing to get shot size,
    // in auto update mode
    // getData().getCurrentMode().getShotList().size() == 0

    // This is for extension someday
    int autoRow = autoShotSize;
    int autoColumn = autoPortSize;

    // If Row is just one line
    //
    // such like...
    // ____________________
    // |                  |
    // | {}{}{}{}{}{}{}{} |
    // |                  |
    // --------------------
    if( autoRow <= 1 && autoColumn > 2 )
    {
      int autoTempRow = (int)Math.sqrt( autoColumn );
      int autoTotalObjectAmount = autoShotSize * autoPortSize;
      // If objects can be placed as square matrix
      if( autoTempRow * autoTempRow >= autoTotalObjectAmount )
        Row = Column = autoTempRow;

      // Else if objects can be placed  n x n+1 matrix
      else if( autoTempRow * ( autoTempRow + 1 ) >= autoTotalObjectAmount )
      {
        Row = autoTempRow;
        Column = autoTempRow + 1;
      }

      // Then create n+1 x n+1 matrix
      else
        Row = Column = autoTempRow + 1;
    }

    // If Column is just one line
    //
    // such like...
    // ________
    // |  {}  |
    // |  {}  |
    // |  {}  |
    // |  {}  |
    // --------
    else if( autoColumn <= 1 && autoRow > 2 )
    {
      int autoTempRow = (int)Math.sqrt( autoRow );
      int autoTotalObjectAmount = autoShotSize * autoPortSize;
      // If objects can be placed as square matrix
      if( autoTempRow * autoTempRow >= autoTotalObjectAmount )
        Row = Column = autoTempRow;

      // Else if objects can be placed  n x n+1 matrix
      else if( autoTempRow * ( autoTempRow + 1 ) >= autoTotalObjectAmount )
      {
        Row = autoTempRow;
        Column = autoTempRow + 1;
      }

      // Then create n+1 x n+1 matrix
      else
        Row = Column = autoTempRow + 1;
    }

    // Else
    else
    {
      Row = autoRow;
      Column = autoColumn;
    }
  }


  // Purpose   : Change between single and multi view mode
  // Argument  : JPanel that clicked on
  // Return    : void
  public void changeViewMode( GraphObject ClickedPanel )
  {
    if( SingleMode )
      SingleMode = false;
    else
      SingleMode = true;

    // remove current component
    removeAll();

    if( SingleMode )
      this.matrixManegiment();  // Detarmine row and column
    else
      Row = Column = 1;         // Set row and column

    // Set layout
    setLayout( new GridLayout( Row, Column ) );

    if( SingleMode )
    {
      for( int i = 0; i < GraphicsObjects.size(); i++ )
        add( (GraphObject)GraphicsObjects.elementAt(i) );
    }
    else
      add( ClickedPanel );         // Add clicked panel

    // Repaint
    this.updateUI();
    this.invalidate();
  }
}
