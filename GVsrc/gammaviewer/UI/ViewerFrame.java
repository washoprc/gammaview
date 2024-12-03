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


    GAMMA Viewer - ViewerFrame : �ᥤ��ե졼�९�饹
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
  JMenu MenuFile          = new JMenu("�ե�����");
  JMenuItem MenuFileExit  = new JMenuItem("��λ");
  JMenuItem MenuFilePrint = new JMenuItem("����");

  // "Help" menu
  JMenu MenuHelp          = new JMenu("�إ��");
  JMenuItem MenuHelpAbout = new JMenuItem("�إ�ס�");

  // "Dialog" menu
  JMenu MenuDialog = new JMenu( "��������" );
  JCheckBoxMenuItem MenuController = new JCheckBoxMenuItem( "����ȥ����ɽ��", true );


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
    MenuFileExit.setToolTipText( "�ǡ�������¸����λ���ޤ�" );
    MenuFilePrint.setActionCommand( "FILE_PRINT" );
    MenuFilePrint.addActionListener( this );
    MenuFilePrint.setToolTipText( "�������ޤ�" );
    // Dialog menu
    MenuController.setActionCommand( "SHOW_CONTROLLER" );
    MenuController.addActionListener( this );
    MenuController.setToolTipText( "����ȥ���ѥͥ��ɽ�����ޤ�" );
    // Help menu
    MenuHelpAbout.setActionCommand( "ABOUT_PROGRAM" );
    MenuHelpAbout.addActionListener( this );
    MenuHelpAbout.setToolTipText( "�إ�ס���ɽ�����ޤ�" );

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
      ControllerButton.setText( "����ȥ���ѥͥ�" );
    ControllerButton.setToolTipText( "����ȥ���ѥͥ�ɽ��" );

    URL ReloadIconURL = this.getImageForcibly( "ReloadIcon.gif" );
    if( ReloadIconURL != null )
      ReloadButton.setIcon( new ImageIcon( ReloadIconURL ) );
    else
      ReloadButton.setText( "�����" );
    ReloadButton.setToolTipText( "�ǡ����Υ����" );

    URL PrintIconURL = this.getImageForcibly( "PrintIcon.gif" );
    if( PrintIconURL != null )
      PrintButton.setIcon( new ImageIcon( PrintIconURL ) );
    else
      PrintButton.setText( "����" );
    PrintButton.setToolTipText( "����" );

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

    /**[�ե�����|��λ]*/
    else if( e.getActionCommand().equals( "FILE_EXIT" ) )
    {
      processWindowEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
    }

    else if( e.getActionCommand().equals( "RELOAD" ) )
    {
      WndMgr.stopDownloading();
      WndMgr.startDownloading( WndMgr.getData().getCurrentMode() );
    }

    /**[�إ��|�С���������]*/
    else if( e.getActionCommand() == "ABOUT_PROGRAM" )
    {
      Object[] autoMessage = { "��ԥإ��",
                               "  �狼��󡩤ʤ�᤯�Τ��äƤ����ʤ�Ĥˤ�����",
                               " ",
                               "�ѡ���������",
                               "  Name         :��GAMMA Viewer",
                               "  BUILD ID     :" + ViewerConstants.BUILD,
                               "  Copyright 2002-2003 Tsuda Eisuke�ȶ��Ϥ��Ƥ��줿�椫������֤���",
                               "  License      :GPL�ʥ��ԡ��������ۤȤ���ͳ�ä��ۡ���äƤ͡�",
                               " ",
                               "���Ϥ��Ƥ��줿�椫������֤������礦�������",
                               "  ����͵��   for Diamag filter",
                               "  ʿ����Ϻ�ȳ���͡��  for NL filter",
                               "  ��¼ʹ��Ϻ�Ⱦ���ͭ��   for BC,ELECAPotential",
                               "  ������ͺ��   for Mac�ܿ��λ��ѡ������ʤ��ġ�",
                               "  ���ӵ�Ƿ   for ��֤�֏���ʤϤ��ȡˢ��ʤ�����ä����餷������",
                               "Thank you!" };

      JOptionPane.showMessageDialog( null, autoMessage, "�إ�ס�", JOptionPane.ERROR_MESSAGE );
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
      autoMessage[0] = "����ե�������ǥ��������ɤ߹��߻��������ʥѥ�᡼���򸫤Ĥ��ޤ�����";
      autoMessage[1] = "�ʲ����ͤ�̵�뤵��ޤ���";
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