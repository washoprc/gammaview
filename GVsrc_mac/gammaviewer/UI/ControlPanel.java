/*
    GAMMA Viewer - ControlPanel : Panel for shot data input
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


    GAMMA Viewer - ControlPanel : ショットデータ入力用パネル
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

import gammaviewer.*;
import gammaviewer.Data.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.text.Position;
import java.util.Vector;
import java.io.*;


public class ControlPanel extends JPanel  implements ActionListener,
                                                     ListSelectionListener,
                                                     KeyListener
{
  //-----------------------------------------
  /*  Fields  */
  //-----------------------------------------


  // Parent dialog
  ControlDialog ParentDialog = null;


  // Radio buttons
  JRadioButton ShotChoose = new JRadioButton( "ショット選択 モード" , true );
  JRadioButton AutoUpdate = new JRadioButton( "Auto update モード", false );

  // Button
  JButton DrawButton = new JButton( "描画" );

  // Button group
  ButtonGroup RadioButtonGroup = new ButtonGroup();

  // Panels.
  JPanel ButtonPanel      = new JPanel();
  JPanel RadioButtonPanel = new JPanel();
  JPanel InputPanel       = new JPanel();
  JPanel PortListPanel    = new JPanel();

  // Lables
  JLabel ShotLabel   = new JLabel( "ショットナンバーを入力して下さい" );
  JLabel ShotLabel2  = new JLabel( "例 180600,180650-5" );
  JLabel PortLabel   = new JLabel( "ポートを右から選択して下さい" );
  JLabel PortLabel2  = new JLabel( "Ctrl,Shiftキーで複数選択" );
  JLabel HostLabel   = new JLabel( "接続先ホスト" );
  JLabel Message     = new JLabel( "Idling..." );

  // Combo box
  JComboBox HostBox = new JComboBox();

  // List and view port
  JList PortList = new JList();
  JScrollPane PortScrollPane = new JScrollPane();

  // Text fields.
  JTextField ShotTextField = new JTextField( 200 );
  JTextField PortTextField = new JTextField( 300 );

  // Layouts.
  GridBagLayout RootLayout = new GridBagLayout();
  GridLayout InputLayout   = new GridLayout( 0, 1 );


  //-----------------------------------------
  /*  Constructor  */
  //-----------------------------------------

  public ControlPanel( ControlDialog autoParnet )
  {
    // Set parent dialog
    ParentDialog = autoParnet;

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

  /* Initilazation of components */
  private void jbInit() throws Exception
  {
    /* Panel layouting */

    // Root layout and panel.
    this.setLayout( RootLayout );

    // GridBagConstraints
    GridBagConstraints autoConstraints = new GridBagConstraints();
    autoConstraints.weightx      = 1.0;
    autoConstraints.fill         = GridBagConstraints.BOTH;
    autoConstraints.insets       = new Insets(2, 2, 2, 2);
    autoConstraints.ipadx        = autoConstraints.ipady = 2;
    autoConstraints.gridwidth    = GridBagConstraints.RELATIVE;

    // Add panels
    RootLayout.setConstraints( InputPanel, autoConstraints );
    this.add( InputPanel );
    autoConstraints.gridwidth = GridBagConstraints.REMAINDER;
    RootLayout.setConstraints( PortListPanel, autoConstraints );
    this.add( PortListPanel );
    RootLayout.setConstraints( ButtonPanel, autoConstraints );
    this.add( ButtonPanel );

    // Input feilds panel
    InputPanel.setLayout( InputLayout );
    InputPanel.add( AutoUpdate );
    InputPanel.add( ShotChoose );
    InputPanel.add( ShotLabel );
    InputPanel.add( ShotTextField );
    InputPanel.add( ShotLabel2 );
    InputPanel.add( PortLabel );
    InputPanel.add( PortTextField );
    InputPanel.add( PortLabel2 );
    InputPanel.add( HostLabel );
    InputPanel.add( HostBox );
    InputPanel.add( Message );

    // Button
    ButtonPanel.add( DrawButton );

    // RadioButton
    RadioButtonGroup.add( AutoUpdate );
    RadioButtonGroup.add( ShotChoose );

    // port list panel
    PortList.addListSelectionListener( this );
    PortScrollPane.getViewport().setView( PortList );
    PortListPanel.add( PortScrollPane );

    /* Components initialization */

    ViewerDatas autoData = ParentDialog.getData();
    ViewerMode autoCurrentMode = autoData.getCurrentMode();

    // Buttons
    DrawButton.addActionListener( this );
    DrawButton.setActionCommand( "DRAW" );

    // Fields
    ShotTextField.setActionCommand( "DRAW" );
    ShotTextField.addActionListener( this );

    // Radio buttons
    AutoUpdate.setActionCommand( "AUTOUPDATE" );
    ShotChoose.setActionCommand( "SHOTCHOOSE" );
    ShotChoose.addActionListener( this );
    AutoUpdate.addActionListener( this );

    // ComboBox
    HostBox.setActionCommand( "CONNECTHOST" );
    HostBox.addActionListener( this );
    setHostItem();
    HostBox.setSelectedItem( autoCurrentMode.getHostToConnect() );

    // Set current mode. default is CHOOSESHOT
    if( autoCurrentMode.getMode() == ViewerConstants.MODE_CHOOSESHOT )
      ShotChoose.setSelected( true );
    else
      AutoUpdate.setSelected( true );

    // Text field
    PortTextField.setEditable( false );

    // Set list items
    PortList.setListData( autoData.getRegisteredPorts() );

    this.restoreData( autoCurrentMode );

    // Set focus
    DrawButton.requestFocus();

    PortList.setVisibleRowCount( 12 );

    // Add key listener
    this.addKeyListener( this );
    this.ShotLabel.addKeyListener( this );
    this.ShotLabel2.addKeyListener( this );
    this.PortLabel.addKeyListener( this );
    this.PortLabel2.addKeyListener( this );
    this.PortTextField.addKeyListener( this );
    this.PortScrollPane.addKeyListener( this );
    this.PortList.addKeyListener( this );
    this.AutoUpdate.addKeyListener( this );
    this.ShotChoose.addKeyListener( this );
    this.HostBox.addKeyListener( this );
  }


  // Purpose   : Called to stop downloading
  // Argument  : void
  // Return    : void
  private void saveData( ViewerMode autoMode ) throws InfomationException
  {
    // Save datas
    autoMode.setPortList( PortTextField.getText() );
    autoMode.setConnectionHost( (String)HostBox.getSelectedItem() );
    autoMode.setShotList( ShotTextField.getText() );
    // Select portlist off
    PortList.clearSelection();
  }
  private void restoreData( ViewerMode autoMode )
  {
    // Restore data
    ShotTextField.setText( autoMode.getShotListAsString() );
    PortTextField.setText( autoMode.getPortListAsString() );
    HostBox.setSelectedItem( autoMode.getHostToConnect() );

    // Reset values
    Vector autoList = autoMode.getPortList();
    int i = 0, j = 0;

    for( i = 0; i < autoList.size(); i++ )
    {
      for( j = 0; j < PortList.getModel().getSize(); j++ )
      {
        if( PortList.getModel().getElementAt(j).equals( autoList.elementAt(i) ) )
          PortList.addSelectionInterval( j, j );
      }
    }
  }


  // Purpose   : Set host name to combobox (HostBox)
  // Argument  : Vector contenting list of host name
  // Return    : void
  public void setHostItem()
  {
    Object autoSelected = HostBox.getSelectedItem();
    // Update items
    Vector autoList = ParentDialog.getData().getHostList();
    HostBox.removeAllItems();
    for( int i = 0; i < autoList.size(); i++ )
      HostBox.addItem( autoList.elementAt(i) );

    // Reselect
    HostBox.setSelectedItem( autoSelected );
  }


  // Purpose   : Update shot controller port list
  // Argument  : void
  // Return    : void
  public void updatePortList()
  {
    // Get selected values
    Object[] autoSelected = PortList.getSelectedValues();
    // Set new datas
    PortList.setListData( ParentDialog.getData().getRegisteredPorts() );
    int i = 0, j = 0;

    // Reset values
    for( i = 0; i < autoSelected.length; i++ )
    {
      for( j = 0; j < PortList.getModel().getSize(); j++ )
      {
        if( PortList.getModel().getElementAt(j).equals( autoSelected[i] ) )
          PortList.addSelectionInterval( j, j );
      }
    }
  }


  // Purpose   : To show message to label
  // Argument  : String that wanted to show
  // Return    : void
  public void showMessage( String autoMsg )     { Message.setText( autoMsg ); }


  // Purpose   : To syncronize selected values of portlist and port names
  // Argument  : List Selected Event
  // Return    : void
  public void valueChanged( ListSelectionEvent e )
  {
    if( PortList.getModel().getSize() < 1 )
      return;

    // Get selected objects
    Object[] autoSelected = PortList.getSelectedValues();
    StringBuffer autoPortString = new StringBuffer();

    // Add objects to stringbuffer
    if( autoSelected.length > 0 )
    {
      autoPortString.append( autoSelected[0] );
      for( int i = 1; i < autoSelected.length; i++ )
      {
        autoPortString.append( "," );
        autoPortString.append( autoSelected[i] );
      }
    }
    // Set to text field
    PortTextField.setText( autoPortString.toString() );
  }


  // Purpose   : Call to start to download
  // Argument  : void
  // Return    : void
  public void startDrawing() throws InfomationException, NumberFormatException
  {
    // Get current mode
    ViewerMode autoCurrent = ParentDialog.getData().getCurrentMode();

    // Save and restoring data
    this.saveData( autoCurrent );
    this.restoreData( autoCurrent );

    // Check port and shot text field
    if( PortTextField.getText().length() < 1 )
      throw new InfomationException( "ポートが設定されていません" );

    if(    autoCurrent.getMode() == ViewerConstants.MODE_CHOOSESHOT
        && ShotTextField.getText().length() < 1 )
      throw new InfomationException( "ショットナンバーが設定されていません" );

    this.changeDownloadStatus( ViewerConstants.STATUS_BEGIN );

    // Start to downloading
    ParentDialog.startDownloading( autoCurrent );
  }


  // Purpose   : Call when press to stop button to download
  // Argument  : void
  // Return    : void
  public void stopDrawing()
  {
    Message.setText( "ダウンロードを中止しています" );
    ParentDialog.stopDownloading();

    this.changeDownloadStatus( ViewerConstants.STATUS_END );
  }


  // Purpose   : Change label and actioncommand
  // Argument  : void
  // Return    : void
  public void changeDownloadStatus( int autoStatus )
  {
    // Change button label
    if( autoStatus == ViewerConstants.STATUS_BEGIN )
    {
      DrawButton.setText( "中止" );
      DrawButton.setActionCommand( "STOP" );
      ShotTextField.setActionCommand( "STOP" );
    }
    else
    {
      DrawButton.setText( "描画" );
      DrawButton.setActionCommand( "DRAW" );
      ShotTextField.setActionCommand( "DRAW" );
      Message.setText( "Idling" );
    }
  }


  // Purpose   : Action performed
  // Argument  : Action Event
  // Return    : void
  public void actionPerformed( ActionEvent e )
  {
    try
    {
      // Data and mode
      ViewerDatas autoData    = ParentDialog.getData();
      ViewerMode  autoCurrent = autoData.getCurrentMode();

      // Purpose   : When mode is changed to auto update mode,
      //             Do save and restore
      if( e.getActionCommand().equals( "AUTOUPDATE" ) )
      {
        if( autoCurrent.getMode() == ViewerConstants.MODE_CHOOSESHOT )
        {
          // Save datas.
          this.saveData( autoCurrent );
          // Restore new datas.
          autoCurrent = autoData.getMode( ViewerConstants.MODE_AUTOUPDATE );
          autoData.setCurrrentMode( autoCurrent );
          this.restoreData( autoCurrent );
          ShotTextField.setEditable( false );
        }
      }

      // Purpose   : When mode is changed to chot choose mode,
      //             Do save and restore
      else if( e.getActionCommand().equals( "SHOTCHOOSE" ) )
      {
        if( autoCurrent.getMode() == ViewerConstants.MODE_AUTOUPDATE )
        {
          // Save datas.
          this.saveData( autoCurrent );
          // Restore new datas
          autoCurrent = autoData.getMode( ViewerConstants.MODE_CHOOSESHOT );
          autoData.setCurrrentMode( autoCurrent );
          this.restoreData( autoCurrent );
          ShotTextField.setEditable( true );
        }
      }

      // Purpose   : Start to download
      else if( e.getActionCommand().equals( "DRAW" ) )
        this.startDrawing();

      // Purpose   : Stop to download
      else if( e.getActionCommand().equals( "STOP" ) )
        this.stopDrawing();

    }
    catch( InfomationException autoInfoExcpt )
    {
      JOptionPane.showMessageDialog( null, autoInfoExcpt.toString(), "Error", JOptionPane.ERROR_MESSAGE );
      // Reset radio button
      if( ParentDialog.getData().getCurrentMode().getMode() == ViewerConstants.MODE_AUTOUPDATE )
        AutoUpdate.setSelected( true );
      else
        ShotChoose.setSelected( true );
    }
    catch( NumberFormatException autoNmbrExcpt )
    {
      Object[] autoMessage = { "数字であるべきところが数字でありません。",
                               "サンプリングタイム、最大最小値等を見なおしてみてください。" };
      JOptionPane.showMessageDialog( null, autoMessage, "Error", JOptionPane.ERROR_MESSAGE );
      // Reset radio button
      if( ParentDialog.getData().getCurrentMode().getMode() == ViewerConstants.MODE_AUTOUPDATE )
        AutoUpdate.setSelected( true );
      else
        ShotChoose.setSelected( true );
    }
    catch( Exception autoExcpt )
    {
      Object[] autoMessage = { "予期しない例外が発生しました。",
                               "以下の情報をメンテナーに報告していただけると助かります。",
                               "ControlPanel line:529, " + e.toString() };
      JOptionPane.showMessageDialog( null, autoMessage, "連絡をお願いします。", JOptionPane.ERROR_MESSAGE );
      // Reset radio button
      if( ParentDialog.getData().getCurrentMode().getMode() == ViewerConstants.MODE_AUTOUPDATE )
        AutoUpdate.setSelected( true );
      else
        ShotChoose.setSelected( true );
    }
  }


  // Purpose   : Key events
  // Argument  : key Event
  // Return    : void
  public void keyReleased( KeyEvent e ) {}
  public void keyTyped( KeyEvent e )    {}
  public void keyPressed( KeyEvent e )
  {
    try
    {
      if( e.getKeyCode() == KeyEvent.VK_ENTER )
      {
        String autoCommand = DrawButton.getActionCommand();
        if( autoCommand.equals( "DRAW" ) )
          this.startDrawing();

        else if( autoCommand.equals( "STOP" ) )
          this.stopDrawing();
      }
    }
    catch( InfomationException autoInfoExcpt )
    {
      JOptionPane.showMessageDialog( null, autoInfoExcpt.toString(), "Error", JOptionPane.ERROR_MESSAGE );
      // Reset radio button
      if( ParentDialog.getData().getCurrentMode().getMode() == ViewerConstants.MODE_AUTOUPDATE )
        AutoUpdate.setSelected( true );
      else
        ShotChoose.setSelected( true );
    }
    catch( NumberFormatException autoNmbrExcpt )
    {
      Object[] autoMessage = { "数字であるべきところが数字でありません。",
                               "サンプリングタイム、最大最小値等を見なおしてみてください。" };
      JOptionPane.showMessageDialog( null, autoMessage, "Error", JOptionPane.ERROR_MESSAGE );
      // Reset radio button
      if( ParentDialog.getData().getCurrentMode().getMode() == ViewerConstants.MODE_AUTOUPDATE )
        AutoUpdate.setSelected( true );
      else
        ShotChoose.setSelected( true );
    }
    catch( Exception autoExcpt )
    {
      Object[] autoMessage = { "予期しない例外が発生しました。",
                               "以下の情報をメンテナーに報告していただけると助かります。",
                               "ControlPanel line:528, " + e.toString() };
      JOptionPane.showMessageDialog( null, autoMessage, "連絡をお願いします。", JOptionPane.ERROR_MESSAGE );
      // Reset radio button
      if( ParentDialog.getData().getCurrentMode().getMode() == ViewerConstants.MODE_AUTOUPDATE )
        AutoUpdate.setSelected( true );
      else
        ShotChoose.setSelected( true );
    }
  }
}
