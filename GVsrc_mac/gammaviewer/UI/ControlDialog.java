/*
    GAMMA Viewer - ControlDialog : Dialog for settings
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


    GAMMA Viewer - ControlDialog : 設定用ダイアログ
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

import gammaviewer.InfomationException;
import gammaviewer.ViewerConstants;
import gammaviewer.Data.ViewerDatas;
import gammaviewer.Data.ViewerMode;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;


public class ControlDialog extends JDialog
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Referrence to maneger
  WindowManager WndMgr = null;

  // Tabbed component panels
  ControlPanel       ControlShotPanel = null;
  ServerSettingPanel ServerSetPanel   = null;
  PortSettingPanel   PortSetPanel     = null;
  CacheSettingPanel  CacheSetPanel    = null;
  ColorSettingPanel  ColorSetPanel    = null;

  // Tabbed pane
  JTabbedPane TabbedPane = new JTabbedPane();


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------


  public ControlDialog( WindowManager autoWndMgr, JFrame autoMainFrame )
  {
    // Registration of this dialog
    super( autoMainFrame, "コントロールパネル" );

    // Set window manager
    WndMgr = autoWndMgr;

    // Set preffered size
    TabbedPane.setPreferredSize( new Dimension( 430, 430 ) );

    // Create new panels
    ControlShotPanel = new ControlPanel( this );
    ServerSetPanel   = new ServerSettingPanel( this );
    PortSetPanel     = new PortSettingPanel( this );
    CacheSetPanel    = new CacheSettingPanel( this );
    ColorSetPanel    = new ColorSettingPanel( this );

    // Add panel to tab pane.
    TabbedPane.addTab( "ショット"      , ControlShotPanel );
    TabbedPane.addTab( "サーバ設定"    , ServerSetPanel );
    TabbedPane.addTab( "ポート設定"    , PortSetPanel );
    TabbedPane.addTab( "キャッシュ設定" , CacheSetPanel );
    TabbedPane.addTab( "色の設定"      , ColorSetPanel );

    // Add tab pane
    this.getContentPane().add( TabbedPane );

    this.pack();
  }


  // Purpose   : finalization
  // Argument  : void
  // Return    : void
  public void finalize()
  {

  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  // Purpose   : To get data contener
  // Argument  : void
  // Return    : The only data class of this application
  public ViewerDatas getData()  { return WndMgr.getData(); }


  // Purpose   : To show message
  // Argument  : Message to show
  // Return    : void
  public void showMessage( String autoMsg )
  {
    ControlShotPanel.showMessage( autoMsg );
  }


  // Purpose   : Update shot controller port and host list
  // Argument  : void
  // Return    : void
  public void updateSettings()
  {
    // Update panel lists
    ControlShotPanel.updatePortList();
    ControlShotPanel.setHostItem();
  }


  // Purpose   : Called to start downloading
  // Argument  : void
  // Return    : void
  public void startDownloading( ViewerMode autoMode )
  {
    // Change status
    ControlShotPanel.changeDownloadStatus( ViewerConstants.STATUS_BEGIN );
    // Start downloading
    WndMgr.startDownloading( autoMode );
  }


  // Purpose   : Save colors
  // Argument  : void
  // Return    : void
  public void saveColor()
  {
    ColorSetPanel.setColor();
  }



  // Purpose   : Called to stop downloading
  // Argument  : void
  // Return    : void
  public void stopDownloading()
  {
    WndMgr.stopDownloading();
  }


  // Purpose   : Called to clean up when finishing downloading
  // Argument  : void
  // Return    : void
  public void finishDownloading()
  {
    ControlShotPanel.changeDownloadStatus( ViewerConstants.STATUS_END );
  }


  /* Close the window */
  protected void processWindowEvent(WindowEvent e)
  {
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
    {
      WndMgr.syncVisibleDialogAndButton( false );
      setVisible(false);
    }
    super.processWindowEvent(e);
  }


  // Purpose   : Unserialize this class
  // Argument  : Vector that element is formatted data
  // Return    : void
  // NOTE      : Contents format is below
  //
  //             /*--------- xxxx -------------*/          header
  //             kay      value                            key, tab, tab, tab, value
  //                ...
  //             kay      value                            key, tab, tab, tab, value
  //
  public void unserialize( Vector autoContents ) throws IOException
  {
    // String that read
    String autoContent = null;
    // Key and value
    String autoKey = null;
    String autoValue = null;

    // Unknown parameter
    boolean autoUnknownParameter = false;
    Vector autoUnknowKeys = new Vector( 2, 2 );

    int autoX = 0;
    int autoY = 0;
    int autoWidth = 450;
    int autoHeight = 450;

    // Read key and value
    for( int i = 1; i < autoContents.size(); i++ )
    {
      autoContent = autoContents.elementAt(i).toString();
      // Check there is a tab
      if( autoContent.indexOf( "\t" ) < 0 )
      {
        autoUnknowKeys.addElement( autoContent.substring( 0, 15 ) + "..." );
        autoUnknownParameter = true;
      }
      else
      {
        // Get kay and value
        autoKey = autoContent.substring( 0, autoContent.indexOf( "\t" ) );
        autoValue = autoContent.substring( autoContent.lastIndexOf( "\t" ) + 1 );

        // Set datas
        if( autoKey.equals( "XPosition" ) )
          autoX = Integer.parseInt( autoValue );

        else if( autoKey.equals( "YPosition" ) )
          autoY = Integer.parseInt( autoValue );

        else if( autoKey.equals( "Width" ) )
          autoWidth = Integer.parseInt( autoValue );

        else if( autoKey.equals( "Height" ) )
          autoHeight = Integer.parseInt( autoValue );

        else
        {
          autoUnknowKeys.addElement( autoKey );
          autoUnknownParameter = true;
        }
      }
    }

    this.setLocation( autoX, autoY );
    this.setSize( autoWidth, autoHeight );

    if( autoUnknownParameter )
    {
      Object[] autoMessage = new Object[ autoUnknowKeys.size() + 2 ];
      autoMessage[0] = "設定ファイル中でサイズの読み込み時に不明なパラメータを見つけました。";
      autoMessage[1] = "以下の値は無視されます。";
      for( int i = 2; i < autoMessage.length; i++ )
        autoMessage[i] = autoUnknowKeys.elementAt(i-2).toString();

      JOptionPane.showMessageDialog( null, autoMessage, "Infomation", JOptionPane.INFORMATION_MESSAGE );
    }
  }


  // Purpose   : Serialize this class
  // Argument  : BufferedWriter opening setting file
  // Return    : void
  // NOTE      : File format is below
  //
  //             /*--------- xxxx -------------*/          header
  //             kay      value                            key, tab, tab, tab, value
  //                ...
  //             kay      value                            key, tab, tab, tab, value
  //             /*------ End ------------*/               footer
  //
  public void serialize( BufferedWriter autoWriter ) throws IOException
  {
    // Write header
    autoWriter.write( "/*------ Dialog  ------*/" );
    autoWriter.newLine();

    String autoWriteContent = null;

    // Save positions
    autoWriteContent = "XPosition\t\t\t" + getLocation().x;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriteContent = "YPosition\t\t\t" + getLocation().y;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriteContent = "Width\t\t\t" + getSize().width;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriteContent = "Height\t\t\t" + getSize().height;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoWriter.write( "/*------ End ------------*/" );
    autoWriter.newLine();
  }
}