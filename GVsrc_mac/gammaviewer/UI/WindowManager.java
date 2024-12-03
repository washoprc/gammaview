/*
    GAMMA Viewer - WindowManager : Windows(Dialogs and frames) handling class
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


    GAMMA Viewer - WindowManager : ウインドウ制御クラス
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



package gammaviewer.UI;

import gammaviewer.ViewerConstants;
import gammaviewer.Data.*;
import gammaviewer.Graphics.*;
import gammaviewer.Net.*;

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;


public class WindowManager implements ViewerConstants
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------

  // Reference to data class
  ViewerDatas Datas = null;

  // Network manager
  NetworkManager NetMgr  = null;
  // Graphics manager
  GraphicsManager GrpMgr = null;

  // Main Frame
  ViewerFrame MainFrame     = null;
  // Control dialog
  ControlDialog Controller  = null;


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public WindowManager( ViewerDatas data )
  {
    Datas = data;

    // Create main frame
    MainFrame   = new ViewerFrame( this );
    // Create a dailog
    Controller  = new ControlDialog( this, MainFrame );
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : To Initialize this class and show frame and dialog
  // Argument  : void
  // Return    : void
  public void init( NetworkManager autoNetMgr, GraphicsManager autoGrpMgr )
  {
    NetMgr = autoNetMgr;
    GrpMgr = autoGrpMgr;

    try
    {
      File autoPositionFile = new File( getData().getHomeDir(), "gvPosition.txt" );
      if( autoPositionFile.canRead() )
      {
        BufferedReader autoReader = new BufferedReader( new FileReader( autoPositionFile ) );

        /* Read data */

        // File contents
        Vector autoContents = getSection( autoReader );

        while( autoContents != null )
        {
          if( autoContents.elementAt(0).equals( "/*------ Dialog  ------*/" ) )
            Controller.unserialize( autoContents );

          else if( autoContents.elementAt(0).equals( "/*------ Frame  ------*/" ) )
            MainFrame.unserialize( autoContents );

          autoContents = getSection( autoReader );
        }
      }
    }
    catch( Exception autoExcpt ){}

    // Add GrpMgr as Canvas
    MainFrame.getContentPane().add( GrpMgr, BorderLayout.CENTER );
    MainFrame.setGrpMgr( GrpMgr );

    // Show only MainFrame and ShotDialog
    Controller.setVisible( true );
    MainFrame.setVisible( true );
  }


  // Purpose   : finalization
  // Argument  : void
  // Return    : void
  public void finalize()
  {
    try
    {
      // this.finalize() is called from ViewerFrame.processWindowEvent(WindowEvent e)
      // That can call only WndMgr.finalize(), so in this.finalize() call all finalize()

      // Call ViewerDatas.finalize()
      Datas.finalize();
      // Call GraphicManager.finalize()
      GrpMgr.finalize();
      // Call NetworkManager.finalize()
      NetMgr.finalize();

      this.Controller.finalize();

      File autoPositionFile = new File( getData().getHomeDir(), "gvPosition.txt" );
      if( autoPositionFile.canWrite() ||
        ( !autoPositionFile.exists() && getData().getHomeDir().canWrite() ) )
      {
        BufferedWriter autoWriter = new BufferedWriter( new FileWriter( autoPositionFile ) );

        /* Write data */

        // Save position and size
        Controller.serialize( autoWriter );
        MainFrame.serialize( autoWriter );

        autoWriter.flush();
        autoWriter.close();
      }
    }
    catch( Exception autoExcpt ) {}
  }


  // Purpose   : Used to get section contents of setting files
  // Argument  : Reader of file
  // Return    : Vector that element is a line of file
  private Vector getSection( BufferedReader autoReader ) throws IOException
  {
    // File content
    String autoContent = autoReader.readLine();

    // If already arrive EOF
    if( autoContent == null )
      return null;

    // Setion buffer
    Vector autoBuffer = new Vector();

    // add contents while not EOF or end of section
    while( autoContent != null && !autoContent.startsWith( "/*------ End ------------*/" ) )
    {
      if( autoContent.length() > 0 )
        autoBuffer.addElement( autoContent );
      autoContent = autoReader.readLine();
    }

    return autoBuffer;
  }


  // Purpose   : synchronize between check button and dialog
  // Argument  : To show or not
  // Return    : void
  public void syncVisibleDialogAndButton( boolean autoShowDialog )
  {
    MainFrame.setSelectedCheckButton( autoShowDialog );
    Controller.setVisible( autoShowDialog );
  }


  // Purpose   : To show message
  // Argument  : Message to show
  // Return    : void
  public void showMessage( String autoMsg )
  {
    MainFrame.showMessage( autoMsg );
    Controller.showMessage( autoMsg );
  }


  // Purpose   : To get login dialog object when login faled
  // Argument  : Host object for infomation shown in the dialog
  // Return    : LoginDialog object
  public LoginDialog getLoginDialog( Host autoHost )
  {
    return new LoginDialog( this.MainFrame, autoHost, Controller.ServerSetPanel );
  }


  // Purpose   : To get data contener
  // Argument  : void
  // Return    : The only data class of this application
  public ViewerDatas getData()  { return Datas; }


  // Purpose   : Called to start downloading
  // Argument  : void
  // Return    : void
  public void startDownloading( ViewerMode autoMode )
  {
    // Save colors
    Controller.saveColor();
    // If still downloading, stop with force
    if( this.isNetThreadAlive() )
      this.stopDownloading();
    else
      NetMgr.start( autoMode );
  }


  // Purpose   : Called to stop downloading
  // Argument  : void
  // Return    : void
  public void stopDownloading()
  {
    NetMgr.stopDownloading();
  }


  // Purpose   : Called to clean up when finishing downloading
  // Argument  : void
  // Return    : void
  public void finishDownloading()
  {
    Controller.finishDownloading();
  }


  // Purpose   : To know whether download thread is alive or not
  // Argument  : void
  // Return    : true if downloand thread is alive, false if not
  public boolean isNetThreadAlive()
  {
    return NetMgr.isNetThreadAlive();
  }
}