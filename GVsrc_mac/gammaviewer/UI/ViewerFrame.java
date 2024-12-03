/*
    GAMMA Viewer - ViewerFrame : Main frame class
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


    GAMMA Viewer - ViewerFrame : メインフレームクラス
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

import gammaviewer.Graphics.GraphicsManager;
import gammaviewer.ViewerConstants;
import gammaviewer.Data.ViewerDatas;

import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ViewerFrame extends JFrame implements ActionListener
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Reference to window manager
  WindowManager WndMgr = null;

  // Reference to graphics manager
  GraphicsManager GrpMgr = null;

  // Canvas pane
  JPanel CanvasPane = new JPanel();

  // Menufile
  JMenuBar ViewerMenuBar = new JMenuBar();
  // "File" menu
  JMenu MenuFile          = new JMenu("ファイル");
  JMenuItem MenuFileExit  = new JMenuItem("終了");
  JMenuItem MenuFilePrint = new JMenuItem("印刷");

  // "Help" menu
  JMenu MenuHelp          = new JMenu("ヘルプ");
  JMenuItem MenuHelpAbout = new JMenuItem("ヘルプ？");

  // "Dialog" menu
  JMenu MenuDialog = new JMenu( "ダイアログ" );
  JCheckBoxMenuItem MenuController = new JCheckBoxMenuItem( "コントローラの表示", true );


  // ToolBar
  JToolBar ViewerToolBar = new JToolBar();

  // Buttons
  JButton ControllerButton = new JButton();
  JButton ReloadButton     = new JButton();
  JButton PrintButton      = new JButton();

  // message
  JLabel statusBar = new JLabel("Get ready");
  BorderLayout borderLayout1 = new BorderLayout();



  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------


  /* Constructor of main frame */
  public ViewerFrame( WindowManager autoWndMgr )
  {
    super( ViewerConstants.APP_NAME + " (BUILD " + ViewerConstants.BUILD + ")" );
    WndMgr = autoWndMgr;

    enableEvents( AWTEvent.WINDOW_EVENT_MASK );
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  //-----------------------------------------
  /*  Methods  */
  //-----------------------------------------


  /* Initialization of components */
  private void jbInit() throws Exception
  {
    // Panel setting
    getContentPane().setLayout( borderLayout1 );
    CanvasPane.setBackground( Color.white );

    // Set menubar items
    // File menu.
    MenuFileExit.setActionCommand( "FILE_EXIT" );
    MenuFileExit.addActionListener( this );
    MenuFileExit.setToolTipText( "データを保存し終了します" );
    MenuFilePrint.setActionCommand( "FILE_PRINT" );
    MenuFilePrint.addActionListener( this );
    MenuFilePrint.setToolTipText( "印刷します" );
    // Dialog menu
    MenuController.setActionCommand( "SHOW_CONTROLLER" );
    MenuController.addActionListener( this );
    MenuController.setToolTipText( "コントロールパネルを表示します" );
    // Help menu
    MenuHelpAbout.setActionCommand( "ABOUT_PROGRAM" );
    MenuHelpAbout.addActionListener( this );
    MenuHelpAbout.setToolTipText( "ヘルプ？を表示します" );

    // Buttons
    ControllerButton.setActionCommand( "SHOW_CONTROLLER_BUTTON" );
    ControllerButton.addActionListener( this );
    ReloadButton.setActionCommand( "RELOAD" );
    ReloadButton.addActionListener( this );
    PrintButton.setActionCommand( "FILE_PRINT" );
    PrintButton.addActionListener( this );

    // Set buttons
    URL ControllerIconURL = this.getImageForcibly( "ControllerIcon.gif" );
    if( ControllerIconURL != null )
      ControllerButton.setIcon( new ImageIcon( ControllerIconURL ) );
    else
      ControllerButton.setText( "コントロールパネル" );
    ControllerButton.setToolTipText( "コントロールパネル表示" );

    URL ReloadIconURL = this.getImageForcibly( "ReloadIcon.gif" );
    if( ReloadIconURL != null )
      ReloadButton.setIcon( new ImageIcon( ReloadIconURL ) );
    else
      ReloadButton.setText( "リロード" );
    ReloadButton.setToolTipText( "データのリロード" );

    URL PrintIconURL = this.getImageForcibly( "PrintIcon.gif" );
    if( PrintIconURL != null )
      PrintButton.setIcon( new ImageIcon( PrintIconURL ) );
    else
      PrintButton.setText( "印刷" );
    PrintButton.setToolTipText( "印刷" );

    // Set icons
    URL SystemIconURL = this.getImageForcibly( "gvIcon.gif" );
    if( SystemIconURL != null )
      this.setIconImage( new ImageIcon( SystemIconURL ).getImage() );

    // Add item to toolbar
    ViewerToolBar.add( ControllerButton );
    ViewerToolBar.addSeparator();
    ViewerToolBar.add( ReloadButton );
    ViewerToolBar.addSeparator();
    ViewerToolBar.add( PrintButton );

    // Add menuitem to menu
    MenuFile.add( MenuFileExit );
    MenuFile.add( MenuFilePrint );
    MenuHelp.add( MenuHelpAbout );
    MenuDialog.add( MenuController );

    // Registrate menubar
    ViewerMenuBar.add( MenuFile );
    ViewerMenuBar.add( MenuDialog );
    ViewerMenuBar.add( MenuHelp );

    // Registrate to JFrame
    this.setJMenuBar( ViewerMenuBar );
    this.getContentPane().add( ViewerToolBar, BorderLayout.NORTH );
    this.getContentPane().add( statusBar, BorderLayout.SOUTH );
    // GrpMgr is added when WndMgr is initialized
//    this.getContentPane().add( GrpMgr, BorderLayout.CENTER );

    // Create bigger frame window.
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    screenSize.height = (int)(4 * screenSize.height / 5);
    screenSize.width = (int)(4 * screenSize.width / 5);
    setSize( screenSize );
    setLocation( 0, 0 );

    validate();
  }


  // Purpose   : To turn on or off the check button on menu
  // Argument  : To check or not
  // Return    : void
  public void setSelectedCheckButton( boolean autoSelected )
  {
    MenuController.setSelected( autoSelected );
  }


  // Purpose   : Set graphics manager
  // Argument  : Graphics manager
  // Return    : void
  public void setGrpMgr( GraphicsManager autoGrpMgr ) { GrpMgr = autoGrpMgr; }


  // Purpose   : Action performing
  // Argument  : ActionEvent
  // Return    : void
  public void actionPerformed( ActionEvent e )
  {
    // Visiblation of control panel
    if( e.getActionCommand().equals( "SHOW_CONTROLLER" ) )
      WndMgr.syncVisibleDialogAndButton( MenuController.isSelected() );
    else if( e.getActionCommand().equals( "SHOW_CONTROLLER_BUTTON" ) )
      WndMgr.syncVisibleDialogAndButton( true );

    // Printing
    else if( e.getActionCommand().equals( "FILE_PRINT" ) )
    {
// WINDOWS //

      PageAttributes autoAttribute = new PageAttributes();
      autoAttribute.setOrientationRequested( PageAttributes.OrientationRequestedType.LANDSCAPE );
      autoAttribute.setColor( PageAttributes.ColorType.COLOR );
      autoAttribute.setOrigin( PageAttributes.OriginType.PRINTABLE );
      PrintJob autoPrintJob = getToolkit().getPrintJob( this, "gvPrint", null, autoAttribute );

// MAC //
//      PrintJob autoPrintJob = getToolkit().getPrintJob( this, "gvPrint", (Properties)null );

      if( autoPrintJob != null )
      {
        // Get print-device graphics object
        Graphics autoDeviceGraphics = autoPrintJob.getGraphics();

        // Printing
        GrpMgr.printToDevice( autoDeviceGraphics, autoPrintJob.getPageDimension() );

        // Cleaning up
        autoDeviceGraphics.dispose();
        autoPrintJob.end();
      }
    }

    /**[ファイル|終了]*/
    else if( e.getActionCommand().equals( "FILE_EXIT" ) )
    {
      processWindowEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
    }

    else if( e.getActionCommand().equals( "RELOAD" ) )
    {
      WndMgr.stopDownloading();
      WndMgr.startDownloading( WndMgr.getData().getCurrentMode() );
    }

    /**[ヘルプ|バージョン情報]*/
    else if( e.getActionCommand() == "ABOUT_PROGRAM" )
    {
      Object[] autoMessage = { "一行ヘルプ",
                               "  わからん？なら近くのしってそうなやつにきけ！",
                               " ",
                               "パージョン情報",
                               "  Name         :　GAMMA Viewer",
                               "  BUILD ID     :" + ViewerConstants.BUILD,
                               "  Copyright 2002-2003 Tsuda Eisukeと協力してくれたゆかいな仲間たち",
                               "  License      :GPL（コピー、再配布とか自由って奴。守ってね）",
                               " ",
                               "協力してくれたゆかいな仲間たちしょうか~い",
                               "  山口裕資   for Diamag filter",
                               "  平井太郎と角田諭宣  for NL filter",
                               "  武村祐一郎と小島有志   for BC,ELECAPotential",
                               "  久保田雄介   for Mac移植の雑用、犠牲（おつ）",
                               "  小林貴之   for らぶらぶ~（はぁと）←（い○もっち、らしいぞ）",
                               "Thank you!" };

      JOptionPane.showMessageDialog( null, autoMessage, "ヘルプ？", JOptionPane.ERROR_MESSAGE );
    }
  }


  // Purpose   : To show message to toolbar
  // Argument  : String that wanted to show
  // Return    : void
  public void showMessage( String msg )     { statusBar.setText( msg ); }


  // Purpose   : To get resource from some location
  // Argument  : resource name
  // Return    : URL to resource or null if not found
  public URL getImageForcibly( String autoImageName )
  {
    URL autoURL = ClassLoader.getSystemResource( autoImageName );
    if( autoURL == null )
      autoURL = ClassLoader.getSystemResource( "images." + autoImageName );
    if( autoURL == null )
      autoURL = ClassLoader.getSystemResource( "gammaviewer.images." + autoImageName );
    return autoURL;
  }


  /* Close the main window*/
  protected void processWindowEvent(WindowEvent e)
  {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
    {
      WndMgr.finalize();
      System.exit(0);
    }
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
    int autoWidth = 600;
    int autoHeight = 500;

    // Read key and value
    for( int i = 1; i < autoContents.size(); i++ )
    {
      autoContent = (String)autoContents.elementAt(i);
      // Check there is a tab
      if( autoContent.indexOf( "\t" ) < 0 )
      {
        autoUnknowKeys.addElement( autoContent );
        autoUnknownParameter = true;
      }
      else
      {
        // Get kay and value
        autoKey = autoContent.substring( 0, autoContent.indexOf( "\t" ) );
        autoValue = autoContent.substring( autoContent.lastIndexOf( "\t" ) + 1 );

        // Shot number
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
    autoWriter.write( "/*------ Frame  ------*/" );
    autoWriter.newLine();

    int autoValue = 0;
    String autoWriteContent = null;

    // Save positions
    autoValue = getLocation().x;
    if( autoValue < 0 )       autoValue = 0;
    if( autoValue > 1240 )    autoValue = 1240;
    autoWriteContent = "XPosition\t\t\t" + autoValue;
    autoWriter.write( autoWriteContent );
    autoWriter.newLine();

    autoValue = getLocation().y;
    if( autoValue < 0 )       autoValue = 0;
    if( autoValue > 1240 )    autoValue = 1240;
    autoWriteContent = "YPosition\t\t\t" + autoValue;
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